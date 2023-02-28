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
import com.expediagroup.graphql.apq.provider.AutomaticPersistedQueriesProvider
import com.expediagroup.graphql.dataloader.instrumentation.level.DataLoaderLevelDispatchedInstrumentation
import com.expediagroup.graphql.dataloader.instrumentation.syncexhaustion.DataLoaderSyncExecutionExhaustedInstrumentation
import com.expediagroup.graphql.generator.SchemaGeneratorConfig
import com.expediagroup.graphql.generator.TopLevelObject
import com.expediagroup.graphql.generator.execution.FlowSubscriptionExecutionStrategy
import com.expediagroup.graphql.generator.federation.FederatedSchemaGeneratorConfig
import com.expediagroup.graphql.generator.federation.FederatedSchemaGeneratorHooks
import com.expediagroup.graphql.generator.federation.toFederatedSchema
import com.expediagroup.graphql.generator.toSchema
import com.expediagroup.graphql.server.execution.GraphQLRequestHandler
import graphql.execution.AsyncExecutionStrategy
import graphql.execution.AsyncSerialExecutionStrategy
import graphql.execution.instrumentation.ChainedInstrumentation
import graphql.execution.instrumentation.Instrumentation
import graphql.execution.preparsed.PreparsedDocumentProvider
import graphql.schema.GraphQLSchema
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.BaseApplicationPlugin
import io.ktor.server.response.respond
import io.ktor.util.AttributeKey
import graphql.GraphQL as GraphQLEngine

/**
 * Ktor plugin that creates GraphQL server based on the provided [GraphQLConfiguration].
 *
 * A configuration of the `GraphQL` plugin might look as follows:
 * 1. Configure and install plugin
 *   ```kotlin
 *   install(GraphQL) {
 *      // your schema, engine and server configuration goes here
 *   }
 *   ```
 *
 * 2. Configure GraphQL routes
 *   ```kotlin
 *   routing {
 *      graphQLPostRoute()
 *   }
 *   ```
 *
 * @param config GraphQL configuration
 */
class GraphQL(config: GraphQLConfiguration) {

    val schema: GraphQLSchema = if (config.schema.federation.enabled) {
        val schemaConfig = FederatedSchemaGeneratorConfig(
            supportedPackages = config.schema.packages ?: throw IllegalStateException("Missing required configuration - packages property is required"),
            topLevelNames = config.schema.topLevelNames,
            hooks = config.schema.hooks as? FederatedSchemaGeneratorHooks ?: throw IllegalStateException("Non federated schema generator hooks were specified when generating federated schema"),
            dataFetcherFactoryProvider = config.engine.dataFetcherFactoryProvider,
            introspectionEnabled = config.engine.introspection.enabled
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
            supportedPackages = config.schema.packages ?: throw IllegalStateException("Missing required configuration - packages property is required"),
            topLevelNames = config.schema.topLevelNames,
            hooks = config.schema.hooks,
            dataFetcherFactoryProvider = config.engine.dataFetcherFactoryProvider,
            introspectionEnabled = config.engine.introspection.enabled
        )
        toSchema(
            config = schemaConfig,
            queries = config.schema.queries.toTopLevelObjects(),
            mutations = config.schema.mutations.toTopLevelObjects(),
            subscriptions = emptyList(),
            schemaObject = config.schema.schemaObject?.let { TopLevelObject(it) }
        )
    }

    val engine: GraphQLEngine = GraphQLEngine.newGraphQL(schema)
        .queryExecutionStrategy(AsyncExecutionStrategy(config.engine.exceptionHandler))
        .mutationExecutionStrategy(AsyncSerialExecutionStrategy(config.engine.exceptionHandler))
        .subscriptionExecutionStrategy(FlowSubscriptionExecutionStrategy(config.engine.exceptionHandler))
        .valueUnboxer(config.engine.idValueUnboxer)
        .also { builder ->
            config.engine.executionIdProvider?.let { builder.executionIdProvider(it) }

            var preparsedDocumentProvider: PreparsedDocumentProvider? = config.engine.preparsedDocumentProvider
            if (config.engine.automaticPersistedQueries.enabled) {
                if (preparsedDocumentProvider != null) {
                    throw IllegalStateException("Custom prepared document provider and APQ specified - disable APQ or don't specify the provider")
                } else {
                    preparsedDocumentProvider = AutomaticPersistedQueriesProvider(config.engine.automaticPersistedQueries.cache)
                }
            }
            preparsedDocumentProvider?.let { builder.preparsedDocumentProvider(it) }

            val instrumentations = mutableListOf<Instrumentation>()
            if (config.engine.batching.enabled) {
                builder.doNotAddDefaultInstrumentations()
                instrumentations.add(
                    when (config.engine.batching.strategy) {
                        GraphQLConfiguration.BatchingStrategy.LEVEL_DISPATCHED -> DataLoaderLevelDispatchedInstrumentation()
                        GraphQLConfiguration.BatchingStrategy.SYNC_EXHAUSTION -> DataLoaderSyncExecutionExhaustedInstrumentation()
                    }
                )
            }
            if (config.schema.federation.enabled && config.schema.federation.tracing.enabled) {
                instrumentations.add(FederatedTracingInstrumentation(FederatedTracingInstrumentation.Options(config.schema.federation.tracing.debug)))
            }

            instrumentations.addAll(config.engine.instrumentations)
            builder.instrumentation(ChainedInstrumentation(instrumentations))
        }
        .build()

    // TODO cannot override the request handler/server as it requires access to graphql engine
    val server: KtorGraphQLServer = KtorGraphQLServer(
        requestParser = config.server.requestParser,
        contextFactory = config.server.contextFactory,
        requestHandler = GraphQLRequestHandler(
            graphQL = engine,
            dataLoaderRegistryFactory = config.engine.dataLoaderRegistryFactory
        )
    )

    companion object Plugin : BaseApplicationPlugin<Application, GraphQLConfiguration, GraphQL> {
        override val key: AttributeKey<GraphQL> = AttributeKey("GraphQL")

        override fun install(pipeline: Application, configure: GraphQLConfiguration.() -> Unit): GraphQL {
            val config = GraphQLConfiguration(pipeline.environment.config).apply(configure)
            return GraphQL(config)
        }
    }
}

internal fun List<Any>.toTopLevelObjects(): List<TopLevelObject> = this.map {
    TopLevelObject(it)
}

internal suspend inline fun KtorGraphQLServer.executeRequest(call: ApplicationCall) = try {
    execute(call.request)?.let {
        call.respond(it)
    } ?: call.respond(HttpStatusCode.BadRequest)
} catch (e: UnsupportedOperationException) {
    call.respond(HttpStatusCode.MethodNotAllowed)
} catch (e: Exception) {
    call.respond(HttpStatusCode.BadRequest)
}
