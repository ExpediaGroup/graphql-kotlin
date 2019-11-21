/*
 * Copyright 2019 Expedia, Inc
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

package com.expediagroup.graphql.spring

import com.expediagroup.graphql.spring.execution.ApolloSubscriptionProtocolHandler
import com.expediagroup.graphql.spring.execution.SimpleSubscriptionHandler
import com.expediagroup.graphql.spring.execution.SubscriptionHandler
import com.expediagroup.graphql.spring.execution.SubscriptionWebSocketHandler
import com.expediagroup.graphql.spring.operations.Subscription
import com.fasterxml.jackson.databind.ObjectMapper
import graphql.GraphQL
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.HandlerMapping
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter

/**
 * SpringBoot auto-configuration that creates default WebSocket handler for GraphQL subscriptions.
 */
@Configuration
@ConditionalOnBean(Subscription::class)
class SubscriptionAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    fun subscriptionHandler(graphQL: GraphQL): SubscriptionHandler = SimpleSubscriptionHandler(graphQL)

    @Bean
    @ConditionalOnMissingBean
    fun websocketHandlerAdapter(): WebSocketHandlerAdapter = WebSocketHandlerAdapter()

    @Bean
    fun apolloSubscriptionProtocolHandler(config: GraphQLConfigurationProperties, handler: SubscriptionHandler, objectMapper: ObjectMapper) =
        ApolloSubscriptionProtocolHandler(config, handler, objectMapper)

    @Bean
    fun subscriptionWebSocketHandler(handler: ApolloSubscriptionProtocolHandler, objectMapper: ObjectMapper) =
        SubscriptionWebSocketHandler(handler, objectMapper)

    @Bean
    fun subscriptionHandlerMapping(config: GraphQLConfigurationProperties, subscriptionWebSocketHandler: SubscriptionWebSocketHandler): HandlerMapping =
        SimpleUrlHandlerMapping(mapOf(config.subscriptions.endpoint to subscriptionWebSocketHandler), 1)
}
