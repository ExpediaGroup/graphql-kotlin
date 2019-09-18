package com.expediagroup.graphql.spring

import com.expediagroup.graphql.SchemaGeneratorConfig
import com.expediagroup.graphql.spring.execution.QueryHandler
import com.expediagroup.graphql.spring.execution.SubscriptionHandler
import com.expediagroup.graphql.spring.operations.Subscription
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import graphql.GraphQL
import graphql.schema.GraphQLSchema
import io.mockk.mockk
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

class SubscriptionConfigurationTest {

    private val contextRunner: ReactiveWebApplicationContextRunner = ReactiveWebApplicationContextRunner()
        .withConfiguration(AutoConfigurations.of(GraphQLAutoConfiguration::class.java))

    @Test
    fun `verify subscription auto configuration`() {
        contextRunner.withUserConfiguration(SubscriptionConfiguration::class.java)
            .withPropertyValues("graphql.packages=com.expediagroup.graphql.spring")
            .run { ctx ->
                assertThat(ctx).hasSingleBean(SchemaGeneratorConfig::class.java)
                val schemaGeneratorConfig = ctx.getBean(SchemaGeneratorConfig::class.java)
                assertEquals(listOf("com.expediagroup.graphql.spring"), schemaGeneratorConfig.supportedPackages)

                assertThat(ctx).hasSingleBean(GraphQLSchema::class.java)
                val schema = ctx.getBean(GraphQLSchema::class.java)
                val subscription = schema.subscriptionType
                val fields = subscription.fieldDefinitions
                assertEquals(1, fields.size)
                val tickerSubscription = fields.firstOrNull { it.name == "ticker" }
                assertNotNull(tickerSubscription)

                assertThat(ctx).hasSingleBean(GraphQL::class.java)
                assertThat(ctx).hasSingleBean(QueryHandler::class.java)

                assertThat(ctx).hasSingleBean(SubscriptionHandler::class.java)
                assertThat(ctx).hasSingleBean(WebSocketHandlerAdapter::class.java)
                assertThat(ctx).hasSingleBean(HandlerMapping::class.java)
            }
    }

    @Test
    fun `verify subscription auto configuration backs off in beans are defined by user`() {
        contextRunner.withUserConfiguration(CustomSubscriptionConfiguration::class.java)
            .run { ctx ->
                val customConfiguration = ctx.getBean(CustomSubscriptionConfiguration::class.java)

                assertThat(ctx).hasSingleBean(SchemaGeneratorConfig::class.java)
                assertThat(ctx).hasSingleBean(GraphQLSchema::class.java)

                assertThat(ctx).hasSingleBean(GraphQL::class.java)
                assertThat(ctx).hasSingleBean(QueryHandler::class.java)

                assertThat(ctx).hasSingleBean(SubscriptionHandler::class.java)
                assertThat(ctx).getBean(SubscriptionHandler::class.java)
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
        fun subscription(): Subscription = SimpleSubscription()
    }

    @Configuration
    class CustomSubscriptionConfiguration {

        // in regular apps object mapper will be created by JacksonAutoConfiguration
        @Bean
        fun objectMapper(): ObjectMapper = jacksonObjectMapper()

        @Bean
        fun subscription(): Subscription = SimpleSubscription()

        @Bean
        fun subscriptionHandler(): SubscriptionHandler = mockk()

        @Bean
        fun webSocketHandlerAdapter(): WebSocketHandlerAdapter = mockk()
    }

    class SimpleSubscription : Subscription {
        fun ticker(): Flux<Int> = Flux.range(1, 5)
            .delayElements(Duration.ofMillis(100))
            .map { Random.nextInt() }
    }
}
