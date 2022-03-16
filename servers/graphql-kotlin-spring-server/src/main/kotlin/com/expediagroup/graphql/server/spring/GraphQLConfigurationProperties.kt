/*
 * Copyright 2021 Expedia, Inc
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
import org.springframework.boot.context.properties.ConstructorBinding

/**
 * [ConfigurationProperties] bean that defines supported GraphQL configuration options.
 */
@ConstructorBinding
@ConfigurationProperties("graphql")
data class GraphQLConfigurationProperties(
    /** GraphQL server endpoint, defaults to 'graphql' */
    val endpoint: String = "graphql",
    /** List of supported packages that can contain GraphQL schema type definitions */
    val packages: List<String>,
    val federation: FederationConfigurationProperties = FederationConfigurationProperties(),
    val subscriptions: SubscriptionConfigurationProperties = SubscriptionConfigurationProperties(),
    val playground: PlaygroundConfigurationProperties = PlaygroundConfigurationProperties(),
    val sdl: SDLConfigurationProperties = SDLConfigurationProperties(),
    val introspection: IntrospectionConfigurationProperties = IntrospectionConfigurationProperties(),
    val batching: BatchingConfigurationProperties = BatchingConfigurationProperties()
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
         * Federation tracing config
         */
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
        /** Keep the websocket alive and send a message to the client every interval in ms. Default to not sending messages */
        val keepAliveInterval: Long? = null
    )

    /**
     * Playground configuration properties.
     */
    data class PlaygroundConfigurationProperties(
        /** Boolean flag indicating whether to enabled Prisma Labs Playground GraphQL IDE */
        val enabled: Boolean = true,
        /** Prisma Labs Playground GraphQL IDE endpoint, defaults to 'playground' */
        val endpoint: String = "playground"
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
        /** Boolean flag indicating whether batching is enabled. */
        val enabled: Boolean = false
    )
}
