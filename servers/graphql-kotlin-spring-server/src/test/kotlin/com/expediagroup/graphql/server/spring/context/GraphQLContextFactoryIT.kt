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

package com.expediagroup.graphql.server.spring.context

import com.expediagroup.graphql.server.spring.execution.SpringGraphQLContext
import com.expediagroup.graphql.server.spring.execution.SpringGraphQLContextFactory
import com.expediagroup.graphql.types.GraphQLRequest
import com.expediagroup.graphql.types.operations.Query
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.server.ServerRequest

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = ["graphql.packages=com.expediagroup.graphql.server.spring.context"]
)
@EnableAutoConfiguration
class GraphQLContextFactoryIT(@Autowired private val testClient: WebTestClient) {

    @Test
    fun `verify context is generated and available to the GraphQL execution`() {
        testClient.post()
            .uri("/graphql")
            .header("X-First-Header", "JUNIT_FIRST")
            .header("X-Second-Header", "JUNIT_SECOND")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(GraphQLRequest("query { context }"))
            .exchange()
            .expectBody()
            .jsonPath("$.data.context").exists()
            .jsonPath("$.data.context").isEqualTo("JUNIT_FIRST,JUNIT_SECOND")
            .jsonPath("$.errors").doesNotExist()
            .jsonPath("$.extensions").doesNotExist()
    }

    @Configuration
    class GraphQLContextFactoryConfiguration {

        @Bean
        fun query(): Query = ContextualQuery()

        @Bean
        @ExperimentalCoroutinesApi
        fun customContextFactory(): SpringGraphQLContextFactory<CustomContext> = object : SpringGraphQLContextFactory<CustomContext>() {
            override suspend fun generateContext(request: ServerRequest): CustomContext {
                return CustomContext(
                    first = request.headers().firstHeader("X-First-Header") ?: "DEFAULT_FIRST",
                    second = request.headers().firstHeader("X-Second-Header") ?: "DEFAULT_SECOND",
                    request = request
                )
            }
        }
    }

    class ContextualQuery : Query {
        fun context(ctx: CustomContext): String = "${ctx.first},${ctx.second}"
    }

    class CustomContext(
        val first: String?,
        val second: String?,
        request: ServerRequest
    ) : SpringGraphQLContext(request)
}
