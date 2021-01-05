/*
 * Copyright 2020 Expedia, Inc
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

package com.expediagroup.graphql.spring.execution

import com.expediagroup.graphql.execution.GraphQLContext
import com.expediagroup.graphql.server.operations.Query
import com.expediagroup.graphql.server.operations.Subscription
import com.expediagroup.graphql.spring.model.SubscriptionOperationMessage
import com.expediagroup.graphql.spring.model.SubscriptionOperationMessage.ClientMessages
import com.expediagroup.graphql.spring.model.SubscriptionOperationMessage.ServerMessages
import com.expediagroup.graphql.types.GraphQLRequest
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.junit.jupiter.api.Test
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient
import reactor.core.publisher.Flux
import reactor.kotlin.core.publisher.toMono
import reactor.test.StepVerifier
import reactor.test.publisher.TestPublisher
import java.net.URI
import java.time.Duration
import kotlin.random.Random

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = [
        "graphql.packages=com.expediagroup.graphql.spring.execution"
    ]
)
@EnableAutoConfiguration
class SubscriptionWebSocketHandlerIT(
    @LocalServerPort private var port: Int
) {

    private val objectMapper = jacksonObjectMapper().registerKotlinModule()
    private val client = ReactorNettyWebSocketClient()
    private val uri = URI.create("ws://localhost:$port/subscriptions")

    @Test
    fun `verify subscription`() {
        val request = GraphQLRequest("subscription { characters }")
        val messageId = "1"
        val initMessage = getInitMessage(messageId)
        val startMessage = getStartMessage(messageId, request)
        val dataOutput = TestPublisher.create<String>()

        val response = client.execute(uri) { session ->
            val firstMessage = session.textMessage(initMessage).toMono()
                .concatWith(session.textMessage(startMessage).toMono())

            session.send(firstMessage)
                .thenMany(
                    session.receive()
                        .map { objectMapper.readValue<SubscriptionOperationMessage>(it.payloadAsText) }
                        .doOnNext {
                            if (it.type == ServerMessages.GQL_DATA.type) {
                                val data = objectMapper.writeValueAsString(it.payload)
                                dataOutput.next(data)
                            } else if (it.type == ServerMessages.GQL_COMPLETE.type) {
                                dataOutput.complete()
                            }
                        }
                )
                .then()
        }.subscribe()

        StepVerifier.create(dataOutput)
            .expectSubscription()
            .expectNext("{\"data\":{\"characters\":\"Alice\"}}")
            .expectNext("{\"data\":{\"characters\":\"Bob\"}}")
            .expectNext("{\"data\":{\"characters\":\"Chuck\"}}")
            .expectNext("{\"data\":{\"characters\":\"Dave\"}}")
            .expectNext("{\"data\":{\"characters\":\"Eve\"}}")
            .expectComplete()
            .verify()

        response.dispose()
    }

    @Test
    fun `verify subscription to counter`() {
        val request = GraphQLRequest("subscription { counter }")
        val messageId = "2"
        val initMessage = getInitMessage(messageId)
        val startMessage = getStartMessage(messageId, request)
        val dataOutput = TestPublisher.create<String>()

        val response = client.execute(uri) { session ->
            val firstMessage = session.textMessage(initMessage).toMono()
                .concatWith(session.textMessage(startMessage).toMono())

            session.send(firstMessage)
                .thenMany(
                    session.receive()
                        .map { objectMapper.readValue<SubscriptionOperationMessage>(it.payloadAsText) }
                        .doOnNext {
                            if (it.type == ServerMessages.GQL_DATA.type) {
                                val data = objectMapper.writeValueAsString(it.payload)
                                dataOutput.next(data)
                            } else if (it.type == ServerMessages.GQL_COMPLETE.type) {
                                dataOutput.complete()
                            }
                        }
                )
                .then()
        }.subscribe()

        StepVerifier.create(dataOutput)
            .expectSubscription()
            .expectNext("{\"data\":{\"counter\":1}}")
            .expectNext("{\"data\":{\"counter\":2}}")
            .expectNext("{\"data\":{\"counter\":3}}")
            .expectNext("{\"data\":{\"counter\":4}}")
            .expectNext("{\"data\":{\"counter\":5}}")
            .expectComplete()
            .verify()

        response.dispose()
    }

    @Test
    fun `verify subscription with context`() {
        val request = GraphQLRequest("subscription { ticker }")
        val messageId = "3"
        val initMessage = getInitMessage(messageId)
        val startMessage = getStartMessage(messageId, request)
        val dataOutput = TestPublisher.create<String>()
        val headers = HttpHeaders()
        headers.set("X-Custom-Header", "junit")

        val response = client.execute(uri, headers) { session ->
            val firstMessage = session.textMessage(initMessage).toMono()
                .concatWith(session.textMessage(startMessage).toMono())

            session.send(firstMessage)
                .thenMany(
                    session.receive()
                        .map { objectMapper.readValue<SubscriptionOperationMessage>(it.payloadAsText) }
                        .doOnNext {
                            if (it.type == ServerMessages.GQL_DATA.type) {
                                val data = objectMapper.writeValueAsString(it.payload)
                                dataOutput.next(data)
                            } else if (it.type == ServerMessages.GQL_COMPLETE.type) {
                                dataOutput.complete()
                            }
                        }
                )
                .then()
        }.subscribe()

        StepVerifier.create(dataOutput)
            .expectNextMatches { it.matches("\\{\"data\":\\{\"ticker\":\"junit:-?\\d+\"}}".toRegex()) }
            .expectComplete()
            .verify()

        response.dispose()
    }

    @Configuration
    class TestConfiguration {

        @Bean
        fun query(): Query = SimpleQuery()

        @Bean
        fun subscription(): Subscription = SimpleSubscription()

        @Bean
        fun customContextFactory(): GraphQLContextFactory<SubscriptionContext> = object : GraphQLContextFactory<SubscriptionContext> {
            override suspend fun generateContext(request: ServerHttpRequest, response: ServerHttpResponse): SubscriptionContext = SubscriptionContext(
                value = request.headers.getFirst("X-Custom-Header") ?: "default"
            )
        }
    }

    // GraphQL spec requires at least single query to be present as Query type is needed to run introspection queries
    // see: https://github.com/graphql/graphql-spec/issues/490 and https://github.com/graphql/graphql-spec/issues/568
    class SimpleQuery : Query {
        @Suppress("Detekt.FunctionOnlyReturningConstant")
        fun query(): String = "hello!"
    }

    class SimpleSubscription : Subscription {

        private val characters = listOf("Alice", "Bob", "Chuck", "Dave", "Eve")

        fun characters(): Flux<String> = Flux.fromIterable(characters)

        @Suppress("unused")
        fun counter(): Flux<Int> = Flux.range(1, 5)
            .delayElements(Duration.ofMillis(100))

        @Suppress("unused")
        fun ticker(ctx: SubscriptionContext): Flux<String> = Flux.just("${ctx.value}:${Random.nextInt()}")
    }

    data class SubscriptionContext(val value: String) : GraphQLContext

    private fun SubscriptionOperationMessage.toJson() = objectMapper.writeValueAsString(this)
    private fun getInitMessage(id: String) = SubscriptionOperationMessage(ClientMessages.GQL_CONNECTION_INIT.type, id).toJson()
    private fun getStartMessage(id: String, payload: Any) = SubscriptionOperationMessage(ClientMessages.GQL_START.type, id, payload).toJson()
}
