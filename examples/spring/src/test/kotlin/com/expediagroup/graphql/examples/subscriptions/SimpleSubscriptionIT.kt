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

package com.expediagroup.graphql.examples.subscriptions

import com.expediagroup.graphql.examples.SUBSCRIPTION_ENDPOINT
import com.expediagroup.graphql.spring.model.GraphQLRequest
import com.expediagroup.graphql.spring.model.SubscriptionOperationMessage
import com.expediagroup.graphql.spring.model.SubscriptionOperationMessage.ClientMessages.GQL_START
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.junit.jupiter.api.Test
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.web.reactive.socket.WebSocketSession
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.publisher.ReplayProcessor
import reactor.test.StepVerifier
import java.net.URI

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableAutoConfiguration
class SimpleSubscriptionIT(@LocalServerPort private var port: Int) {

    private val objectMapper = jacksonObjectMapper().registerKotlinModule()

    @Test
    fun `verify singleValueSubscription query`() {
        val query = "singleValueSubscription"
        val subscription = subscribe(query, "1", 1L)

        StepVerifier.create(subscription)
            .expectNext("{\"data\":{\"$query\":1}}")
            .expectComplete()
            .verify()
    }

    @Test
    fun `verify counter query`() {
        val query = "counter"
        val numberRegex = "(\\-?[0-9]+)"
        val expectedDataRegex = Regex("\\{\"data\":\\{\"$query\":$numberRegex}}")
        val subscription = subscribe(query, "2", 2L)

        StepVerifier.create(subscription)
            .expectNextMatches { s -> s.matches(expectedDataRegex) }
            .expectNextMatches { s -> s.matches(expectedDataRegex) }
            .expectComplete()
            .verify()
    }

    @Test
    fun `verify singleValueThenError query`() {
        val query = "singleValueThenError"
        val subscription = subscribe(query, "3", 2L)

        StepVerifier.create(subscription)
            .expectNextCount(2)
            .expectComplete()
            .verify()
    }

    @Test
    fun `verify flow query`() {
        val query = "flow"
        val subscription = subscribe(query, "4", 3L)

        StepVerifier.create(subscription)
            .expectNext("{\"data\":{\"$query\":1}}")
            .expectNext("{\"data\":{\"$query\":2}}")
            .expectNext("{\"data\":{\"$query\":4}}")
            .expectComplete()
            .verify()
    }

    private fun subscribe(query: String, id: String, take: Long): Flux<String> {
        val message = toMessage(query, id)
        val output = ReplayProcessor.create<String>()

        val client = ReactorNettyWebSocketClient()
        val uri = URI.create("ws://localhost:$port$SUBSCRIPTION_ENDPOINT")

        val sessionMono = client.execute(uri) { session -> executeSubscription(session, message, output, take) }
        return output.doOnSubscribe { sessionMono.subscribe() }
    }

    private fun executeSubscription(
        session: WebSocketSession,
        message: String,
        output: ReplayProcessor<String>,
        take: Long
    ): Mono<Void> {
        return session.send(Mono.just(session.textMessage(message)))
            .thenMany(session.receive()
                .map { objectMapper.readValue<SubscriptionOperationMessage>(it.payloadAsText) }
                .map { objectMapper.writeValueAsString(it.payload) }
            )
            .subscribeWith(output)
            .take(take)
            .then()
    }

    private fun toMessage(query: String, id: String): String {
        val request = GraphQLRequest("subscription { $query }")
        val subscriptionOperationMessage = SubscriptionOperationMessage(GQL_START.type, id = id, payload = request)
        return objectMapper.writeValueAsString(subscriptionOperationMessage)
    }
}
