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

import com.expediagroup.graphql.dataloader.KotlinDataLoaderRegistryFactory
import com.expediagroup.graphql.generator.TopLevelNames
import com.expediagroup.graphql.generator.execution.KotlinDataFetcherFactoryProvider
import com.expediagroup.graphql.generator.execution.SimpleKotlinDataFetcherFactoryProvider
import com.expediagroup.graphql.generator.hooks.NoopSchemaGeneratorHooks
import com.expediagroup.graphql.generator.hooks.SchemaGeneratorHooks
import com.expediagroup.graphql.generator.scalars.IDValueUnboxer
import com.expediagroup.graphql.server.Schema
import com.expediagroup.graphql.server.operations.Mutation
import com.expediagroup.graphql.server.operations.Query
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import graphql.execution.DataFetcherExceptionHandler
import graphql.execution.ExecutionIdProvider
import graphql.execution.SimpleDataFetcherExceptionHandler
import graphql.execution.instrumentation.Instrumentation
import graphql.execution.preparsed.PreparsedDocumentProvider
import io.ktor.server.config.ApplicationConfig
import io.ktor.server.config.tryGetString
import io.ktor.server.config.tryGetStringList

/*
schema {
  packages = listOf("foo")
  queries = listOf(Foo())
  mutations = listOf()
  subscriptions = listOf()
  schemaObject = null
  hooks = NoopSchemaGeneratorHooks
  topLevelNames = TopLevelNames()
  federation {
    enabled = false
    tracing {
      enabled = true
      debug = false
    }
  }
}
engine {
  introspection {
    enabled: true
  }
  batching {
    enabled = true
    strategy = SYNC_EXHAUSTION
  }

  dataFetcherFactoryProvider = SimpleKotlinDataFetcherFactoryProvider()
  dataLoaderRegistryFactory = KotlinDataLoaderRegistryFactory()
  exceptionHandler = SimpleDataFetcherExceptionHandler()
  executionIdProvider = null
  idValueUnboxer = IDValueUnboxer()
  instrumentations = emptyList()
  preparsedDocumentProvider = null
}
server {
  contextFactory = DefaultKtorGraphQLContextFactory()
  requestParser = KtorGraphQLRequestParser(jacksonObjectMapper())
}
routing {
  endpoint = graphql
  subscriptions {
    endpoint = subscriptions
    keepAliveInterval = null
  }
}
tools {
  graphiql {
    enabled = true
    endpoint = "graphiql"
  }
  sdl {
    enabled = true
    endpoint = "sdl"
    printAtStartup = true
  }
}
 */

/**
 * Configuration properties that define supported GraphQL configuration options.
 */
class GraphQLConfiguration(config: ApplicationConfig) {
    val schema: SchemaConfiguration = SchemaConfiguration(config)
    fun schema(schemaConfig: SchemaConfiguration.() -> Unit) {
        schema.apply(schemaConfig)
    }

    val engine: EngineConfiguration = EngineConfiguration(config)
    fun engine(engineConfig: EngineConfiguration.() -> Unit) {
        engine.apply(engineConfig)
    }

    val server: ServerConfiguration = ServerConfiguration()
    fun server(serverConfig: ServerConfiguration.() -> Unit) {
        server.apply(serverConfig)
    }

    val routing: RoutingConfiguration = RoutingConfiguration(config)
    fun routing(routingConfig: RoutingConfiguration.() -> Unit) {
        routing.apply(routingConfig)
    }

    val tools: ToolsConfiguration = ToolsConfiguration(config)
    fun tools(toolsConfig: ToolsConfiguration.() -> Unit) {
        tools.apply(toolsConfig)
    }

    class SchemaConfiguration(config: ApplicationConfig) {
        /** List of supported packages that can contain GraphQL schema type definitions */
        var packages: List<String>? = config.tryGetStringList("graphql.schema.packages")
        var queries: List<Query> = emptyList()
        var mutations: List<Mutation> = emptyList()
//        var subscriptions: List<Subscription> = emptyList()
        var schemaObject: Schema? = null
        var topLevelNames: TopLevelNames = TopLevelNames()
        var hooks: SchemaGeneratorHooks = NoopSchemaGeneratorHooks
        val federation: FederationConfiguration = FederationConfiguration(config)
        fun federation(federationConfig: FederationConfiguration.() -> Unit) {
            federation.apply(federationConfig)
        }
    }

    /**
     * Apollo Federation configuration properties.
     */
    class FederationConfiguration(config: ApplicationConfig) {
        /**
         * Boolean flag indicating whether to generate federated GraphQL model
         */
        var enabled: Boolean = config.tryGetString("graphql.schema.federation.enabled").toBoolean()

        /**
         * Federation tracing config
         */
        var tracing: FederationTracingConfiguration = FederationTracingConfiguration(config)
    }

    /**
     * Apollo Federation tracing configuration properties
     */
    class FederationTracingConfiguration(config: ApplicationConfig) {
        /**
         * Flag to enable or disable field tracing for the Apollo Gateway.
         * Default is true as this is only used if the parent config is enabled.
         */
        var enabled: Boolean = config.tryGetString("graphql.schema.federation.tracing.enabled")?.toBoolean() ?: true

        /**
         * Flag to enable or disable debug logging
         */
        var debug: Boolean = config.tryGetString("graphql.schema.federation.tracing.enabled").toBoolean()
    }

