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

package com.expediagroup.graphql.server.spring

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.NestedConfigurationProperty

/**
 * [ConfigurationProperties] bean that defines supported GraphQL configuration options.
 */
@ConfigurationProperties("graphql")
data class GraphQLConfigurationProperties(
    /** GraphQL server endpoint, defaults to 'graphql' */
    val endpoint: String = "graphql",
    /** List of supported packages that can contain GraphQL schema type definitions */
    val packages: List<String>,
    /** Boolean flag indicating whether to print the schema after generator creates it */
    val printSchema: Boolean = false,
    @NestedConfigurationProperty
    val federation: FederationConfigurationProperties = FederationConfigurationProperties(),
    @NestedConfigurationProperty
    val subscriptions: SubscriptionConfigurationProperties = SubscriptionConfigurationProperties(),
    @NestedConfigurationProperty
    val playground: PlaygroundConfigurationProperties = PlaygroundConfigurationProperties(),
    @NestedConfigurationProperty
    val graphiql: GraphiQLConfigurationProperties = GraphiQLConfigurationProperties(),
    @NestedConfigurationProperty
    val sdl: SDLConfigurationProperties = SDLConfigurationProperties(),
    @NestedConfigurationProperty
    val introspection: IntrospectionConfigurationProperties = IntrospectionConfigurationProperties(),
    @NestedConfigurationProperty
    val batching: BatchingConfigurationProperties = BatchingConfigurationProperties(),
    @NestedConfigurationProperty
    val automaticPersistedQueries: AutomaticPersistedQueriesConfigurationProperties = AutomaticPersistedQueriesConfigurationProperties()
) {
    /**
     * Apollo Federation configuration properties.
     */
    data class FederationConfigurationProperties(
        /**
         * Boolean flag indicating whether to generate federated GraphQL model
         */
        val enabled: Boolean = false,

        /**
         * Boolean flag indicating whether we want to generate Federation v2 compatible schema.
         */
        val optInV2: Boolean = true,

        /**
         * Federation tracing config
         */
        @NestedConfigurationProperty
        val tracing: FederationTracingConfigurationProperties = FederationTracingConfigurationProperties()
    )

    /**
     * Apollo Federation tracing configuration properties
     */
    data class FederationTracingConfigurationProperties(
        /**
         * Flag to enable or disable field tracing for the Apollo Gateway.
         * Default is true as this is only used if the parent config is enabled.
         */
        val enabled: Boolean = true,

        /**
         * Flag to enable or disable debug logging
         */
        val debug: Boolean = false
    )

    /**
     * GraphQL subscription configuration properties.
     */
    data class SubscriptionConfigurationProperties(
        /** GraphQL subscriptions endpoint, defaults to 'subscriptions' */
        val endpoint: String = "subscriptions",
        /** **Deprecated** Keep the websocket alive and send a message to the client every interval in ms. Default to not sending messages */
        val keepAliveInterval: Long? = null,
        /** Server timeout between establishing web socket connection and receiving connection-init message. */
        val connectionInitTimeout: Long = 60_000,
        /** WebSocket based subscription protocol */
        val protocol: SubscriptionProtocol = SubscriptionProtocol.GRAPHQL_WS
    )

    /**
     * Playground configuration properties.
     */
    data class PlaygroundConfigurationProperties(
        /** Boolean flag indicating whether to enabled Prisma Labs Playground GraphQL IDE */
        val enabled: Boolean = false,
        /** Prisma Labs Playground GraphQL IDE endpoint, defaults to 'playground' */
        val endpoint: String = "playground"
    )

    /**
     * GraphiQL configuration properties.
     */
    data class GraphiQLConfigurationProperties(
        /** Boolean flag indicating whether to enabled GraphiQL GraphQL IDE */
        val enabled: Boolean = true,
        /** GraphiQL GraphQL IDE endpoint, defaults to 'graphiql' */
        val endpoint: String = "graphiql"
    )

    /**
     * SDL endpoint configuration properties.
     */
    data class SDLConfigurationProperties(
        /** Boolean flag indicating whether SDL endpoint is enabled */
        val enabled: Boolean = true,
        /** GraphQL SDL endpoint */
        val endpoint: String = "sdl"
    )

    /**
     * Introspection configuration properties.
     */
    data class IntrospectionConfigurationProperties(
        /** Boolean flag indicating whether introspection queries are enabled. */
        val enabled: Boolean = true
    )

    /**
     * Batching configuration properties.
     */
    data class BatchingConfigurationProperties(
        /** Boolean flag to enable or disable batching for a set of GraphQL Operations. */
        val enabled: Boolean = false,
        /** configure the [BatchingStrategy] that will be used when batching is enabled for a set of GraphQL Operations. */
        val strategy: BatchingStrategy = BatchingStrategy.LEVEL_DISPATCHED
    )

    data class AutomaticPersistedQueriesConfigurationProperties(
        /** Boolean flag to enable or disable Automatic Persisted Queries. */
        val enabled: Boolean = false
    )
}

// workaround to Spring GraalVM native issue
// when using nested configuration enum classes Spring Boot is unable to bind them correctly

enum class SubscriptionProtocol {
    APOLLO_SUBSCRIPTIONS_WS,
    GRAPHQL_WS
}

/**
 * Approaches for batching transactions of a set of GraphQL Operations.
 */
enum class BatchingStrategy {
    LEVEL_DISPATCHED,
    SYNC_EXHAUSTION
}
