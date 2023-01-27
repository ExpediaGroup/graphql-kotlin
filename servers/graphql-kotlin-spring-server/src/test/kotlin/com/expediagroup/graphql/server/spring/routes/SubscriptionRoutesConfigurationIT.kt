/*
 * Copyright 2021 Expedia, Inc
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
import com.expediagroup.graphql.server.spring.subscriptions.SubscriptionOperationMessage
import com.expediagroup.graphql.server.spring.subscriptions.SubscriptionOperationMessage.ClientMessages
import com.expediagroup.graphql.server.spring.subscriptions.SubscriptionOperationMessage.ServerMessages
import com.expediagroup.graphql.server.types.GraphQLRequest
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.junit.jupiter.api.Test
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
import reactor.kotlin.core.publisher.toMono
import reactor.test.StepVerifier
import reactor.test.publisher.TestPublisher
import java.net.URI

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = [
        "graphql.packages=com.expediagroup.graphql.server.spring.routes",
        "graphql.endpoint=foo",
        "graphql.subscriptions.endpoint=foo"
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

    @Suppress("unused")
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

    @Test
    fun `verify subscription`() {
        val request = GraphQLRequest("subscription { getNumber }")
        val messageId = "1"
        val initMessage = SubscriptionOperationMessage(ClientMessages.GQL_CONNECTION_INIT.type, id = messageId).toJson()
        val startMessage = SubscriptionOperationMessage(ClientMessages.GQL_START.type, id = messageId, payload = request).toJson()
        val terminateMessage = SubscriptionOperationMessage(ClientMessages.GQL_CONNECTION_TERMINATE.type, id = messageId).toJson()
        val dataOutput = TestPublisher.create<String>()

        val client = ReactorNettyWebSocketClient()
        val uri = URI.create("ws://localhost:$port/foo")

        client.execute(uri) { session ->
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
                .then(session.send(session.textMessage(terminateMessage).toMono()))
        }.subscribe()

        StepVerifier.create(dataOutput)
            .expectNext("{\"data\":{\"getNumber\":42}}")
            .expectComplete()
            .verify()
    }

    private fun WebTestClient.ResponseSpec.verifyGraphQLRoute(expected: String) = this.expectStatus().isOk
        .expectBody()
        .jsonPath("$.data.hello").isEqualTo(expected)
        .jsonPath("$.errors").doesNotExist()
        .jsonPath("$.extensions").doesNotExist()

    private fun SubscriptionOperationMessage.toJson() = objectMapper.writeValueAsString(this)
}
