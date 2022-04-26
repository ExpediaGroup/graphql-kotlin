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

package com.expediagroup.graphql.dataloader.instrumentation.syncexhaustion

import com.expediagroup.graphql.dataloader.instrumentation.fixture.DataLoaderInstrumentationStrategy
import com.expediagroup.graphql.dataloader.instrumentation.fixture.TestGraphQL
import io.mockk.verify
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class DataLoaderSyncExecutionExhaustedInstrumentationTest {
    private val graphQL = TestGraphQL.builder
        .instrumentation(DataLoaderSyncExecutionExhaustedInstrumentation())
        // graphql java adds DataLoaderDispatcherInstrumentation by default
        .doNotAddDefaultInstrumentations()
        .build()

    @Test
    fun `Instrumentation should batch transactions on async top level fields`() {
        val queries = listOf(
            "{ astronaut(id: 1) { name } }",
            "{ astronaut(id: 2) { id name } }",
            "{ mission(id: 3) { id designation } }",
            "{ mission(id: 4) { designation } }"
        )

        val (results, kotlinDataLoaderRegistry) = TestGraphQL.execute(
            graphQL,
            queries,
            DataLoaderInstrumentationStrategy.SYNC_EXHAUSTION
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

        val (results, kotlinDataLoaderRegistry) = TestGraphQL.execute(
            graphQL,
            queries,
            DataLoaderInstrumentationStrategy.SYNC_EXHAUSTION
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
    fun `Instrumentation should batch transactions on different levels`() {
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

        val (results, kotlinDataLoaderRegistry) = TestGraphQL.execute(
            graphQL,
            queries,
            DataLoaderInstrumentationStrategy.SYNC_EXHAUSTION
        )

        assertEquals(4, results.size)

        val astronautStatistics = kotlinDataLoaderRegistry.dataLoadersMap["AstronautDataLoader"]?.statistics
        val missionStatistics = kotlinDataLoaderRegistry.dataLoadersMap["MissionDataLoader"]?.statistics
        val missionsByAstronautStatistics = kotlinDataLoaderRegistry.dataLoadersMap["MissionsByAstronautDataLoader"]?.statistics

        assertEquals(1, astronautStatistics?.batchInvokeCount)
        // Level 1 and 2
        assertEquals(2, astronautStatistics?.batchLoadCount)

        assertEquals(1, missionStatistics?.batchInvokeCount)
        // Level 1 and 2
        assertEquals(2, missionStatistics?.batchLoadCount)

        assertEquals(1, missionsByAstronautStatistics?.batchInvokeCount)
        // Level 2 and 3
        assertEquals(2, missionsByAstronautStatistics?.batchLoadCount)

        verify(exactly = 3) {
            kotlinDataLoaderRegistry.dispatchAll()
        }
    }

    @Test
    fun `Instrumentation should batch transactions after exhausting a single ExecutionInput`() {
        val queries = listOf(
            """
                fragment AstronautFragment on Astronaut { name missions { designation } }
                query ComplexQuery {
                    astronaut1: astronaut(id: 1) { ...AstronautFragment }
                    nasa {
                        astronaut(id: 2) {...AstronautFragment }
                    }
                    astronaut3: astronaut(id: 3) { ...AstronautFragment }
                }
            """.trimIndent()
        )

        val (results, kotlinDataLoaderRegistry) = TestGraphQL.execute(
            graphQL,
            queries,
            DataLoaderInstrumentationStrategy.SYNC_EXHAUSTION
        )

        assertEquals(1, results.size)

        val astronautStatistics = kotlinDataLoaderRegistry.dataLoadersMap["AstronautDataLoader"]?.statistics
        val missionsByAstronautStatistics = kotlinDataLoaderRegistry.dataLoadersMap["MissionsByAstronautDataLoader"]?.statistics

        assertEquals(1, astronautStatistics?.batchInvokeCount)
        assertEquals(3, astronautStatistics?.batchLoadCount)

        assertEquals(1, missionsByAstronautStatistics?.batchInvokeCount)
        assertEquals(3, missionsByAstronautStatistics?.batchLoadCount)

        verify(exactly = 3) {
            kotlinDataLoaderRegistry.dispatchAll()
        }
    }

    @Test
    fun `Instrumentation should batch transactions after exhausting multiple ExecutionInput`() {
        val queries = listOf(
            """
                fragment AstronautFragment on Astronaut { name missions { designation } }
                fragment MissionFragment on Mission { designation }
                query ComplexQuery {
                    mission1: mission(id: 1) { ...MissionFragment }
                    astronaut1: astronaut(id: 1) { ...AstronautFragment }
                    nasa {
                        astronaut(id: 2) {...AstronautFragment }
                    }
                    astronaut3: astronaut(id: 3) { ...AstronautFragment }
                }
            """.trimIndent(),
            """
                fragment AstronautFragment on Astronaut { name missions { designation } }
                fragment MissionFragment on Mission { designation }
                query ComplexQuery2 {
                    astronaut1: astronaut(id: 1) { ...AstronautFragment }
                    mission1: mission(id: 1) { ...MissionFragment }
                    nasa {
                        mission(id: 2) {...MissionFragment }
                    }
                    mission3: mission(id: 3) { ...MissionFragment }
                }
            """.trimIndent()
        )

        val (results, kotlinDataLoaderRegistry) = TestGraphQL.execute(
            graphQL,
            queries,
            DataLoaderInstrumentationStrategy.SYNC_EXHAUSTION
        )

        assertEquals(2, results.size)

        val astronautStatistics = kotlinDataLoaderRegistry.dataLoadersMap["AstronautDataLoader"]?.statistics
        val missionStatistics = kotlinDataLoaderRegistry.dataLoadersMap["MissionDataLoader"]?.statistics
        val missionsByAstronautStatistics = kotlinDataLoaderRegistry.dataLoadersMap["MissionsByAstronautDataLoader"]?.statistics

        assertEquals(1, astronautStatistics?.batchInvokeCount)
        assertEquals(3, astronautStatistics?.batchLoadCount)

        assertEquals(1, missionStatistics?.batchInvokeCount)
        assertEquals(3, missionStatistics?.batchLoadCount)

        assertEquals(1, missionsByAstronautStatistics?.batchInvokeCount)
        assertEquals(3, missionsByAstronautStatistics?.batchLoadCount)

        verify(exactly = 3) {
            kotlinDataLoaderRegistry.dispatchAll()
        }
    }

    @Test
    fun `Instrumentation should batch transactions for list of lists`() {
        val queries = listOf(
            """
                fragment AstronautFragment on Astronaut { name missions { designation } }
                fragment MissionFragment on Mission { designation }

                query ComplexQuery {
                    astronauts1And2: astronauts(ids: [1, 2]) { ...AstronautFragment }
                    missions1And2: missions(ids: [1, 2]) { ...MissionFragment }
                    nasa {
                        phoneNumber
                        address { street zipCode }
                        astronauts3AndNull: astronauts(ids: [3, 404]) { ...AstronautFragment }
                        missions3AndNull: missions(ids: [3, 404]) { ...MissionFragment }
                    }
                }
            """.trimIndent()
        )

        val (results, kotlinDataLoaderRegistry) = TestGraphQL.execute(
            graphQL,
            queries,
            DataLoaderInstrumentationStrategy.SYNC_EXHAUSTION
        )

        assertEquals(1, results.size)

        val astronautStatistics = kotlinDataLoaderRegistry.dataLoadersMap["AstronautDataLoader"]?.statistics
        val missionStatistics = kotlinDataLoaderRegistry.dataLoadersMap["MissionDataLoader"]?.statistics
        val missionsByAstronautStatistics = kotlinDataLoaderRegistry.dataLoadersMap["MissionsByAstronautDataLoader"]?.statistics

        assertEquals(1, astronautStatistics?.batchInvokeCount)
        assertEquals(4, astronautStatistics?.batchLoadCount)

        assertEquals(1, missionStatistics?.batchInvokeCount)
        assertEquals(4, missionStatistics?.batchLoadCount)

        assertEquals(1, missionsByAstronautStatistics?.batchInvokeCount)
        assertEquals(3, missionsByAstronautStatistics?.batchLoadCount)

        verify(exactly = 3) {
            kotlinDataLoaderRegistry.dispatchAll()
        }
    }

    @Test
    fun `Instrumentation should batch transactions for list of lists without arguments`() {
        val queries = listOf(
            """
                fragment AstronautFragment on Astronaut { name missions { designation } }
                fragment MissionFragment on Mission { designation }
                {
                    astronauts { ...AstronautFragment }
                    nasa {
                        missions { ...MissionFragment }
                    }
                }
            """.trimIndent()
        )

        val (results, kotlinDataLoaderRegistry) = TestGraphQL.execute(
            graphQL,
            queries,
            DataLoaderInstrumentationStrategy.SYNC_EXHAUSTION
        )

        assertEquals(1, results.size)

        val astronautStatistics = kotlinDataLoaderRegistry.dataLoadersMap["AstronautDataLoader"]?.statistics
        val missionStatistics = kotlinDataLoaderRegistry.dataLoadersMap["MissionDataLoader"]?.statistics
        val missionsByAstronautStatistics = kotlinDataLoaderRegistry.dataLoadersMap["MissionsByAstronautDataLoader"]?.statistics

        assertEquals(0, astronautStatistics?.batchInvokeCount)
        assertEquals(0, missionStatistics?.batchInvokeCount)
        assertEquals(1, missionsByAstronautStatistics?.batchInvokeCount)
    }
}
