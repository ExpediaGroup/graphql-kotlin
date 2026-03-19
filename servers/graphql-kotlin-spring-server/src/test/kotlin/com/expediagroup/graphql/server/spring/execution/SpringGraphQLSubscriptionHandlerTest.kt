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

package com.expediagroup.graphql.server.spring.execution

import com.expediagroup.graphql.generator.SchemaGeneratorConfig
import com.expediagroup.graphql.generator.TopLevelObject
import com.expediagroup.graphql.generator.exceptions.GraphQLKotlinException
import com.expediagroup.graphql.generator.execution.FlowSubscriptionExecutionStrategy
import com.expediagroup.graphql.generator.toSchema
import com.expediagroup.graphql.dataloader.KotlinDataLoader
import com.expediagroup.graphql.dataloader.KotlinDataLoaderRegistryFactory
import com.expediagroup.graphql.generator.extensions.toGraphQLContext
import com.expediagroup.graphql.server.execution.GraphQLRequestHandler
import com.expediagroup.graphql.server.extensions.getValueFromDataLoader
import com.expediagroup.graphql.server.types.GraphQLRequest
import graphql.GraphQL
import graphql.GraphQLContext
import graphql.schema.DataFetchingEnvironment
import graphql.schema.GraphQLSchema
import kotlinx.coroutines.reactor.asFlux
import org.dataloader.DataLoader
import org.dataloader.DataLoaderFactory
import org.dataloader.DataLoaderOptions
import org.junit.jupiter.api.Test
import reactor.core.publisher.Flux
import reactor.kotlin.core.publisher.toFlux
import reactor.kotlin.core.publisher.toMono
import reactor.test.StepVerifier
import java.time.Duration
import java.util.concurrent.CompletableFuture
import kotlin.random.Random
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class SpringGraphQLSubscriptionHandlerTest {

    private val testSchema: GraphQLSchema = toSchema(
        config = SchemaGeneratorConfig(supportedPackages = listOf("com.expediagroup.graphql.server.spring.execution")),
        queries = listOf(TopLevelObject(BasicQuery())),
        subscriptions = listOf(TopLevelObject(BasicSubscription()))
    )
    private val testGraphQL: GraphQL = GraphQL.newGraphQL(testSchema)
        .subscriptionExecutionStrategy(FlowSubscriptionExecutionStrategy())
        .build()
    private val mockLoader: KotlinDataLoader<String, String> = object : KotlinDataLoader<String, String> {
        override val dataLoaderName: String = "MockDataLoader"
        override fun getDataLoader(graphQLContext: GraphQLContext): DataLoader<String, String> =
            DataLoaderFactory.newDataLoader<String, String>(
                { ids: MutableList<String> ->
                    CompletableFuture.completedFuture(ids.map { "$it:value" })
                },
                DataLoaderOptions.newOptions().setBatchingEnabled(false).build()
            )
    }
    private val dataLoaderRegistryFactory = KotlinDataLoaderRegistryFactory(listOf(mockLoader))
    private val subscriptionHandler = GraphQLRequestHandler(testGraphQL, dataLoaderRegistryFactory)

    @Test
    fun `verify subscription`() {
        val request = GraphQLRequest(query = "subscription { ticker }")
        val responseFlux = subscriptionHandler.executeSubscription(
            request,
            emptyMap<Any, Any>().toGraphQLContext()
        ).asFlux()

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
            .verify(Duration.ofSeconds(10))
    }

    @Test
    fun `verify subscription with data loader`() {
        val request = GraphQLRequest(query = "subscription { dataLoaderValue }")
        val responseFlux = subscriptionHandler.executeSubscription(
            request,
            emptyMap<Any, Any>().toGraphQLContext()
        ).asFlux()

        StepVerifier.create(responseFlux)
            .assertNext { response ->
                assertNotNull(response.data as? Map<*, *>) { data ->
                    assertNotNull(data["dataLoaderValue"] as? String) { value ->
                        assertEquals("foo:value", value)
                    }
                }
                assertNull(response.errors)
                assertNull(response.extensions)
            }
            .thenCancel()
            .verify(Duration.ofSeconds(10))
    }

    @Test
    fun `verify subscription with data loader and batching enabled`() {
        // Use a batching-enabled loader here so we still cover the normal DataLoader dispatch path for subscriptions.
        val batchingLoader = object : KotlinDataLoader<String, String> {
            override val dataLoaderName: String = "MockDataLoader"
            override fun getDataLoader(graphQLContext: GraphQLContext): DataLoader<String, String> =
                DataLoaderFactory.newDataLoader<String, String>(
                    { ids: MutableList<String> ->
                        CompletableFuture.completedFuture(ids.map { "$it:value" })
                    },
                    DataLoaderOptions.newOptions().setBatchingEnabled(true).build()
                )
        }
        val batchingHandler = GraphQLRequestHandler(testGraphQL, KotlinDataLoaderRegistryFactory(listOf(batchingLoader)))
        val graphQLContext = emptyMap<Any, Any>().toGraphQLContext()
        val request = GraphQLRequest(query = "subscription { dataLoaderValue }")
        val responseFlux = batchingHandler.executeSubscription(request, graphQLContext).asFlux()

        StepVerifier.create(responseFlux)
            .then {
                // Subscription execution doesn't automatically flush this test loader, so dispatch explicitly to release the value.
                val registry = graphQLContext.get<org.dataloader.DataLoaderRegistry>(org.dataloader.DataLoaderRegistry::class)
                assertNotNull(registry)
                registry.dispatchAll()
            }
            .assertNext { response ->
                assertNotNull(response.data as? Map<*, *>) { data ->
                    assertNotNull(data["dataLoaderValue"] as? String) { value ->
                        assertEquals("foo:value", value)
                    }
                }
                assertNull(response.errors)
                assertNull(response.extensions)
            }
            .expectComplete()
            .verify(Duration.ofSeconds(10))
    }

    @Test
    fun `verify subscription with data loader async completion`() {
        val pendingLoad = CompletableFuture<List<String>>()
        val asyncLoader = object : KotlinDataLoader<String, String> {
            override val dataLoaderName: String = "MockDataLoader"
            override fun getDataLoader(graphQLContext: GraphQLContext): DataLoader<String, String> =
                DataLoaderFactory.newDataLoader<String, String>(
                    { _: MutableList<String> -> pendingLoad },
                    DataLoaderOptions.newOptions().setBatchingEnabled(false).build()
                )
        }
        val asyncHandler = GraphQLRequestHandler(testGraphQL, KotlinDataLoaderRegistryFactory(listOf(asyncLoader)))
        val request = GraphQLRequest(query = "subscription { dataLoaderValue }")
        val responseFlux = asyncHandler.executeSubscription(request, emptyMap<Any, Any>().toGraphQLContext()).asFlux()

        StepVerifier.create(responseFlux)
            // Keep the future pending first so we can prove the subscription waits for the async DataLoader result.
            .expectSubscription()
            .expectNoEvent(Duration.ofMillis(100))
            .then {
                assertFalse(pendingLoad.isDone)
                // Complete the future under test control to make the async handoff deterministic instead of scheduler-driven.
                pendingLoad.complete(listOf("foo:value"))
            }
            .assertNext { response ->
                assertNotNull(response.data as? Map<*, *>) { data ->
                    assertNotNull(data["dataLoaderValue"] as? String) { value ->
                        assertEquals("foo:value", value)
                    }
                }
                assertNull(response.errors)
                assertNull(response.extensions)
            }
            .expectComplete()
            .verify(Duration.ofSeconds(10))
    }

    @Test
    fun `verify subscription with context map`() {
        val request = GraphQLRequest(query = "subscription { contextualMapTicker }")
        val graphQLContext = mapOf("foo" to "junitHandler").toGraphQLContext()
        val responseFlux = subscriptionHandler.executeSubscription(request, graphQLContext).asFlux()

        StepVerifier.create(responseFlux)
            .thenConsumeWhile { response ->
                assertNotNull(response.data as? Map<*, *>) { data ->
                    assertNotNull(data["contextualMapTicker"] as? String) { tickerValue ->
                        assertTrue(tickerValue.startsWith("junitHandler:"))
                        assertNotNull(tickerValue.substringAfter("junitHandler:").toIntOrNull())
                    }
                }
                assertNull(response.errors)
                assertNull(response.extensions)
                true
            }
            .expectComplete()
            .verify(Duration.ofSeconds(10))
    }

    @Test
    fun `verify subscription to failing publisher`() {
        val request = GraphQLRequest(query = "subscription { alwaysThrows }")
        val responseFlux = subscriptionHandler.executeSubscription(
            request,
            emptyMap<Any, Any>().toGraphQLContext()
        ).asFlux()

        StepVerifier.create(responseFlux)
            .assertNext { response ->
                assertNull(response.data)
                assertNotNull(response.errors) { errors ->
                    assertEquals(1, errors.size)
                    val error = errors.first()
                    assertEquals("JUNIT subscription failure", error.message)
                }
                assertNull(response.extensions)
            }
            .expectComplete()
            .verify(Duration.ofSeconds(10))
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

        fun contextualMapTicker(dfe: DataFetchingEnvironment): Flux<String> = Flux.range(1, 5)
            .delayElements(Duration.ofMillis(100))
            .map { "${dfe.graphQlContext.get<String>("foo")}:${Random.nextInt(100)}" }

        fun dataLoaderValue(dfe: DataFetchingEnvironment): Flux<String> = dfe.getValueFromDataLoader<String, String>("MockDataLoader", "foo").toMono().toFlux()
    }
}
