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

package com.expediagroup.graphql.server.spring.routes

import com.expediagroup.graphql.generator.extensions.toGraphQLContext
import com.expediagroup.graphql.server.operations.Query
import com.expediagroup.graphql.server.spring.execution.REQUEST_PARAM_OPERATION_NAME
import com.expediagroup.graphql.server.spring.execution.REQUEST_PARAM_QUERY
import com.expediagroup.graphql.server.spring.execution.REQUEST_PARAM_VARIABLES
import com.expediagroup.graphql.server.spring.execution.SpringGraphQLContextFactory
import com.expediagroup.graphql.server.spring.execution.graphQLMediaType
import com.expediagroup.graphql.server.types.GraphQLRequest
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import graphql.GraphQLContext
import graphql.schema.DataFetchingEnvironment
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody
import org.springframework.web.reactive.function.server.ServerRequest

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = [
        "graphql.packages=com.expediagroup.graphql.server.spring.routes",
        "graphql.sdl.enabled=true"
    ]
)
@EnableAutoConfiguration
class RouteConfigurationIT(@Autowired private val testClient: WebTestClient) {

    @Configuration
    class TestConfiguration {
        @Bean
        fun query(): Query = SimpleQuery()

        @Bean
        fun customContextFactory(): SpringGraphQLContextFactory = object : SpringGraphQLContextFactory() {
            override suspend fun generateContext(request: ServerRequest): GraphQLContext =
                mapOf(
                    "value" to (request.headers().firstHeader("X-Custom-Header") ?: "default")
                ).toGraphQLContext()
        }
    }

    class SimpleQuery : Query {
        fun hello(name: String): String = "Hello $name!"
        fun context(env: DataFetchingEnvironment): String = env.graphQlContext.getOrDefault("value", "default")
    }

    val expectedSchema =
        """
        schema {
          query: Query
        }

        "Marks the field, argument, input field or enum value as deprecated"
        directive @deprecated(
            "The reason for the deprecation"
            reason: String = "No longer supported"
          ) on FIELD_DEFINITION | ARGUMENT_DEFINITION | ENUM_VALUE | INPUT_FIELD_DEFINITION

        "Directs the executor to include this field or fragment only when the `if` argument is true"
        directive @include(
            "Included when true."
            if: Boolean!
          ) on FIELD | FRAGMENT_SPREAD | INLINE_FRAGMENT

        "Directs the executor to skip this field or fragment when the `if` argument is true."
        directive @skip(
            "Skipped when true."
            if: Boolean!
          ) on FIELD | FRAGMENT_SPREAD | INLINE_FRAGMENT

        "Exposes a URL that specifies the behaviour of this scalar."
        directive @specifiedBy(
            "The URL that specifies the behaviour of this scalar."
            url: String!
          ) on SCALAR

        type Query {
          context: String!
          hello(name: String!): String!
        }
        """.trimIndent().plus("\n")

    @Test
    fun `verify SDL route`() {
        testClient.get().uri("/sdl")
            .accept(MediaType.TEXT_PLAIN)
            .exchange()
            .expectStatus().isOk
            .expectBody<String>()
            .isEqualTo(expectedSchema)
    }

    @Test
    fun `verify POST graphQL request`() {
        val request = GraphQLRequest(
            query = "query helloWorldQuery(\$name: String!) { hello(name: \$name) }",
            variables = mapOf("name" to "JUNIT route"),
            operationName = "helloWorldQuery"
        )

        testClient.post()
            .uri("/graphql")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .verifyGraphQLRoute("Hello JUNIT route!")
    }

    @Test
    fun `verify GET graphQL request`() {
        val query = """query { hello(name: "JUNIT GET") }"""
        testClient.get()
            .uri { builder ->
                builder.path("/graphql")
                    .queryParam(REQUEST_PARAM_QUERY, "{query}")
                    .build(query)
            }
            .exchange()
            .verifyGraphQLRoute("Hello JUNIT GET!")
    }

    @Test
    fun `verify GET graphQL request with variables`() {
        val query = "query helloWorldQuery(\$name: String!) { hello(name: \$name) }"
        val variables = mapOf("name" to "JUNIT GET with variables")
        val operationName = "helloWorldQuery"
        testClient.get()
            .uri { builder ->
                builder.path("/graphql")
                    .queryParam(REQUEST_PARAM_QUERY, "{query}")
                    .queryParam(REQUEST_PARAM_VARIABLES, "{variables}")
                    .queryParam(REQUEST_PARAM_OPERATION_NAME, "{operationName}")
                    .build(query, jacksonObjectMapper().writeValueAsString(variables), operationName)
            }
            .exchange()
            .verifyGraphQLRoute("Hello JUNIT GET with variables!")
    }

