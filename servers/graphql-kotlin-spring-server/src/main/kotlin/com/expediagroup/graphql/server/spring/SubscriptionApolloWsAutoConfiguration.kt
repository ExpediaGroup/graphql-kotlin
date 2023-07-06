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

import com.expediagroup.graphql.server.execution.GraphQLRequestHandler
import com.expediagroup.graphql.server.spring.subscriptions.ApolloSubscriptionHooks
import com.expediagroup.graphql.server.spring.subscriptions.ApolloSubscriptionProtocolHandler
import com.expediagroup.graphql.server.spring.subscriptions.ApolloSubscriptionWebSocketHandler
import com.expediagroup.graphql.server.spring.subscriptions.DefaultSpringSubscriptionGraphQLContextFactory
import com.expediagroup.graphql.server.spring.subscriptions.SimpleSubscriptionHooks
import com.expediagroup.graphql.server.spring.subscriptions.SpringSubscriptionGraphQLContextFactory
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

@Deprecated("Apollo subscriptions-transport-ws protocol auto configuration is deprecated and will be removed in next major release")
@ConditionalOnProperty(prefix = "graphql.subscriptions", name = ["protocol"], havingValue = "APOLLO_SUBSCRIPTIONS_WS", matchIfMissing = true)
@Configuration
@Import(GraphQLSchemaConfiguration::class)
class SubscriptionApolloWsAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    fun apolloSubscriptionHooks(): ApolloSubscriptionHooks = SimpleSubscriptionHooks()

    @Bean
    @ConditionalOnMissingBean
    fun springSubscriptionGraphQLContextFactory(): SpringSubscriptionGraphQLContextFactory = DefaultSpringSubscriptionGraphQLContextFactory()

    @Bean
    fun apolloSubscriptionProtocolHandler(
        config: GraphQLConfigurationProperties,
        subscriptionContextFactory: SpringSubscriptionGraphQLContextFactory,
        handler: GraphQLRequestHandler,
        objectMapper: ObjectMapper,
        apolloSubscriptionHooks: ApolloSubscriptionHooks
    ) = ApolloSubscriptionProtocolHandler(config, subscriptionContextFactory, handler, objectMapper, apolloSubscriptionHooks)

    @Bean
    fun webSocketHandler(handler: ApolloSubscriptionProtocolHandler, objectMapper: ObjectMapper) =
        ApolloSubscriptionWebSocketHandler(handler, objectMapper)
}