    class EngineConfiguration(config: ApplicationConfig) {
        var introspection: IntrospectionConfiguration = IntrospectionConfiguration(config)
        var batching: BatchingConfiguration = BatchingConfiguration(config)
        var dataFetcherFactoryProvider: KotlinDataFetcherFactoryProvider = SimpleKotlinDataFetcherFactoryProvider()
        var dataLoaderRegistryFactory: KotlinDataLoaderRegistryFactory = KotlinDataLoaderRegistryFactory()
        var exceptionHandler: DataFetcherExceptionHandler = SimpleDataFetcherExceptionHandler()
        var executionIdProvider: ExecutionIdProvider? = null
        var idValueUnboxer: IDValueUnboxer = IDValueUnboxer()
        var instrumentations: List<Instrumentation> = emptyList()
        var preparsedDocumentProvider: PreparsedDocumentProvider? = null
        // TODO support APQ
//        var automaticPersistedQueries: AutomaticPersistedQueriesConfiguration = AutomaticPersistedQueriesConfiguration(config)
    }

    /**
     * Introspection configuration properties.
     */
    class IntrospectionConfiguration(config: ApplicationConfig) {
        /** Boolean flag indicating whether introspection queries are enabled. */
        var enabled: Boolean = config.tryGetString("graphql.engine.introspection.enabled")?.toBoolean() ?: true
    }

    /**
     * Approaches for batching transactions of a set of GraphQL Operations.
     */
    enum class BatchingStrategy { LEVEL_DISPATCHED, SYNC_EXHAUSTION }
    /**
     * Batching configuration properties.
     */
    class BatchingConfiguration(config: ApplicationConfig) {
        /** Boolean flag to enable or disable batching for a set of GraphQL Operations. */
        var enabled: Boolean = config.tryGetString("graphql.engine.batching.enabled").toBoolean()

        /** configure the [BatchingStrategy] that will be used when batching is enabled for a set of GraphQL Operations. */
        var strategy: BatchingStrategy = config.tryGetString("graphql.engine.batching.strategy").toBatchingStrategy()
    }

    class AutomaticPersistedQueriesConfiguration(config: ApplicationConfig) {
        /** Boolean flag to enable or disable Automatic Persisted Queries. */
        var enabled: Boolean = config.tryGetString("graphql.engine.automaticPersistedQueries.enabled").toBoolean()
    }

    class ServerConfiguration {
        // TODO support custom servers/request handlers
        var contextFactory: KtorGraphQLContextFactory = DefaultKtorGraphQLContextFactory()
        var requestParser: KtorGraphQLRequestParser = KtorGraphQLRequestParser(jacksonObjectMapper())
    }

    class RoutingConfiguration(config: ApplicationConfig) {
        /** GraphQL server endpoint, defaults to 'graphql' */
        var endpoint: String = config.tryGetString("graphql.routing.endpoint") ?: "graphql"
        // TODO support subscriptions
        //    var subscriptions: SubscriptionConfiguration = SubscriptionConfiguration(config)
    }

    /**
     * GraphQL subscription configuration properties.
     */
    class SubscriptionConfiguration(config: ApplicationConfig) {
        /** GraphQL subscriptions endpoint, defaults to 'subscriptions' */
        var endpoint: String = config.tryGetString("graphql.routing.subscriptions.endpoint") ?: "subscriptions"

        /** Keep the websocket alive and send a message to the client every interval in ms. Default to not sending messages */
        var keepAliveInterval: Long? = config.tryGetString("graphql.routing.subscriptions.keepAliveInterval")?.toLongOrNull()
    }

    class ToolsConfiguration(config: ApplicationConfig) {
        var graphiql: GraphiQLConfiguration = GraphiQLConfiguration(config)
        var sdl: SDLConfiguration = SDLConfiguration(config)
    }

    /**
     * GraphiQL configuration properties.
     */
    class GraphiQLConfiguration(config: ApplicationConfig) {
        /** Boolean flag indicating whether to enabled GraphiQL GraphQL IDE */
        var enabled: Boolean = config.tryGetString("graphql.tools.graphiql.enabled")?.toBoolean() ?: true

        /** GraphiQL GraphQL IDE endpoint, defaults to 'graphiql' */
        var endpoint: String = config.tryGetString("graphql.tools.graphiql.endpoint") ?: "graphiql"
    }

    /**
     * SDL endpoint configuration properties.
     */
    class SDLConfiguration(config: ApplicationConfig) {
        /** Boolean flag indicating whether SDL endpoint is enabled */
        var enabled: Boolean = config.tryGetString("graphql.tools.sdl.enabled")?.toBoolean() ?: true

        /** GraphQL SDL endpoint */
        var endpoint: String = config.tryGetString("graphql.tools.sdl.endpoint") ?: "sdl"

        /** Boolean flag indicating whether to print the schema after generator creates it */
        var printAtStartup: Boolean = config.tryGetString("graphql.tools.sdl.print").toBoolean()
    }
}

private fun String?.toBatchingStrategy(): GraphQLConfiguration.BatchingStrategy =
    GraphQLConfiguration.BatchingStrategy.values().firstOrNull { strategy -> strategy.name == this } ?: GraphQLConfiguration.BatchingStrategy.LEVEL_DISPATCHED