    @Test
    fun `verify query parameter takes precedence over POST body`() {
        val request = GraphQLRequest(
            query = "query helloWorldQuery(\$name: String!) { hello(name: \$name) }",
            variables = mapOf("name" to "JUNIT POST body"),
            operationName = "helloWorldQuery"
        )

        testClient.post()
            .uri { builder ->
                builder.path("/graphql")
                    .queryParam(REQUEST_PARAM_QUERY, "{query}")
                    .build("{ hello(name: \"POST query param\") }")
            }
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .verifyGraphQLRoute("Hello POST query param!")
    }

    @Test
    fun `verify POST graphQL request with graphql content type`() {
        testClient.post()
            .uri("/graphql")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(graphQLMediaType)
            .bodyValue("query { hello(name: \"POST application/graphql\") }")
            .exchange()
            .verifyGraphQLRoute("Hello POST application/graphql!")
    }

    @Test
    fun `verify POST request with XML content type will result in HTTP 400 bad request`() {
        testClient.post()
            .uri("/graphql")
            .contentType(MediaType.APPLICATION_XML)
            .exchange()
            .expectStatus()
            .isBadRequest
    }

    @Test
    fun `verify GET request without query will result in HTTP 400 bad request`() {
        testClient.get()
            .uri("/graphql")
            .exchange()
            .expectStatus()
            .isBadRequest
    }

    @Test
    fun `verify POST graphQL request with contextual value specified`() {
        testClient.post()
            .uri("/graphql")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .header("X-Custom-Header", "customValue")
            .bodyValue(GraphQLRequest(query = "query { context }"))
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.data.context").isEqualTo("customValue")
            .jsonPath("$.errors").doesNotExist()
            .jsonPath("$.extensions").doesNotExist()
    }

    @Test
    fun `verify POST graphQL request with default contextual value`() {
        testClient.post()
            .uri("/graphql")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(GraphQLRequest(query = "query { context }"))
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.data.context").isEqualTo("default")
            .jsonPath("$.errors").doesNotExist()
            .jsonPath("$.extensions").doesNotExist()
    }

    @Test
    fun `verify POST graphQL request with explicit charset`() {
        val request = GraphQLRequest(
            query = "query helloWorldQuery(\$name: String!) { hello(name: \$name) }",
            variables = mapOf("name" to "JUNIT route with charset encoding"),
            operationName = "helloWorldQuery"
        )

        testClient.post()
            .uri("/graphql")
            .accept(MediaType.APPLICATION_JSON_UTF8)
            .contentType(MediaType.APPLICATION_JSON_UTF8)
            .bodyValue(request)
            .exchange()
            .verifyGraphQLRoute("Hello JUNIT route with charset encoding!")
    }

    @Test
    fun `verify POST graphQL request with websocket header fails`() {
        val request = GraphQLRequest(
            query = "query helloWorldQuery(\$name: String!) { hello(name: \$name) }",
            variables = mapOf("name" to "JUNIT route"),
            operationName = "helloWorldQuery"
        )

        testClient.post()
            .uri("/graphql")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .header("Connection", "Upgrade")
            .header("Upgrade", "websocket")
            .bodyValue(request)
            .exchange()
            .expectStatus()
            .isNotFound
    }

    @Test
    fun `verify POST graphQL request with partially missing websocket header still passes`() {
        val request = GraphQLRequest(
            query = "query helloWorldQuery(\$name: String!) { hello(name: \$name) }",
            variables = mapOf("name" to "JUNIT route"),
            operationName = "helloWorldQuery"
        )

        testClient.post()
            .uri("/graphql")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .header("Connection", "upgrade")
            .bodyValue(request)
            .exchange()
            .expectStatus()
            .isOk
    }

    private fun WebTestClient.ResponseSpec.verifyGraphQLRoute(expected: String) = this.expectStatus().isOk
        .expectBody()
        .jsonPath("$.data.hello").isEqualTo(expected)
        .jsonPath("$.errors").doesNotExist()
        .jsonPath("$.extensions").doesNotExist()
}
