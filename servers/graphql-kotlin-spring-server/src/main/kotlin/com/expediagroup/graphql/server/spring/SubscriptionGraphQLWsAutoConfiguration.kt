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
import com.expediagroup.graphql.server.spring.subscriptions.DefaultSpringGraphQLSubscriptionHooks
import com.expediagroup.graphql.server.spring.subscriptions.DefaultSpringSubscriptionGraphQLContextFactory
import com.expediagroup.graphql.server.spring.subscriptions.SpringGraphQLSubscriptionHooks
import com.expediagroup.graphql.server.spring.subscriptions.SpringSubscriptionGraphQLContextFactory
import com.expediagroup.graphql.server.spring.subscriptions.SubscriptionWebSocketHandler
import com.expediagroup.graphql.server.spring.subscriptions.DefaultWebSocketGraphQLRequestParser
import com.expediagroup.graphql.server.spring.subscriptions.SpringGraphQLSubscriptionRequestParser
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

@ConditionalOnProperty(prefix = "graphql.subscriptions", name = ["protocol"], havingValue = "GRAPHQL_WS", matchIfMissing = true)
@Configuration
@Import(GraphQLSchemaConfiguration::class)
class SubscriptionGraphQLWsAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    fun springSubscriptionGraphQLContextFactory(): SpringSubscriptionGraphQLContextFactory = DefaultSpringSubscriptionGraphQLContextFactory()

    // graphql-transport-ws protocol
    @Bean
    @ConditionalOnMissingBean
    fun subscriptionRequestParser(): SpringGraphQLSubscriptionRequestParser = DefaultWebSocketGraphQLRequestParser()

    @Bean
    @ConditionalOnMissingBean
    fun subscriptionHooks(): SpringGraphQLSubscriptionHooks = DefaultSpringGraphQLSubscriptionHooks()

    @Bean
    fun webSocketHandler(
        subscriptionRequestParser: SpringGraphQLSubscriptionRequestParser,
        subscriptionContextFactory: SpringSubscriptionGraphQLContextFactory,
        subscriptionHooks: SpringGraphQLSubscriptionHooks,
        handler: GraphQLRequestHandler,
        objectMapper: ObjectMapper,
        config: GraphQLConfigurationProperties
    ) = SubscriptionWebSocketHandler(
        subscriptionRequestParser,
        subscriptionContextFactory,
        subscriptionHooks,
        handler,
        config.subscriptions.connectionInitTimeout,
        objectMapper
    )
}
