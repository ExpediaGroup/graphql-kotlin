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

package com.expediagroup.graphql.examples.server.spring.subscriptions

import com.expediagroup.graphql.examples.server.spring.SUBSCRIPTION_ENDPOINT
import com.expediagroup.graphql.server.spring.subscriptions.SubscriptionOperationMessage
import com.expediagroup.graphql.server.spring.subscriptions.SubscriptionOperationMessage.ClientMessages.GQL_CONNECTION_INIT
import com.expediagroup.graphql.server.spring.subscriptions.SubscriptionOperationMessage.ClientMessages.GQL_START
import com.expediagroup.graphql.server.spring.subscriptions.SubscriptionOperationMessage.ServerMessages
import com.expediagroup.graphql.server.types.GraphQLRequest
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestMethodOrder
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.web.reactive.socket.WebSocketSession
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import reactor.test.publisher.TestPublisher
import java.net.URI
import kotlin.random.Random

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = ["graphql.packages=com.expediagroup.graphql.examples.server.spring"]
)
@EnableAutoConfiguration
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.MethodName::class)
@Disabled("unknown race condition is causing random failures when run using GH action, cannot reproduce it locally")
class SimpleSubscriptionIT(@LocalServerPort private var port: Int) {

    private val objectMapper = jacksonObjectMapper()
    private val client = ReactorNettyWebSocketClient()
    private val uri: URI = URI.create("ws://localhost:$port$SUBSCRIPTION_ENDPOINT")

    @Test
    fun `verify singleValueSubscription query`() {
        val query = "singleValueSubscription"
        val output = TestPublisher.create<String>()

        val subscription = client.execute(uri) { session -> executeSubscription(session, query, output) }.subscribe()

        StepVerifier.create(output)
            .expectNext("{\"data\":{\"$query\":1}}")
            .expectComplete()
            .verify()

        subscription.dispose()
    }

    @Test
    fun `verify counter query`() {
        val query = "counter(limit: 2)"
        val numberRegex = "(\\-?[0-9]+)"
        val expectedDataRegex = Regex("\\{\"data\":\\{\"counter\":$numberRegex}}")
        val output = TestPublisher.create<String>()

        val subscription = client.execute(uri) { session -> executeSubscription(session, query, output) }.subscribe()

        StepVerifier.create(output)
            .expectNextMatches { s -> s.matches(expectedDataRegex) }
            .expectNextMatches { s -> s.matches(expectedDataRegex) }
            .expectComplete()
            .verify()

        subscription.dispose()
    }

    @Test
    fun `verify singleValueThenError query`() {
        val query = "singleValueThenError"
        val output = TestPublisher.create<String>()

        val subscription = client.execute(uri) { session -> executeSubscription(session, query, output) }.subscribe()

        StepVerifier.create(output)
            .expectNextCount(1)
            .expectComplete()
            .verify()

        subscription.dispose()
    }

    @Test
    fun `verify flow query`() {
        val query = "flow"
        val output = TestPublisher.create<String>()

        val subscription = client.execute(uri) { session -> executeSubscription(session, query, output) }.subscribe()

        StepVerifier.create(output)
            .expectNext("{\"data\":{\"$query\":1}}")
            .expectNext("{\"data\":{\"$query\":2}}")
            .expectNext("{\"data\":{\"$query\":4}}")
            .expectComplete()
            .verify()

        subscription.dispose()
    }

    @Test
    fun `verify subscriptionContext query without connectionParams`() {
        val query = "subscriptionContext"
        val output = TestPublisher.create<String>()

        val subscription = client.execute(uri) { session -> executeSubscription(session, query, output) }.subscribe()

        StepVerifier.create(output)
            .expectNext("{\"data\":{\"$query\":\"none\"}}")
            .expectNext("{\"data\":{\"$query\":\"value 2\"}}")
            .expectNext("{\"data\":{\"$query\":\"value3\"}}")
            .expectComplete()
            .verify()

        subscription.dispose()
    }

    @Test
    fun `verify subscriptionContext query with connectionParams read by MySubscriptionHooks`() {
        val query = "subscriptionContext"
        val output = TestPublisher.create<String>()

        val subscription = client.execute(uri) { session -> executeSubscription(session, query, output, mapOf("Authorization" to "mytoken")) }.subscribe()

        StepVerifier.create(output)
            .expectNext("{\"data\":{\"$query\":\"mytoken\"}}")
            .expectNext("{\"data\":{\"$query\":\"value 2\"}}")
            .expectNext("{\"data\":{\"$query\":\"value3\"}}")
            .expectComplete()
            .verify()

        subscription.dispose()
    }

    private fun executeSubscription(
        session: WebSocketSession,
        query: String,
        output: TestPublisher<String>,
        initPayload: Any? = null
    ): Mono<Void> {
        val id = Random.nextInt().toString()
        val initMessage = getInitMessage(id, initPayload)
        val startMessage = getStartMessage(query, id)

        return session.send(Flux.just(session.textMessage(initMessage)))
            .then(session.send(Flux.just(session.textMessage(startMessage))))
            .thenMany(
                session.receive()
                    .map { objectMapper.readValue<SubscriptionOperationMessage>(it.payloadAsText) }
                    .doOnNext {
                        if (it.type == ServerMessages.GQL_DATA.type) {
                            val data = objectMapper.writeValueAsString(it.payload)
                            output.next(data)
                        } else if (it.type == ServerMessages.GQL_COMPLETE.type) {
                            output.complete()
                        }
                    }
            )
            .then()
    }

    private fun SubscriptionOperationMessage.toJson() = objectMapper.writeValueAsString(this)
    private fun getInitMessage(id: String, payload: Any?) = SubscriptionOperationMessage(GQL_CONNECTION_INIT.type, id = id, payload = payload).toJson()
    private fun getStartMessage(query: String, id: String): String {
        val request = GraphQLRequest("subscription { $query }")
        return SubscriptionOperationMessage(GQL_START.type, id = id, payload = request).toJson()
    }
}
