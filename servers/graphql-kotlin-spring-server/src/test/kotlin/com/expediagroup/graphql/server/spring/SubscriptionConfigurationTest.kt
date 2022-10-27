/*
 * Copyright 2022 Expedia, Inc
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

import com.expediagroup.graphql.generator.SchemaGeneratorConfig
import com.expediagroup.graphql.generator.hooks.FlowSubscriptionSchemaGeneratorHooks
import com.expediagroup.graphql.generator.hooks.SchemaGeneratorHooks
import com.expediagroup.graphql.server.execution.GraphQLRequestHandler
import com.expediagroup.graphql.server.operations.Query
import com.expediagroup.graphql.server.operations.Subscription
import com.expediagroup.graphql.server.spring.subscriptions.SpringGraphQLSubscriptionHandler
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import graphql.GraphQL
import graphql.schema.GraphQLSchema
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.boot.autoconfigure.AutoConfigurations
import org.springframework.boot.test.context.runner.ReactiveWebApplicationContextRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.HandlerMapping
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter
import reactor.core.publisher.Flux
import java.time.Duration
import kotlin.random.Random
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class SubscriptionConfigurationTest {

    private val contextRunner: ReactiveWebApplicationContextRunner = ReactiveWebApplicationContextRunner()
        .withConfiguration(AutoConfigurations.of(GraphQLAutoConfiguration::class.java))

    @Test
    fun `verify subscription auto configuration`() {
        contextRunner.withUserConfiguration(SubscriptionConfiguration::class.java)
            .withPropertyValues("graphql.packages=com.expediagroup.graphql.server.spring")
            .run { ctx ->
                assertThat(ctx).hasSingleBean(SchemaGeneratorConfig::class.java)
                val schemaGeneratorConfig = ctx.getBean(SchemaGeneratorConfig::class.java)
                assertEquals(listOf("com.expediagroup.graphql.server.spring"), schemaGeneratorConfig.supportedPackages)
                assertTrue(schemaGeneratorConfig.hooks is FlowSubscriptionSchemaGeneratorHooks)

                assertThat(ctx).hasSingleBean(GraphQLSchema::class.java)
                val schema = ctx.getBean(GraphQLSchema::class.java)
                val subscription = schema.subscriptionType
                val fields = subscription.fieldDefinitions
                assertEquals(1, fields.size)
                val tickerSubscription = fields.firstOrNull { it.name == "ticker" }
                assertNotNull(tickerSubscription)

                assertThat(ctx).hasSingleBean(GraphQL::class.java)
                assertThat(ctx).hasSingleBean(GraphQLRequestHandler::class.java)

                assertThat(ctx).hasSingleBean(SpringGraphQLSubscriptionHandler::class.java)
                assertThat(ctx).hasSingleBean(WebSocketHandlerAdapter::class.java)
                assertThat(ctx).hasSingleBean(HandlerMapping::class.java)
            }
    }

    @Test
    fun `verify subscription auto configuration backs off in beans are defined by user`() {
        contextRunner.withUserConfiguration(CustomSubscriptionConfiguration::class.java)
            .withPropertyValues("graphql.packages=com.expediagroup.graphql.server.spring")
            .run { ctx ->
                val customConfiguration = ctx.getBean(CustomSubscriptionConfiguration::class.java)

                assertThat(ctx).hasSingleBean(SchemaGeneratorHooks::class.java)
                assertThat(ctx).getBean(SchemaGeneratorHooks::class.java)
                    .isSameAs(customConfiguration.customHooks())
                assertThat(ctx).hasSingleBean(SchemaGeneratorConfig::class.java)
                assertThat(ctx).hasSingleBean(GraphQLSchema::class.java)

                assertThat(ctx).hasSingleBean(GraphQL::class.java)
                assertThat(ctx).hasSingleBean(GraphQLRequestHandler::class.java)

                assertThat(ctx).hasSingleBean(SpringGraphQLSubscriptionHandler::class.java)
                assertThat(ctx).getBean(SpringGraphQLSubscriptionHandler::class.java)
                    .isSameAs(customConfiguration.subscriptionHandler())

                assertThat(ctx).hasSingleBean(WebSocketHandlerAdapter::class.java)
                assertThat(ctx).getBean(WebSocketHandlerAdapter::class.java)
                    .isSameAs(customConfiguration.webSocketHandlerAdapter())
                assertThat(ctx).hasSingleBean(HandlerMapping::class.java)
            }
    }

    @Configuration
    class SubscriptionConfiguration {

        // in regular apps object mapper will be created by JacksonAutoConfiguration
        @Bean
        fun objectMapper(): ObjectMapper = jacksonObjectMapper()

        @Bean
        fun query(): Query = SimpleQuery()

        @Bean
        fun subscription(): Subscription = SimpleSubscription()
    }

    @Configuration
    class CustomSubscriptionConfiguration {

        @Bean
        fun customHooks(): SchemaGeneratorHooks = object : FlowSubscriptionSchemaGeneratorHooks() {
            // custom hooks
        }

        // in regular apps object mapper will be created by JacksonAutoConfiguration
        @Bean
        fun objectMapper(): ObjectMapper = jacksonObjectMapper()

        @Bean
        fun query(): Query = SimpleQuery()

        @Bean
        fun subscription(): Subscription = SimpleSubscription()

        @Bean
        fun subscriptionHandler(): SpringGraphQLSubscriptionHandler = mockk {
            every { executeSubscription(any(), any()) } returns flowOf()
        }

        @Bean
        fun webSocketHandlerAdapter(): WebSocketHandlerAdapter = mockk()
    }

    // GraphQL spec requires at least single query to be present as Query type is needed to run introspection queries
    // see: https://github.com/graphql/graphql-spec/issues/490 and https://github.com/graphql/graphql-spec/issues/568
    class SimpleQuery : Query {
        @Suppress("Detekt.FunctionOnlyReturningConstant")
        fun query(): String = "hello!"
    }

    @Suppress("unused")
    class SimpleSubscription : Subscription {
        fun ticker(): Flux<Int> = Flux.range(1, 5)
            .delayElements(Duration.ofMillis(100))
            .map { Random.nextInt() }
    }
}
