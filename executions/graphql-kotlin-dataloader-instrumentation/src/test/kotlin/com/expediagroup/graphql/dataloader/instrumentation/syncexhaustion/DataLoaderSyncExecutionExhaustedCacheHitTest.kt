/*
 * Copyright 2026 Expedia, Inc
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

package com.expediagroup.graphql.dataloader.instrumentation.syncexhaustion

import com.expediagroup.graphql.dataloader.KotlinDataLoader
import com.expediagroup.graphql.dataloader.KotlinDataLoaderRegistryFactory
import com.expediagroup.graphql.dataloader.instrumentation.extensions.dispatchIfNeeded
import com.expediagroup.graphql.dataloader.instrumentation.syncexhaustion.state.SyncExecutionExhaustedState
import graphql.ExecutionInput
import graphql.ExecutionResult
import graphql.GraphQL
import graphql.GraphQLContext
import graphql.schema.DataFetcher
import graphql.schema.DataFetchingEnvironment
import graphql.schema.idl.RuntimeWiring
import graphql.schema.idl.SchemaGenerator
import graphql.schema.idl.SchemaParser
import graphql.schema.idl.TypeRuntimeWiring
import org.dataloader.DataLoader
import org.dataloader.DataLoaderFactory
import org.dataloader.DataLoaderRegistry
import org.junit.jupiter.api.Test
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.fail

/**
 * A [DataLoader.load] that hits the cache still notifies the [DataLoaderSyncExecutionExhaustedDataLoaderDispatcher],
 * and when the cached future is already completed it does so synchronously, before any dispatch happens.
 * These tests make sure that such loads do not corrupt the dispatch state and leave later loads undispatched.
 *
 * Every future is completed by the test itself so each scenario always runs in the same order:
 * batch loads are completed with [completeBatchLoad] and fields that resolve asynchronously
 * are released with [openGate].
 */
class DataLoaderSyncExecutionExhaustedCacheHitTest {
    private val batchLoads = LinkedBlockingQueue<BatchLoad>()
    private val gates = ConcurrentHashMap<String, CompletableFuture<Unit>>()

    private val entityDataLoader = object : KotlinDataLoader<String, String> {
        override val dataLoaderName: String = "EntityDataLoader"
        override fun getDataLoader(graphQLContext: GraphQLContext): DataLoader<String, String> =
            DataLoaderFactory.newDataLoader { keys: List<String> ->
                CompletableFuture<List<String>>().also { future -> batchLoads.add(BatchLoad(keys.toList(), future)) }
            }
    }

    private val entityDataFetcher = DataFetcher { environment ->
        environment.entityDataLoader().load(environment.getArgument<String>("key")!!)
    }

    private val gatedEntityDataFetcher = DataFetcher { environment ->
        val key = environment.getArgument<String>("key")!!
        gate(environment.getArgument<String>("gate")!!)
            .thenCompose { environment.entityDataLoader().load(key).dispatchIfNeeded(environment) }
    }

    private val graphQL = GraphQL.newGraphQL(
        SchemaGenerator().makeExecutableSchema(
            SchemaParser().parse(
                """
                type Query {
                    entity(key: String!): String
                    gatedEntity(key: String!, gate: String!): String
                }
                """.trimIndent()
            ),
            RuntimeWiring.newRuntimeWiring()
                .type(
                    TypeRuntimeWiring.newTypeWiring("Query")
                        .dataFetcher("entity", entityDataFetcher)
                        .dataFetcher("gatedEntity", gatedEntityDataFetcher)
                )
                .build()
        )
    )
        .instrumentation(GraphQLSyncExecutionExhaustedDataLoaderDispatcher())
        .doNotAutomaticallyDispatchDataLoader()
        .build()

    @Test
    fun `Instrumentation should dispatch a load invoked after a cache hit on a completed future`() {
        val results = executeOperations(
            "{ entity(key: \"a\") }",
            "{ gatedEntity(key: \"a\", gate: \"cacheHit\") }",
            "{ gatedEntity(key: \"b\", gate: \"newKey\") }"
        )

        completeBatchLoad(listOf("a"))
        openGate("cacheHit")
        openGate("newKey")
        completeBatchLoad(listOf("b"))

        assertEquals(
            listOf(
                mapOf("entity" to "value-a"),
                mapOf("gatedEntity" to "value-a"),
                mapOf("gatedEntity" to "value-b")
            ),
            results.awaitData()
        )
        assertNoMoreBatchLoads()
    }

    @Test
    fun `Instrumentation should dispatch a load invoked after a cache hit on a completed future in a single operation`() {
        val results = executeOperations(
            "{ first: entity(key: \"a\") second: gatedEntity(key: \"a\", gate: \"cacheHit\") third: gatedEntity(key: \"b\", gate: \"newKey\") }"
        )

        completeBatchLoad(listOf("a"))
        openGate("cacheHit")
        openGate("newKey")
        completeBatchLoad(listOf("b"))

        assertEquals(
            listOf(mapOf("first" to "value-a", "second" to "value-a", "third" to "value-b")),
            results.awaitData()
        )
        assertNoMoreBatchLoads()
    }

