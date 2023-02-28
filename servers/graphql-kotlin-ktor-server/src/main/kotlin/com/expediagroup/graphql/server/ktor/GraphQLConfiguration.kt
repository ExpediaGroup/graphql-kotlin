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

import com.expediagroup.graphql.apq.cache.AutomaticPersistedQueriesCache
import com.expediagroup.graphql.apq.cache.DefaultAutomaticPersistedQueriesCache
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
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import graphql.execution.DataFetcherExceptionHandler
import graphql.execution.ExecutionIdProvider
import graphql.execution.SimpleDataFetcherExceptionHandler
import graphql.execution.instrumentation.Instrumentation
import graphql.execution.preparsed.PreparsedDocumentProvider
import io.ktor.server.config.ApplicationConfig
import io.ktor.server.config.tryGetString
import io.ktor.server.config.tryGetStringList

/**
 * Configuration properties that define supported GraphQL configuration options.
 *
 * ```
 * schema {
 *   packages = listOf("com.example")
 *   queries = listOf() // non-federated schemas, require at least a single query
 *   mutations = listOf()
 *   subscriptions = listOf()
 *   schemaObject = null
 *   hooks = NoopSchemaGeneratorHooks
 *   topLevelNames = TopLevelNames()
 *   federation {
 *     enabled = false
 *     tracing {
 *       enabled = true
 *       debug = false
 *     }
 *   }
 * }
 * engine {
 *   automaticPersistedQueries {
 *      enabled = false
 *   }
 *   batching {
 *     enabled = true
 *     strategy = SYNC_EXHAUSTION
 *   }
 *   introspection {
 *      enabled = true
 *   }
 *   dataFetcherFactoryProvider = SimpleKotlinDataFetcherFactoryProvider()
 *   dataLoaderRegistryFactory = KotlinDataLoaderRegistryFactory()
 *   exceptionHandler = SimpleDataFetcherExceptionHandler()
 *   executionIdProvider = null
 *   idValueUnboxer = IDValueUnboxer()
 *   instrumentations = emptyList()
 *   preparsedDocumentProvider = null
 * }
 * server {
 *   contextFactory = DefaultKtorGraphQLContextFactory()
 *   jacksonConfiguration = { }
 *   requestParser = KtorGraphQLRequestParser(jacksonObjectMapper())
 * }
 * ```
 */
class GraphQLConfiguration(config: ApplicationConfig) {
    /** Configure schema generation process */
    val schema: SchemaConfiguration = SchemaConfiguration(config)
    fun schema(schemaConfig: SchemaConfiguration.() -> Unit) {
        schema.apply(schemaConfig)
    }

    /** Configure GraphQL engine that will be processing the requests */
    val engine: EngineConfiguration = EngineConfiguration(config)
    fun engine(engineConfig: EngineConfiguration.() -> Unit) {
        engine.apply(engineConfig)
    }

    /** Configure GraphQL server */
    val server: ServerConfiguration = ServerConfiguration(config)
    fun server(serverConfig: ServerConfiguration.() -> Unit) {
        server.apply(serverConfig)
    }

    /**
     * Configuration properties that control schema generation process.
     */
    class SchemaConfiguration(config: ApplicationConfig) {
        /** List of supported packages that can contain GraphQL schema type definitions */
        var packages: List<String>? = config.tryGetStringList("graphql.schema.packages")
        /** List of GraphQL queries supported by this server */
        var queries: List<Query> = emptyList()
        /** List of GraphQL mutations supported by this server */
        var mutations: List<Mutation> = emptyList()
        // TODO support subscriptions
//        /** List of GraphQL subscriptions supported by this server */
//        var subscriptions: List<Subscription> = emptyList()
        /** GraphQL schema object with any custom directives */
        var schemaObject: Schema? = null
        /** The names of the top level objects in the schema, defaults to Query, Mutation and Subscription */
        var topLevelNames: TopLevelNames = TopLevelNames()
        /** Custom hooks that will be used when generating the schema */
        var hooks: SchemaGeneratorHooks = NoopSchemaGeneratorHooks
        /** Apollo Federation configuration */
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
        fun tracing(tracingConfig: FederationTracingConfiguration.() -> Unit) {
            tracing.apply(tracingConfig)
        }
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

