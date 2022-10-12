/*
 * Copyright 2022 Expedia, Inc
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

package com.expediagroup.graphql.dataloader.instrumentation.level

import com.expediagroup.graphql.dataloader.instrumentation.fixture.AstronautGraphQL
import com.expediagroup.graphql.dataloader.instrumentation.fixture.DataLoaderInstrumentationStrategy
import io.mockk.clearAllMocks
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class DataLoaderLevelDispatchedInstrumentationTest {
    private val dataLoaderLevelDispatchedInstrumentation = DataLoaderLevelDispatchedInstrumentation()
    private val graphQL = AstronautGraphQL.builder
        .instrumentation(dataLoaderLevelDispatchedInstrumentation)
        // graphql java adds DataLoaderDispatcherInstrumentation by default
        .doNotAddDefaultInstrumentations()
        .build()

    @BeforeEach
    fun clear() {
        clearAllMocks()
    }

    @Test
    fun `Instrumentation should batch transactions on async top level fields`() {
        val queries = listOf(
            "{ astronaut(id: 1) { name } }",
            "{ astronaut(id: 2) { id name } }",
            "{ mission(id: 3) { id designation } }",
            "{ mission(id: 4) { designation } }"
        )

        val (results, kotlinDataLoaderRegistry) = AstronautGraphQL.execute(
            graphQL,
            queries,
            DataLoaderInstrumentationStrategy.LEVEL_DISPATCHED
        )

        assertEquals(4, results.size)

        val astronautStatistics = kotlinDataLoaderRegistry.dataLoadersMap["AstronautDataLoader"]?.statistics
        val missionStatistics = kotlinDataLoaderRegistry.dataLoadersMap["MissionDataLoader"]?.statistics

        assertEquals(1, astronautStatistics?.batchInvokeCount)
        assertEquals(2, astronautStatistics?.batchLoadCount)

        assertEquals(1, missionStatistics?.batchInvokeCount)
        assertEquals(2, missionStatistics?.batchLoadCount)

        verify(exactly = 2) {
            kotlinDataLoaderRegistry.dispatchAll()
        }
    }

    @Test
    fun `Instrumentation should batch transactions on sync top level fields`() {
        val queries = listOf(
            "{ nasa { astronaut(id: 1) { name } } }",
            "{ nasa { astronaut(id: 2) { id name } } }",
            "{ nasa { mission(id: 3) { designation } } }",
            "{ nasa { mission(id: 4) { id designation } } }"
        )

        val (results, kotlinDataLoaderRegistry) = AstronautGraphQL.execute(
            graphQL,
            queries,
            DataLoaderInstrumentationStrategy.LEVEL_DISPATCHED
        )

        assertEquals(4, results.size)

        val astronautStatistics = kotlinDataLoaderRegistry.dataLoadersMap["AstronautDataLoader"]?.statistics
        val missionStatistics = kotlinDataLoaderRegistry.dataLoadersMap["MissionDataLoader"]?.statistics

        assertEquals(1, astronautStatistics?.batchInvokeCount)
        assertEquals(2, astronautStatistics?.batchLoadCount)

        assertEquals(1, missionStatistics?.batchInvokeCount)
        assertEquals(2, missionStatistics?.batchLoadCount)

        verify(exactly = 3) {
            kotlinDataLoaderRegistry.dispatchAll()
        }
    }

    @Test
    fun `Instrumentation should batch by level even if different levels attempt to use same dataFetchers`() {
        val queries = listOf(
            // L2 astronaut - L3 missions
            "{ nasa { astronaut(id: 1) { id name missions { designation } } } }",
            // L1 astronaut - L2 missions
            "{ astronaut(id: 2) { id name missions { designation } } }",
            // L2 mission
            "{ nasa { mission(id: 3) { designation } } }",
            // L1 mission
            "{ mission(id: 4) { designation } }"
        )

        val (results, kotlinDataLoaderRegistry) = AstronautGraphQL.execute(
            graphQL,
            queries,
            DataLoaderInstrumentationStrategy.LEVEL_DISPATCHED
        )

        assertEquals(4, results.size)

        val astronautStatistics = kotlinDataLoaderRegistry.dataLoadersMap["AstronautDataLoader"]?.statistics
        val missionStatistics = kotlinDataLoaderRegistry.dataLoadersMap["MissionDataLoader"]?.statistics
        val missionsByAstronautStatistics = kotlinDataLoaderRegistry.dataLoadersMap["MissionsByAstronautDataLoader"]?.statistics

        // 1 for Level 1, 1 for Level 2
        assertEquals(2, astronautStatistics?.batchInvokeCount)
        assertEquals(2, astronautStatistics?.batchLoadCount)

        // 1 for Level 1, 1 for Level 2
        assertEquals(2, missionStatistics?.batchInvokeCount)
        assertEquals(2, missionStatistics?.batchLoadCount)

        // 1 for Level 2, 1 for Level 3
        assertEquals(2, missionsByAstronautStatistics?.batchInvokeCount)
        assertEquals(2, missionsByAstronautStatistics?.batchLoadCount)

        verify(exactly = 4) {
            kotlinDataLoaderRegistry.dispatchAll()
        }
    }

    @Test
    fun `Instrumentation should not apply to mutations`() {
        val queries = listOf(
            """mutation { createAstronaut(name: "spaceMan") { id name } }"""
        )

        val (results, kotlinDataLoaderRegistry) = AstronautGraphQL.execute(
            graphQL,
            queries,
            DataLoaderInstrumentationStrategy.LEVEL_DISPATCHED
        )

        assertEquals(1, results.size)
        verify(exactly = 0) {
            kotlinDataLoaderRegistry.dispatchAll()
        }
    }
}
