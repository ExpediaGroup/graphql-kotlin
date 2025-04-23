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
import org.dataloader.DataLoader
import org.dataloader.DataLoaderFactory
import org.dataloader.instrumentation.ChainedDataLoaderInstrumentation
import org.junit.jupiter.api.Test
import reactor.kotlin.core.publisher.toFlux
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class KotlinDataLoaderRegistryFactoryTest {
    @Test
    fun `generate registry with empty list`() {
        val registry = KotlinDataLoaderRegistryFactory().generate(mockk())
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

        val registry = KotlinDataLoaderRegistryFactory(
            listOf(mockLoader)
        ).generate(mockk())
        assertEquals(1, registry.dataLoaders.size)
        assertTrue(registry.instrumentation is ChainedDataLoaderInstrumentation)
    }
}