    @Test
    fun `Instrumentation should dispatch a load invoked after a cache hit on a pending future`() {
        val results = executeOperations(
            "{ entity(key: \"a\") }",
            "{ gatedEntity(key: \"a\", gate: \"cacheHit\") }",
            "{ gatedEntity(key: \"b\", gate: \"newKey\") }"
        )

        val firstBatchLoad = awaitBatchLoad()
        assertEquals(listOf("a"), firstBatchLoad.keys)
        openGate("cacheHit")
        firstBatchLoad.complete()
        openGate("newKey")
        completeBatchLoad(listOf("b"))

        assertEquals(
            listOf(
                mapOf("entity" to "value-a"),
                mapOf("gatedEntity" to "value-a"),
                mapOf("gatedEntity" to "value-b")
            ),
            results.awaitData()
        )
        assertNoMoreBatchLoads()
    }

    @Test
    fun `Instrumentation should dispatch every load invoked after a cache hit on a completed future`() {
        val results = executeOperations(
            "{ entity(key: \"a\") }",
            "{ gatedEntity(key: \"a\", gate: \"cacheHit\") }",
            "{ first: gatedEntity(key: \"b\", gate: \"newKeys\") second: gatedEntity(key: \"c\", gate: \"newKeys\") }"
        )

        completeBatchLoad(listOf("a"))
        openGate("cacheHit")
        openGate("newKeys")
        val secondBatchLoad = awaitBatchLoad().also(BatchLoad::complete)
        val thirdBatchLoad = awaitBatchLoad().also(BatchLoad::complete)

        assertEquals(setOf("b", "c"), (secondBatchLoad.keys + thirdBatchLoad.keys).toSet())
        assertEquals(
            listOf(
                mapOf("entity" to "value-a"),
                mapOf("gatedEntity" to "value-a"),
                mapOf("first" to "value-b", "second" to "value-c")
            ),
            results.awaitData()
        )
        assertNoMoreBatchLoads()
    }

    private fun DataFetchingEnvironment.entityDataLoader(): DataLoader<String, String> =
        getDataLoader<String, String>("EntityDataLoader")!!

    private fun gate(name: String): CompletableFuture<Unit> =
        gates.computeIfAbsent(name) { CompletableFuture() }

    private fun openGate(name: String) {
        gate(name).complete(Unit)
    }

    private fun awaitBatchLoad(): BatchLoad =
        assertNotNull(
            batchLoads.poll(HANG_TIMEOUT_MS, TimeUnit.MILLISECONDS),
            "expected a batch load but none was dispatched"
        )

    private fun completeBatchLoad(expectedKeys: List<String>) {
        val batchLoad = awaitBatchLoad()
        assertEquals(expectedKeys, batchLoad.keys)
        batchLoad.complete()
    }

    private fun assertNoMoreBatchLoads() {
        assertNull(batchLoads.poll(), "unexpected batch load")
    }

    private fun executeOperations(vararg queries: String): List<CompletableFuture<ExecutionResult>> {
        val graphQLContext = GraphQLContext.getDefault()
        val syncExecutionExhaustedState = SyncExecutionExhaustedState(queries.size) {
            graphQLContext.get(DataLoaderRegistry::class)
        }
        graphQLContext.put(SyncExecutionExhaustedState::class, syncExecutionExhaustedState)
        val dataLoaderRegistry = KotlinDataLoaderRegistryFactory(listOf(entityDataLoader)).generate(
            graphQLContext,
            DataLoaderSyncExecutionExhaustedDataLoaderDispatcher(syncExecutionExhaustedState)
        )

        return queries.map { query ->
            graphQL.executeAsync(
                ExecutionInput.newExecutionInput(query)
                    .dataLoaderRegistry(dataLoaderRegistry)
                    .graphQLContext { it.of(graphQLContext) }
                    .build()
            )
        }
    }

    private fun List<CompletableFuture<ExecutionResult>>.awaitData(): List<Any?> =
        try {
            CompletableFuture.allOf(*toTypedArray()).get(HANG_TIMEOUT_MS, TimeUnit.MILLISECONDS)
            map { it.join().getData<Any?>() }
        } catch (e: TimeoutException) {
            fail("operations did not complete")
        }

    private class BatchLoad(val keys: List<String>, private val future: CompletableFuture<List<String>>) {
        fun complete() {
            future.complete(keys.map { key -> "value-$key" })
        }
    }

    companion object {
        private const val HANG_TIMEOUT_MS = 5_000L
    }
}
