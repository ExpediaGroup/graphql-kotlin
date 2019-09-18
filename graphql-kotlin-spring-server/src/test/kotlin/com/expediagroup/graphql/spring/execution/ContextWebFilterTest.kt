package com.expediagroup.graphql.spring.execution

import graphql.GraphQLContext
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import kotlin.test.assertNotNull

class ContextWebFilterTest {

    @Test
    fun `verify web filter populates context in the subscriber context`() {
        val mockRequest = mockk<ServerHttpRequest>()
        val exchange = mockk<ServerWebExchange> {
            every { request } returns mockRequest
            every { response } returns mockk()
        }
        val chain = mockk<WebFilterChain> {
            every { filter(exchange) } returns Mono.empty()
        }

        val contextFilter = ContextWebFilter(EmptyContextFactory)
        StepVerifier.create(contextFilter.filter(exchange, chain))
            .expectAccessibleContext()
            .hasSize(1)
            .hasKey(GRAPHQL_CONTEXT_KEY)
            .assertThat {
                val graphQLContext = it.getOrDefault<GraphQLContext>(GRAPHQL_CONTEXT_KEY, null)
                assertNotNull(graphQLContext)
            }
            .then()
            .verifyComplete()
    }
}
