package com.expediagroup.graphql.transactionbatcher.instrumentation.syncexhaustion

import com.expediagroup.graphql.dataloader.DefaultDataLoaderRegistryFactory
import com.expediagroup.graphql.transactionbatcher.instrumentation.fixture.TestGraphQL
import com.expediagroup.graphql.transactionbatcher.instrumentation.fixture.datafetcher.AstronautDataLoader
import com.expediagroup.graphql.transactionbatcher.instrumentation.fixture.datafetcher.MissionDataLoader
import com.expediagroup.graphql.transactionbatcher.instrumentation.fixture.datafetcher.MissionsByAstronautDataLoader
import com.expediagroup.graphql.transactionbatcher.instrumentation.syncexhaustion.state.SyncExhaustionInstrumentationState
import graphql.ExecutionInput
import io.mockk.spyk
import io.mockk.verify
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.future.await
import kotlinx.coroutines.runBlocking
import org.dataloader.DataLoaderRegistry
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class TransactionLoaderSyncExhaustionInstrumentationTest {
    private val graphQL = TestGraphQL.builder
        .instrumentation(TransactionLoaderSyncExhaustionInstrumentation())
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

        val dataLoaderRegistry = spyk(
            DefaultDataLoaderRegistryFactory(
                AstronautDataLoader(), MissionDataLoader()
            ).generate()
        )

        val graphQLContext = mapOf(
            DataLoaderRegistry::class to dataLoaderRegistry,
            SyncExhaustionInstrumentationState::class to SyncExhaustionInstrumentationState(
                queries.size,
                dataLoaderRegistry
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

        val astronautStatistics = dataLoaderRegistry.dataLoadersMap["AstronautDataLoader"]?.statistics
        val missionStatistics = dataLoaderRegistry.dataLoadersMap["MissionDataLoader"]?.statistics

        assertEquals(1, astronautStatistics?.batchInvokeCount)
        assertEquals(2, astronautStatistics?.batchLoadCount)

        assertEquals(1, missionStatistics?.batchInvokeCount)
        assertEquals(2, missionStatistics?.batchLoadCount)

        verify(exactly = 2) {
            dataLoaderRegistry.dispatchAll()
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

        val dataLoaderRegistry = spyk(
            DefaultDataLoaderRegistryFactory(
                AstronautDataLoader(), MissionDataLoader()
            ).generate()
        )

        val graphQLContext = mapOf(
            DataLoaderRegistry::class to dataLoaderRegistry,
            SyncExhaustionInstrumentationState::class to SyncExhaustionInstrumentationState(
                queries.size,
                dataLoaderRegistry
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

        val astronautStatistics = dataLoaderRegistry.dataLoadersMap["AstronautDataLoader"]?.statistics
        val missionStatistics = dataLoaderRegistry.dataLoadersMap["MissionDataLoader"]?.statistics

        assertEquals(1, astronautStatistics?.batchInvokeCount)
        assertEquals(2, astronautStatistics?.batchLoadCount)

        assertEquals(1, missionStatistics?.batchInvokeCount)
        assertEquals(2, missionStatistics?.batchLoadCount)

        verify(exactly = 2) {
            dataLoaderRegistry.dispatchAll()
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

        val dataLoaderRegistry = spyk(
            DefaultDataLoaderRegistryFactory(
                AstronautDataLoader(), MissionDataLoader(), MissionsByAstronautDataLoader()
            ).generate()
        )

        val graphQLContext = mapOf(
            DataLoaderRegistry::class to dataLoaderRegistry,
            SyncExhaustionInstrumentationState::class to SyncExhaustionInstrumentationState(
                queries.size,
                dataLoaderRegistry
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

        val astronautStatistics = dataLoaderRegistry.dataLoadersMap["AstronautDataLoader"]?.statistics
        val missionStatistics = dataLoaderRegistry.dataLoadersMap["MissionDataLoader"]?.statistics
        val missionsByAstronautStatistics = dataLoaderRegistry.dataLoadersMap["MissionsByAstronautDataLoader"]?.statistics

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
            dataLoaderRegistry.dispatchAll()
        }
    }
}
