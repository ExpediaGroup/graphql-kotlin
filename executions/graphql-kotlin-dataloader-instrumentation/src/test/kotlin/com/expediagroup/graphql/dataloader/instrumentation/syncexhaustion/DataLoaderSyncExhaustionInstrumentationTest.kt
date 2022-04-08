package com.expediagroup.graphql.dataloader.instrumentation.syncexhaustion

import com.expediagroup.graphql.dataloader.DefaultKotlinDataLoaderRegistryFactory
import com.expediagroup.graphql.dataloader.KotlinDataLoaderRegistry
import com.expediagroup.graphql.dataloader.instrumentation.fixture.TestGraphQL
import com.expediagroup.graphql.dataloader.instrumentation.fixture.datafetcher.AstronautDataLoader
import com.expediagroup.graphql.dataloader.instrumentation.fixture.datafetcher.MissionDataLoader
import com.expediagroup.graphql.dataloader.instrumentation.fixture.datafetcher.MissionsByAstronautDataLoader
import com.expediagroup.graphql.dataloader.instrumentation.syncexhaustion.state.SyncExhaustionInstrumentationState
import graphql.ExecutionInput
import io.mockk.spyk
import io.mockk.verify
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.future.await
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class DataLoaderSyncExhaustionInstrumentationTest {
    private val graphQL = TestGraphQL.builder
        .instrumentation(DataLoaderSyncExhaustionInstrumentation())
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

        val kotlinDataLoaderRegistry = spyk(
            DefaultKotlinDataLoaderRegistryFactory(
                AstronautDataLoader(), MissionDataLoader()
            ).generate()
        )

        val graphQLContext = mapOf(
            KotlinDataLoaderRegistry::class to kotlinDataLoaderRegistry,
            SyncExhaustionInstrumentationState::class to SyncExhaustionInstrumentationState(
                queries.size,
                kotlinDataLoaderRegistry
            )
        )

        val results = runBlocking {
            queries.map { query ->
                async {
                    graphQL.executeAsync(
                        ExecutionInput.newExecutionInput(query).graphQLContext(graphQLContext).build()
                    ).await()
                }
            }.awaitAll()
        }

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

        val kotlinDataLoaderRegistry = spyk(
            DefaultKotlinDataLoaderRegistryFactory(
                AstronautDataLoader(), MissionDataLoader()
            ).generate()
        )

        val graphQLContext = mapOf(
            KotlinDataLoaderRegistry::class to kotlinDataLoaderRegistry,
            SyncExhaustionInstrumentationState::class to SyncExhaustionInstrumentationState(
                queries.size,
                kotlinDataLoaderRegistry
            )
        )

        val results = runBlocking {
            queries.map { query ->
                async {
                    graphQL.executeAsync(
                        ExecutionInput.newExecutionInput(query).graphQLContext(graphQLContext).build()
                    ).await()
                }
            }.awaitAll()
        }

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

        val kotlinDataLoaderRegistry = spyk(
            DefaultKotlinDataLoaderRegistryFactory(
                AstronautDataLoader(), MissionDataLoader(), MissionsByAstronautDataLoader()
            ).generate()
        )

        val graphQLContext = mapOf(
            KotlinDataLoaderRegistry::class to kotlinDataLoaderRegistry,
            SyncExhaustionInstrumentationState::class to SyncExhaustionInstrumentationState(
                queries.size,
                kotlinDataLoaderRegistry
            )
        )

        val results = runBlocking {
            queries.map { query ->
                async {
                    graphQL.executeAsync(
                        ExecutionInput.newExecutionInput(query).graphQLContext(graphQLContext).build()
                    ).await()
                }
            }.awaitAll()
        }

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
}
