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

package com.expediagroup.graphql.spring.execution

import com.expediagroup.graphql.SchemaGeneratorConfig
import com.expediagroup.graphql.TopLevelObject
import com.expediagroup.graphql.exceptions.GraphQLKotlinException
import com.expediagroup.graphql.execution.GraphQLContext
import com.expediagroup.graphql.toSchema
import com.expediagroup.graphql.types.GraphQLRequest
import graphql.GraphQL
import graphql.schema.GraphQLSchema
import io.mockk.mockk
import org.junit.jupiter.api.Test
import reactor.core.publisher.Flux
import reactor.test.StepVerifier
import java.time.Duration
import kotlin.random.Random
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class SpringGraphQLSubscriptionHandlerTest {

    private val testSchema: GraphQLSchema = toSchema(
        config = SchemaGeneratorConfig(supportedPackages = listOf("com.expediagroup.graphql.spring.execution")),
        queries = listOf(TopLevelObject(BasicQuery())),
        subscriptions = listOf(TopLevelObject(BasicSubscription()))
    )
    private val testGraphQL: GraphQL = GraphQL.newGraphQL(testSchema).build()
    private val subscriptionHandler = SpringGraphQLSubscriptionHandler(testGraphQL)

    @Test
    fun `verify subscription`() {
        val request = GraphQLRequest(query = "subscription { ticker }")
        val responseFlux = subscriptionHandler.executeSubscription(request, mockk())

        StepVerifier.create(responseFlux)
            .thenConsumeWhile { response ->
                assertNotNull(response.data as? Map<*, *>) { data ->
                    assertNotNull(data["ticker"] as? Int)
                }
                assertNull(response.errors)
                assertNull(response.extensions)
                true
            }
            .expectComplete()
            .verify()
    }

    @Test
    fun `verify subscription with context`() {
        val request = GraphQLRequest(query = "subscription { contextualTicker }")
        val context = SubscriptionContext("junitHandler")
        val responseFlux = subscriptionHandler.executeSubscription(request, context)

        StepVerifier.create(responseFlux)
            .thenConsumeWhile { response ->
                assertNotNull(response.data as? Map<*, *>) { data ->
                    assertNotNull(data["contextualTicker"] as? String) { tickerValue ->
                        assertTrue(tickerValue.startsWith("junitHandler:"))
                        assertNotNull(tickerValue.substringAfter("junitHandler:").toIntOrNull())
                    }
                }
                assertNull(response.errors)
                assertNull(response.extensions)
                true
            }
            .expectComplete()
            .verify()
    }

    @Test
    fun `verify subscription to failing publisher`() {
        val request = GraphQLRequest(query = "subscription { alwaysThrows }")
        val responseFlux = subscriptionHandler.executeSubscription(request, mockk())

        StepVerifier.create(responseFlux)
            .assertNext { response ->
                assertNull(response.data)
                assertNotNull(response.errors) { errors ->
                    assertEquals(1, errors.size)
                    val error = errors.first()
                    assertEquals("Exception while fetching data () : JUNIT subscription failure", error.message)
                }
                assertNull(response.extensions)
            }
            .expectComplete()
            .verify()
    }

    // GraphQL spec requires at least single query to be present as Query type is needed to run introspection queries
    // see: https://github.com/graphql/graphql-spec/issues/490 and https://github.com/graphql/graphql-spec/issues/568
    class BasicQuery {
        @Suppress("Detekt.FunctionOnlyReturningConstant")
        fun query(): String = "hello"
    }

    class BasicSubscription {
        fun ticker(): Flux<Int> = Flux.range(1, 5)
            .delayElements(Duration.ofMillis(100))
            .map { Random.nextInt() }

        fun alwaysThrows(): Flux<String> = Flux.error(GraphQLKotlinException("JUNIT subscription failure"))

        fun contextualTicker(context: SubscriptionContext): Flux<String> = Flux.range(1, 5)
            .delayElements(Duration.ofMillis(100))
            .map { "${context.value}:${Random.nextInt(100)}" }
    }

    data class SubscriptionContext(val value: String) : GraphQLContext
}
