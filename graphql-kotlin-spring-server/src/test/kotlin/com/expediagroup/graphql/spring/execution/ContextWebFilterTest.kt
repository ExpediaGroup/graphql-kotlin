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

package com.expediagroup.graphql.spring.execution

import graphql.GraphQLContext
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import reactor.util.context.Context
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class ContextWebFilterTest {

    @Test
    @Suppress("ForbiddenVoid")
    fun `verify web filter populates context in the subscriber context`() {
        var generatedContext: Context? = null
        val exchange: ServerWebExchange = mockk {
            every { request } returns mockk()
            every { response } returns mockk()
        }
        val chain: WebFilterChain = mockk {
            every { filter(any()) } returns Mono.subscriberContext().flatMap {
                generatedContext = it
                Mono.empty<Void>()
            }
        }

        val simpleFactory: GraphQLContextFactory<Any> = mockk {
            coEvery { generateContext(any(), any()) } returns GraphQLContext.newContext().build()
        }

        val contextFilter = ContextWebFilter(simpleFactory)
        StepVerifier.create(contextFilter.filter(exchange, chain))
            .verifyComplete()

        assertNotNull(generatedContext)
        val graphQLContext = generatedContext?.getOrDefault<GraphQLContext>(GRAPHQL_CONTEXT_KEY, null)
        assertNotNull(graphQLContext)
    }

    @Test
    fun `verify web filter order`() {
        val factory: GraphQLContextFactory<Any> = mockk {
            coEvery { generateContext(any(), any()) } returns mockk()
        }
        val contextFilter = ContextWebFilter(factory)
        assertEquals(expected = 0, actual = contextFilter.order)
    }
}
