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

package com.expediagroup.graphql.spring.execution

import com.expediagroup.graphql.annotations.GraphQLContext
import com.expediagroup.graphql.spring.model.GraphQLRequest
import com.expediagroup.graphql.spring.operations.Subscription
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.junit.jupiter.api.Test
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.web.reactive.socket.WebSocketMessage
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.publisher.ReplayProcessor
import reactor.netty.http.client.HttpClient
import reactor.test.StepVerifier
import java.net.URI
import java.time.Duration
import kotlin.random.Random

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = ["graphql.packages=com.expediagroup.graphql.spring.execution"])
@EnableAutoConfiguration
class SubscriptionWebSocketHandlerIT(@LocalServerPort private var port: Int) {

    @Test
    fun `verify subscription`() {
        val request = getRequest("subscription { characters }")
        val output = ReplayProcessor.create<String>()

        val client = ReactorNettyWebSocketClient()
        val uri = URI.create("ws://localhost:$port/subscriptions")

        val sessionMono = client.execute(uri) { session ->
            session.send(Mono.just(session.textMessage(request)))
                .thenMany(session.receive().map(WebSocketMessage::getPayloadAsText))
                .subscribeWith(output)
                .take(5)
                .then()
        }

        StepVerifier.create(output.doOnSubscribe { sessionMono.subscribe() })
            .expectNext("{\"data\":{\"characters\":\"Alice\"}}")
            .expectNext("{\"data\":{\"characters\":\"Bob\"}}")
            .expectNext("{\"data\":{\"characters\":\"Chuck\"}}")
            .expectNext("{\"data\":{\"characters\":\"Dave\"}}")
            .expectNext("{\"data\":{\"characters\":\"Eve\"}}")
            .expectComplete()
            .verify()
    }

    @Test
    fun `verify subscription to counter`() {
        val request = getRequest("subscription { counter }")
        val output = ReplayProcessor.create<String>()

        val client = ReactorNettyWebSocketClient()
        val uri = URI.create("ws://localhost:$port/subscriptions")

        val sessionMono = client.execute(uri) { session ->
            session.send(Mono.just(session.textMessage(request)))
                .thenMany(session.receive().map(WebSocketMessage::getPayloadAsText))
                .subscribeWith(output)
                .take(5)
                .then()
        }

        StepVerifier.create(output.doOnSubscribe { sessionMono.subscribe() })
            .expectNextCount(5)
            .expectComplete()
            .verify()
    }

    @Test
    fun `verify subscription with context`() {
        val request = getRequest("subscription { ticker }")
        val output = ReplayProcessor.create<String>()

        val httpClient = HttpClient.create().headers { it.set("X-Custom-Header", "junit") }
        val client = ReactorNettyWebSocketClient(httpClient)
        val uri = URI.create("ws://localhost:$port/subscriptions")

        val sessionMono = client.execute(uri) { session ->
            session.send(Mono.just(session.textMessage(request)))
                .thenMany(session.receive().map(WebSocketMessage::getPayloadAsText))
                .subscribeWith(output)
                .take(1)
                .then()
        }

        StepVerifier.create(output.doOnSubscribe { sessionMono.subscribe() })
            .expectNextMatches { it.matches("\\{\"data\":\\{\"ticker\":\"junit:-?\\d+\"}}".toRegex()) }
            .expectComplete()
            .verify()
    }

    private fun getRequest(query: String) = jacksonObjectMapper().writeValueAsString(GraphQLRequest(query))

    @Configuration
    class TestConfiguration {
        @Bean
        fun subscription(): Subscription = SimpleSubscription()

        @Bean
        fun customContextFactory(): GraphQLContextFactory<SubscriptionContext> = object : GraphQLContextFactory<SubscriptionContext> {
            override suspend fun generateContext(request: ServerHttpRequest, response: ServerHttpResponse): SubscriptionContext = SubscriptionContext(
                value = request.headers.getFirst("X-Custom-Header") ?: "default"
            )
        }
    }

    class SimpleSubscription : Subscription {

        private val characters = listOf("Alice", "Bob", "Chuck", "Dave", "Eve")

        fun characters(): Flux<String> = Flux.interval(Duration.ofMillis(100))
            .zipWithIterable(characters)
            .map { it.t2 }

        fun counter(): Flux<Int> = Flux.range(1, 5)
            .delayElements(Duration.ofMillis(100))
            .map { Random.nextInt() }

        fun ticker(@GraphQLContext ctx: SubscriptionContext): Flux<String> = Flux.just("${ctx.value}:${Random.nextInt()}")
    }

    data class SubscriptionContext(val value: String)
}
