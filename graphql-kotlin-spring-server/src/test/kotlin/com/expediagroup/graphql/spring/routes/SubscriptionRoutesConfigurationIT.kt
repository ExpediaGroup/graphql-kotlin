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

package com.expediagroup.graphql.spring.routes

import com.expediagroup.graphql.spring.REQUEST_PARAM_QUERY
import com.expediagroup.graphql.spring.model.SubscriptionOperationMessage
import com.expediagroup.graphql.spring.operations.Query
import com.expediagroup.graphql.spring.operations.Subscription
import com.expediagroup.graphql.types.GraphQLRequest
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.publisher.ReplayProcessor
import reactor.test.StepVerifier
import java.net.URI

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = [
        "graphql.packages=com.expediagroup.graphql.spring.routes",
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
        val message = SubscriptionOperationMessage(SubscriptionOperationMessage.ClientMessages.GQL_START.type, id = "1", payload = request)
        val output = ReplayProcessor.create<String>()

        val client = ReactorNettyWebSocketClient()
        val uri = URI.create("ws://localhost:$port/foo")

        val sessionMono = client.execute(uri) { session ->
            session.send(Mono.just(session.textMessage(objectMapper.writeValueAsString(message))))
                .thenMany(
                    session.receive()
                        .map { objectMapper.readValue<SubscriptionOperationMessage>(it.payloadAsText) }
                        .map { objectMapper.writeValueAsString(it.payload) }
                )
                .subscribeWith(output)
                .take(1)
                .then()
        }

        StepVerifier.create(output.doOnSubscribe { sessionMono.subscribe() })
            .expectNext("{\"data\":{\"getNumber\":42}}")
            .expectComplete()
            .verify()
    }

    private fun WebTestClient.ResponseSpec.verifyGraphQLRoute(expected: String) = this.expectStatus().isOk
        .expectBody()
        .jsonPath("$.data.hello").isEqualTo(expected)
        .jsonPath("$.errors").doesNotExist()
        .jsonPath("$.extensions").doesNotExist()
}
