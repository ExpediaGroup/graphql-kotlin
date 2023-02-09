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

package com.expediagroup.graphql.server.spring.context

import com.expediagroup.graphql.server.execution.context.GraphQLContextEntryProducer
import com.expediagroup.graphql.server.operations.Query
import com.expediagroup.graphql.server.spring.execution.context.SpringGraphQLContextBuilder
import com.expediagroup.graphql.server.types.GraphQLRequest
import com.expediagroup.graphql.server.types.GraphQLServerRequest
import graphql.GraphQLContext
import graphql.schema.DataFetchingEnvironment
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.server.ServerRequest

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = ["graphql.packages=com.expediagroup.graphql.server.spring.context"]
)
@EnableAutoConfiguration
class GraphQLContextBuilderIT(@Autowired private val testClient: WebTestClient) {
    @Test
    fun `verify context is generated with http request headers and available to the GraphQL execution`() {
        testClient.post()
            .uri("/graphql")
            .header("X-First-Header", "JUNIT_FIRST")
            .header("X-Second-Header", "JUNIT_SECOND")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(GraphQLRequest("query { contextMap }"))
            .exchange()
            .expectBody()
            .jsonPath("$.data.contextMap").exists()
            .jsonPath("$.data.contextMap").isEqualTo("junit_first,junit_second")
            .jsonPath("$.errors").doesNotExist()
            .jsonPath("$.extensions").doesNotExist()
    }

    @Test
    fun `verify context is generated with graphql request and available to the GraphQL execution`() {
        testClient.post()
            .uri("/graphql")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(
                GraphQLRequest(
                    "query RequestDetails(\$foo: String!) { operationName something(foo: \$foo) }",
                    "RequestDetails",
                    mapOf(
                        "foo" to "graphql-kotlin"
                    )
                )
            )
            .exchange()
            .expectBody()
            .jsonPath("$.data.operationName").exists()
            .jsonPath("$.data.operationName").isEqualTo("RequestDetails")
            .jsonPath("$.data.something").exists()
            .jsonPath("$.data.something").isEqualTo("graphql-kotlin")
            .jsonPath("$.errors").doesNotExist()
            .jsonPath("$.extensions").doesNotExist()
    }

    @Configuration
    class GraphQLContextFactoryConfiguration {

        @Bean
        fun query(): Query = ContextualQuery()

        @Bean
        @Order(1)
        fun producerA(): GraphQLContextEntryProducer<ServerRequest, String, String?> =
            GraphQLContextEntryProducer { request, _, _ ->
                "first" to (request.headers().firstHeader("X-First-Header") ?: "DEFAULT_FIRST")
            }

        @Bean
        @Order(2)
        fun producerB(): GraphQLContextEntryProducer<ServerRequest, String, String?> =
            GraphQLContextEntryProducer { request, _, _ ->
                "second" to (request.headers().firstHeader("X-Second-Header") ?: "DEFAULT_SECOND")
            }

        @Bean
        @Order(3)
        fun producerC(): GraphQLContextEntryProducer<ServerRequest, String, String?> =
            GraphQLContextEntryProducer { _, _, accumulator ->
                "third" to "${accumulator["first"] as? String},${accumulator["second"] as? String}".lowercase()
            }

        @Bean
        @Order(4)
        fun producerD(): GraphQLContextEntryProducer<ServerRequest, String, String?> =
            GraphQLContextEntryProducer { _, graphQLRequest, _ ->
                val graphqlRequest = (graphQLRequest as? GraphQLRequest)
                "operationName" to (graphqlRequest?.operationName ?: "")
            }

        @Bean
        @Order(5)
        fun producerE(): GraphQLContextEntryProducer<ServerRequest, String, String?> =
            GraphQLContextEntryProducer { _, graphQLRequest, _ ->
                val graphqlRequest = (graphQLRequest as? GraphQLRequest)
                "fooFromVariables" to (graphqlRequest?.variables?.get("foo") as? String ?: "")
            }

        @Bean
        @ExperimentalCoroutinesApi
        fun customContextProvider(
            producers: List<GraphQLContextEntryProducer<ServerRequest, Any, Any>>
        ): SpringGraphQLContextBuilder = object : SpringGraphQLContextBuilder {
            override val producers = producers

            override suspend fun generateContext(
                request: ServerRequest,
                graphQLRequest: GraphQLServerRequest
            ): GraphQLContext =
                super.generateContext(request, graphQLRequest)
        }
    }

    class ContextualQuery : Query {
        fun contextMap(env: DataFetchingEnvironment): String =
            env.graphQlContext.getOrDefault("third", "")

        fun operationName(env: DataFetchingEnvironment): String =
            env.graphQlContext.getOrDefault("operationName", "")

        fun something(env: DataFetchingEnvironment, foo: String): String =
            env.graphQlContext.getOrDefault("fooFromVariables", "")
    }
}
