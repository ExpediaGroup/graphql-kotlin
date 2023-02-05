/*
 * Copyright 2023 Expedia, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.expediagroup.graphql.server.ktor

import com.apollographql.federation.graphqljava.tracing.FederatedTracingInstrumentation
import com.expediagroup.graphql.dataloader.instrumentation.level.DataLoaderLevelDispatchedInstrumentation
import com.expediagroup.graphql.dataloader.instrumentation.syncexhaustion.DataLoaderSyncExecutionExhaustedInstrumentation
import com.expediagroup.graphql.generator.SchemaGeneratorConfig
import com.expediagroup.graphql.generator.TopLevelObject
import com.expediagroup.graphql.generator.execution.FlowSubscriptionExecutionStrategy
import com.expediagroup.graphql.generator.extensions.print
import com.expediagroup.graphql.generator.federation.FederatedSchemaGeneratorConfig
import com.expediagroup.graphql.generator.federation.FederatedSchemaGeneratorHooks
import com.expediagroup.graphql.generator.federation.toFederatedSchema
import com.expediagroup.graphql.generator.toSchema
import com.expediagroup.graphql.server.execution.GraphQLRequestHandler
import graphql.GraphQL
import graphql.execution.AsyncExecutionStrategy
import graphql.execution.AsyncSerialExecutionStrategy
import graphql.execution.instrumentation.ChainedInstrumentation
import graphql.execution.instrumentation.Instrumentation
import graphql.schema.GraphQLSchema
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.jackson.jackson
import io.ktor.server.application.Application
import io.ktor.server.application.BaseApplicationPlugin
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.application.log
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import io.ktor.util.AttributeKey

// TODO config is mutable -> should we store those values in non-mutable fields?
class GraphQLPlugin(config: GraphQLConfiguration) {

    private val schema: GraphQLSchema = if (config.federation.enabled) {
        val schemaConfig = FederatedSchemaGeneratorConfig(
            supportedPackages = config.packages ?: error("Missing required configuration - packages property is required"),
            topLevelNames = config.schema.topLevelNames,
            hooks = config.schema.hooks as? FederatedSchemaGeneratorHooks ?: throw IllegalStateException("Non federated schema generator hooks were specified when generating federated schema"),
            dataFetcherFactoryProvider = config.engine.dataFetcherFactoryProvider,
            introspectionEnabled = config.introspection.enabled
        )
        toFederatedSchema(
            config = schemaConfig,
            queries = config.schema.queries.toTopLevelObjects(),
            mutations = config.schema.mutations.toTopLevelObjects(),
            subscriptions = emptyList(),
            schemaObject = config.schema.schemaObject?.let { TopLevelObject(it) }
        )
    } else {
        val schemaConfig = SchemaGeneratorConfig(
            supportedPackages = config.packages ?: error("Missing required configuration - packages property is required"),
            topLevelNames = config.schema.topLevelNames,
            hooks = config.schema.hooks,
            dataFetcherFactoryProvider = config.engine.dataFetcherFactoryProvider,
            introspectionEnabled = config.introspection.enabled
        )
        toSchema(
            config = schemaConfig,
            queries = config.schema.queries.toTopLevelObjects(),
            mutations = config.schema.mutations.toTopLevelObjects(),
            subscriptions = emptyList(),
            schemaObject = config.schema.schemaObject?.let { TopLevelObject(it) }
        )
    }

    val engine: GraphQL = GraphQL.newGraphQL(schema)
        .queryExecutionStrategy(AsyncExecutionStrategy(config.engine.exceptionHandler))
        .mutationExecutionStrategy(AsyncSerialExecutionStrategy(config.engine.exceptionHandler))
        .subscriptionExecutionStrategy(FlowSubscriptionExecutionStrategy(config.engine.exceptionHandler))
        .valueUnboxer(config.engine.idValueUnboxer)
        .also { builder ->
            config.engine.executionIdProvider?.let { builder.executionIdProvider(it) }
            config.engine.preparsedDocumentProvider?.let { builder.preparsedDocumentProvider(it) }

            val instrumentations = mutableListOf<Instrumentation>()
            if (config.batching.enabled) {
                builder.doNotAddDefaultInstrumentations()
                instrumentations.add(
                    when (config.batching.strategy) {
                        GraphQLConfiguration.BatchingStrategy.LEVEL_DISPATCHED -> DataLoaderLevelDispatchedInstrumentation()
                        GraphQLConfiguration.BatchingStrategy.SYNC_EXHAUSTION -> DataLoaderSyncExecutionExhaustedInstrumentation()
                    }
                )
            }
            if (config.federation.enabled && config.federation.tracing.enabled) {
                instrumentations.add(FederatedTracingInstrumentation(FederatedTracingInstrumentation.Options(config.federation.tracing.debug)))
            }

            instrumentations.addAll(config.engine.instrumentations)
            builder.instrumentation(ChainedInstrumentation(instrumentations))
        }
        .build()

    val server: KtorGraphQLServer = config.server.customServer ?: KtorGraphQLServer(
        requestParser = config.server.requestParser,
        contextFactory = config.server.contextFactory,
        requestHandler = config.server.requestHandler ?: GraphQLRequestHandler(
            graphQL = engine,
            dataLoaderRegistryFactory = config.engine.dataLoaderRegistryFactory
        )
    )

    companion object Plugin : BaseApplicationPlugin<Application, GraphQLConfiguration, GraphQLPlugin> {
        override val key: AttributeKey<GraphQLPlugin> = AttributeKey("GraphQLPlugin")

        override fun install(pipeline: Application, configure: GraphQLConfiguration.() -> Unit): GraphQLPlugin {
            val config = GraphQLConfiguration(pipeline.environment.config).apply(configure)
            val plugin = GraphQLPlugin(config)

            if (config.sdl.print) {
                pipeline.log.info("\n${plugin.schema.print()}")
            }

            // install content negotiation
            // TODO figure out how to support kotlinx-serialization
            pipeline.install(ContentNegotiation) {
                jackson()
            }
            // install routing
            pipeline.routing {
                get(config.endpoint)  {
                    plugin.server.execute(call.request)?.let {
                        call.respond(it)
                    } ?: call.respond(HttpStatusCode.BadRequest)
                }
                post(config.endpoint) {
                    plugin.server.execute(call.request)?.let {
                        call.respond(it)
                    } ?: call.respond(HttpStatusCode.BadRequest)
                }

                if (config.sdl.enabled) {
                    val sdl = plugin.schema.print()
                    get(config.sdl.endpoint) {
                        call.respondText(text = sdl)
                    }
                }
                if (config.graphiql.enabled) {
                    get(config.graphiql.endpoint) {
                        val contextPath = pipeline.environment.rootPath
                        val graphiQL = GraphQLPlugin::class.java.classLoader.getResourceAsStream("graphql-graphiql.html")?.bufferedReader()?.use { reader ->
                            reader.readText()
                                .replace("\${graphQLEndpoint}", if (contextPath.isBlank()) config.endpoint else "$contextPath/${config.endpoint}")
                                .replace("\${subscriptionsEndpoint}", if (contextPath.isBlank()) "subscriptions" else "$contextPath/subscriptions")
//                                ?.replace("\${subscriptionsEndpoint}", if (contextPath.isBlank()) config.subscriptions.endpoint else "$contextPath/${config.subscriptions.endpoint}")
                        } ?: error("Unable to load GraphiQL")
                        call.respondText(graphiQL, ContentType.Text.Html)
                    }
                }
            }
            return plugin
        }
    }
}

internal fun List<Any>.toTopLevelObjects(): List<TopLevelObject> = this.map {
    TopLevelObject(it)
}
