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

package com.expediagroup.graphql.server.spring.routes

import com.expediagroup.graphql.server.operations.Query
import com.expediagroup.graphql.server.operations.Subscription
import com.expediagroup.graphql.server.spring.execution.REQUEST_PARAM_QUERY
import com.expediagroup.graphql.server.types.GraphQLRequest
import com.expediagroup.graphql.server.types.GraphQLResponse
import com.expediagroup.graphql.server.types.GraphQLSubscriptionMessage
import com.expediagroup.graphql.server.types.SubscriptionMessageComplete
import com.expediagroup.graphql.server.types.SubscriptionMessageConnectionAck
import com.expediagroup.graphql.server.types.SubscriptionMessageConnectionInit
import com.expediagroup.graphql.server.types.SubscriptionMessageNext
import com.expediagroup.graphql.server.types.SubscriptionMessageSubscribe
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Timeout
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient
import reactor.core.publisher.Flux
import reactor.test.StepVerifier
import reactor.test.publisher.TestPublisher
import java.net.URI
import java.time.Duration
import java.util.UUID

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = [
        "graphql.packages=com.expediagroup.graphql.server.spring.routes",
        "graphql.endpoint=foo",
        "graphql.subscriptions.endpoint=bar"
    ]
)
@EnableAutoConfiguration
class SubscriptionRoutesConfigurationIT(
    @Autowired private val testClient: WebTestClient,
    @LocalServerPort private var port: Int
) {

    val objectMapper = jacksonObjectMapper().registerKotlinModule()

    @Configuration
    class TestConfiguration {
        @Bean
        fun query(): Query = SimpleQuery()

        @Bean
        fun subscription(): Subscription = SimpleSubscription()
    }

    class SimpleQuery : Query {
        fun hello(name: String) = "Hello $name!"
    }

    class SimpleSubscription : Subscription {
        fun getNumber(): Flux<Int> = Flux.just(42)
    }

    @Test
    fun `verify POST graphQL request`() {
        val request = GraphQLRequest(
            query = "query helloWorldQuery(\$name: String!) { hello(name: \$name) }",
            variables = mapOf("name" to "JUNIT route"),
            operationName = "helloWorldQuery"
        )

        testClient.post()
            .uri("/foo")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .verifyGraphQLRoute("Hello JUNIT route!")
    }

    @Test
    fun `verify GET graphQL request`() {
        val query = "query { hello(name: \"JUNIT GET\") }"
        testClient.get()
            .uri { builder ->
                builder.path("/foo")
                    .queryParam(REQUEST_PARAM_QUERY, "{query}")
                    .build(query)
            }
            .exchange()
            .verifyGraphQLRoute("Hello JUNIT GET!")
    }

    @Timeout(10)
    @Test
    fun `verify subscription-transport-ws protocol subscription`() {
        val request = GraphQLRequest("subscription { getNumber }")
        val messageId = UUID.randomUUID().toString()
        val initMessage = SubscriptionMessageConnectionInit().toJson()
        val subscribeMessage = SubscriptionMessageSubscribe(id = messageId, payload = request).toJson()

        val dataOutput = TestPublisher.create<GraphQLResponse<*>>()

        val client = ReactorNettyWebSocketClient()
        val uri = URI.create("ws://localhost:$port/bar")

        client.execute(uri) { session ->
            val clientMessages = Flux.just(session.textMessage(initMessage), session.textMessage(subscribeMessage))
                .delayElements(Duration.ofMillis(200))
            var connectionAcked = false

            session.send(clientMessages)
                .thenMany(session.receive())
                .map { objectMapper.readValue<GraphQLSubscriptionMessage>(it.payloadAsText) }
                .doOnNext { msg ->
                    if (connectionAcked && msg is SubscriptionMessageNext) {
                        dataOutput.next(msg.payload)
                    } else if (msg is SubscriptionMessageConnectionAck) {
                        connectionAcked = true
                    } else if (msg is SubscriptionMessageComplete) {
                        dataOutput.complete()
                    } else {
                        throw IllegalStateException("received unexpected message $msg")
                    }
                }
                .then()
        }.subscribe()

        StepVerifier.create(dataOutput)
            .expectNext(GraphQLResponse(data = mapOf("getNumber" to 42)))
            .expectComplete()
            .verify()
    }

    private fun WebTestClient.ResponseSpec.verifyGraphQLRoute(expected: String) = this.expectStatus().isOk
        .expectBody()
        .jsonPath("$.data.hello").isEqualTo(expected)
        .jsonPath("$.errors").doesNotExist()
        .jsonPath("$.extensions").doesNotExist()

    private fun GraphQLSubscriptionMessage.toJson() = objectMapper.writeValueAsString(this)
}
