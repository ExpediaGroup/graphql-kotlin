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

package com.expediagroup.graphql.spring.context

import com.expediagroup.graphql.annotations.GraphQLContext
import com.expediagroup.graphql.spring.execution.GRAPHQL_CONTEXT_FILTER_ODER
import com.expediagroup.graphql.spring.execution.GraphQLContextFactory
import com.expediagroup.graphql.spring.model.GraphQLRequest
import com.expediagroup.graphql.spring.operations.Query
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.reactor.ReactorContext
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.http.MediaType
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.server.WebFilter
import kotlin.coroutines.coroutineContext

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = ["graphql.packages=com.expediagroup.graphql.spring.context"])
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
            .bodyValue(GraphQLRequest("query { context { first second } }"))
            .exchange()
            .expectBody()
            .jsonPath("$.data.context").exists()
            .jsonPath("$.data.context.first").isEqualTo("JUNIT_FIRST")
            .jsonPath("$.data.context.second").isEqualTo("JUNIT_SECOND")
            .jsonPath("$.errors").doesNotExist()
            .jsonPath("$.extensions").doesNotExist()
    }

    @Configuration
    class GraphQLContextFactoryConfiguration {

        @Bean
        fun query(): Query = ContextualQuery()

        @Bean
        @ExperimentalCoroutinesApi
        fun customContextFactory(): GraphQLContextFactory<CustomContext> = object : GraphQLContextFactory<CustomContext> {
            override suspend fun generateContext(request: ServerHttpRequest, response: ServerHttpResponse): CustomContext {
                val firstValue = coroutineContext[ReactorContext]?.context?.get<String>("firstFilterValue")
                return CustomContext(
                    first = firstValue,
                    second = request.headers.getFirst("X-Second-Header") ?: "DEFAULT_SECOND"
                )
            }
        }

        @Bean
        @Order(GRAPHQL_CONTEXT_FILTER_ODER - 1)
        fun customWebFilter(): WebFilter = WebFilter { exchange, chain ->
            val headerValue = exchange.request.headers.getFirst("X-First-Header") ?: "DEFAULT_FIRST"
            chain.filter(exchange).subscriberContext { it.put("firstFilterValue", headerValue) }
        }
    }

    class ContextualQuery : Query {
        fun context(@GraphQLContext ctx: CustomContext): CustomContext = ctx
    }

    data class CustomContext(val first: String?, val second: String?)
}
