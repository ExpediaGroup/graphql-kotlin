/*
 * Copyright 2025 Expedia, Inc
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

package com.expediagroup.graphql.dataloader

import graphql.GraphQLContext
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.future.await
import kotlinx.coroutines.runBlocking
import org.dataloader.DataLoader
import org.dataloader.DataLoaderFactory
import org.dataloader.DataLoaderOptions
import org.dataloader.instrumentation.DataLoaderInstrumentation
import org.dataloader.instrumentation.DataLoaderInstrumentationContext
import org.junit.jupiter.api.Test
import reactor.kotlin.core.publisher.toFlux
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class KotlinDataLoaderRegistryFactoryTest {
    @Test
    fun `generate registry with empty list`() {
        val registry = KotlinDataLoaderRegistryFactory().generate(mockk(relaxed = true))
        assertTrue(registry.dataLoaders.isEmpty())
    }

    @Test
    fun `generate registry with basic loader and instrumentation`() {
        val mockLoader: KotlinDataLoader<String, String> = object : KotlinDataLoader<String, String> {
            override val dataLoaderName: String = "MockDataLoader"
            override fun getDataLoader(graphQLContext: GraphQLContext): DataLoader<String, String> =
                DataLoaderFactory.newDataLoader { keys ->
                    keys.toFlux().map(String::uppercase).collectList().toFuture()
                }
        }

        val customInstrumentation = object : DataLoaderInstrumentation {
        }
        val registry = KotlinDataLoaderRegistryFactory(
            listOf(mockLoader)
        ).generate(
            mockk(relaxed = true),
            customInstrumentation
        )
        assertEquals(1, registry.dataLoaders.size)
        assertEquals(customInstrumentation, registry.instrumentation)
    }

    @Test
    fun `cached and non-batched data loader is invoked once for concurrent loads of the same key`() {
        runBlocking {
            val invocations = AtomicInteger()
            val kotlinDataLoader = object : KotlinDataLoader<String, String> {
                override val dataLoaderName = "NonBatchingDataLoader"
                override fun getDataLoader(graphQLContext: GraphQLContext): DataLoader<String, String> =
                    DataLoaderFactory.newMappedDataLoader(
                        { keys ->
                            invocations.incrementAndGet()
                            Thread.sleep(100)
                            CompletableFuture.completedFuture(keys.associateWith { it })
                        },
                        DataLoaderOptions.newOptions()
                            .setBatchingEnabled(false)
                            .build()
                    )
            }
            val registry = KotlinDataLoaderRegistryFactory(kotlinDataLoader).generate(mockk(relaxed = true))

            val dataLoader = requireNotNull(registry.getDataLoader<String, String>(kotlinDataLoader.dataLoaderName))

            List(2) {
                async(Dispatchers.Default) {
                    dataLoader.load("same-key").await()
                }
            }.awaitAll()

            assertEquals(1, invocations.get())
        }
    }

    @Test
    fun `cached and non-batched data loader is invoked once for concurrent loads of the same key when registry has instrumentation`() {
        runBlocking {
            val invocations = AtomicInteger()
            val instrumentedLoads = AtomicInteger()
            val kotlinDataLoader = object : KotlinDataLoader<String, String> {
                override val dataLoaderName = "NonBatchingDataLoader"
                override fun getDataLoader(graphQLContext: GraphQLContext): DataLoader<String, String> =
                    DataLoaderFactory.newMappedDataLoader(
                        { keys ->
                            invocations.incrementAndGet()
                            Thread.sleep(100)
                            CompletableFuture.completedFuture(keys.associateWith { it })
                        },
                        DataLoaderOptions.newOptions()
                            .setBatchingEnabled(false)
                            .build()
                    )
            }
            val countingInstrumentation = object : DataLoaderInstrumentation {
                override fun beginLoad(dataLoader: DataLoader<*, *>, key: Any, loadContext: Any?): DataLoaderInstrumentationContext<Any?>? {
                    instrumentedLoads.incrementAndGet()
                    return null
                }
            }
            val registry = KotlinDataLoaderRegistryFactory(kotlinDataLoader).generate(mockk(relaxed = true), countingInstrumentation)

            val dataLoader = requireNotNull(registry.getDataLoader<String, String>(kotlinDataLoader.dataLoaderName))

            List(8) {
                async(Dispatchers.Default) {
                    dataLoader.load("same-key").await()
                }
            }.awaitAll()

            assertEquals(1, invocations.get())
            assertEquals(8, instrumentedLoads.get())
        }
    }

    @Test
    fun `cached and non-batched data loader is invoked once per key for concurrent loadMany of the same keys`() {
        runBlocking {
            val invocations = ConcurrentHashMap<String, AtomicInteger>()
            val kotlinDataLoader = object : KotlinDataLoader<String, String> {
                override val dataLoaderName = "NonBatchingDataLoader"
                override fun getDataLoader(graphQLContext: GraphQLContext): DataLoader<String, String> =
                    DataLoaderFactory.newMappedDataLoader(
                        { keys ->
                            keys.forEach { key -> invocations.computeIfAbsent(key) { AtomicInteger() }.incrementAndGet() }
                            Thread.sleep(100)
                            CompletableFuture.completedFuture(keys.associateWith { it })
                        },
                        DataLoaderOptions.newOptions()
                            .setBatchingEnabled(false)
                            .build()
                    )
            }
            val registry = KotlinDataLoaderRegistryFactory(kotlinDataLoader).generate(mockk(relaxed = true), object : DataLoaderInstrumentation {})

            val dataLoader = requireNotNull(registry.getDataLoader<String, String>(kotlinDataLoader.dataLoaderName))
            val keys = listOf("a", "b")

            List(9) { index ->
                async(Dispatchers.Default) {
                    when (index % 3) {
                        0 -> dataLoader.loadMany(keys).await()
                        1 -> dataLoader.loadMany(keys, keys.map { key -> "context-$key" }).await()
                        else -> dataLoader.loadMany(keys.associateWith { key -> "context-$key" }).await().values.toList()
                    }
                }
            }.awaitAll()

            assertEquals(mapOf("a" to 1, "b" to 1), invocations.mapValues { (_, count) -> count.get() })
        }
    }
}