    /** Configuration of a GraphQL engine */
    class EngineConfiguration(config: ApplicationConfig) {
        /**
         * Configuration for automatic persisted queries support.
         *
         * Warning: If you need custom preparsed document provider, do not configure APQ settings.
         */
        val automaticPersistedQueries: AutomaticPersistedQueriesConfiguration = AutomaticPersistedQueriesConfiguration(config)
        fun automaticPersistedQueries(apqConfig: AutomaticPersistedQueriesConfiguration.() -> Unit) {
            automaticPersistedQueries.apply(apqConfig)
        }
        /** Automatic batching configuration */
        var batching: BatchingConfiguration = BatchingConfiguration(config)
        fun batching(batchingConfig: BatchingConfiguration.() -> Unit) {
            batching.apply(batchingConfig)
        }
        /** Introspection configuration */
        val introspection: IntrospectionConfiguration = IntrospectionConfiguration(config)
        fun introspection(introspectionConfig: IntrospectionConfiguration.() -> Unit) {
            introspection.apply(introspectionConfig)
        }
        /** Factory for creating function and property data fetcher factories. */
        var dataFetcherFactoryProvider: KotlinDataFetcherFactoryProvider = SimpleKotlinDataFetcherFactoryProvider()
        /** Factory for creating data loader registry */
        var dataLoaderRegistryFactory: KotlinDataLoaderRegistryFactory = KotlinDataLoaderRegistryFactory()
        /** GraphQL exception handler */
        var exceptionHandler: DataFetcherExceptionHandler = SimpleDataFetcherExceptionHandler()
        /** Execution ID provider */
        var executionIdProvider: ExecutionIdProvider? = null
        /** ID value class unboxer */
        var idValueUnboxer: IDValueUnboxer = IDValueUnboxer()
        /** List of instrumentations */
        var instrumentations: List<Instrumentation> = emptyList()
        /**
         * Preparsed document provider that allows for safe listing and/or document caching.
         *
         * Warning: If using APQ auto configuration settings, preparsed document provider should not be set.
         */
        var preparsedDocumentProvider: PreparsedDocumentProvider? = null
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

    /**
     * Configuration for setting up automatic persisted query support.
     */
    class AutomaticPersistedQueriesConfiguration(config: ApplicationConfig) {
        /** Boolean flag to enable or disable Automatic Persisted Queries. */
        var enabled: Boolean = config.tryGetString("graphql.engine.automaticPersistedQueries.enabled").toBoolean()
        /** APQ query cache */
        var cache: AutomaticPersistedQueriesCache = DefaultAutomaticPersistedQueriesCache()
    }

    /** Configuration for configuring GraphQL server */
    class ServerConfiguration(config: ApplicationConfig) {
        // TODO support custom servers/request handlers
        /** Custom GraphQL context factory */
        var contextFactory: KtorGraphQLContextFactory = DefaultKtorGraphQLContextFactory()
        /** Custom Jackson ObjectMapper configuration */
        var jacksonConfiguration: ObjectMapper.() -> Unit = {}
        /** Custom request parser */
        var requestParser: KtorGraphQLRequestParser = KtorGraphQLRequestParser(jacksonObjectMapper().apply(jacksonConfiguration))
    }
}

private fun String?.toBatchingStrategy(): GraphQLConfiguration.BatchingStrategy =
    GraphQLConfiguration.BatchingStrategy.values().firstOrNull { strategy -> strategy.name == this } ?: GraphQLConfiguration.BatchingStrategy.LEVEL_DISPATCHED
