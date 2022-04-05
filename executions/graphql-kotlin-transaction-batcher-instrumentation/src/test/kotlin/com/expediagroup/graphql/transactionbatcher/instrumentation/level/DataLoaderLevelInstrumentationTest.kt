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

package com.expediagroup.graphql.transactionbatcher.instrumentation.level

import com.expediagroup.graphql.server.execution.dataloader.DefaultDataLoaderRegistryFactory
import com.expediagroup.graphql.transactionbatcher.instrumentation.datafetcher.Astronaut
import com.expediagroup.graphql.transactionbatcher.instrumentation.datafetcher.AstronautDataLoader
import com.expediagroup.graphql.transactionbatcher.instrumentation.datafetcher.AstronautService
import com.expediagroup.graphql.transactionbatcher.instrumentation.datafetcher.AstronautServiceRequest
import com.expediagroup.graphql.transactionbatcher.instrumentation.datafetcher.MissionDataLoader
import com.expediagroup.graphql.transactionbatcher.instrumentation.datafetcher.MissionService
import com.expediagroup.graphql.transactionbatcher.instrumentation.datafetcher.MissionServiceRequest
import com.expediagroup.graphql.transactionbatcher.instrumentation.datafetcher.MissionsByAstronautDataLoader
import com.expediagroup.graphql.transactionbatcher.instrumentation.datafetcher.NasaService
import com.expediagroup.graphql.transactionbatcher.instrumentation.level.state.ExecutionLevelInstrumentationState
import graphql.ExecutionInput
import graphql.GraphQL
import graphql.schema.idl.RuntimeWiring
import graphql.schema.idl.SchemaGenerator
import graphql.schema.idl.SchemaParser
import graphql.schema.idl.TypeRuntimeWiring
import io.mockk.spyk
import io.mockk.verify
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.future.await
import kotlinx.coroutines.runBlocking
import org.dataloader.DataLoaderRegistry
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class DataLoaderLevelInstrumentationTest {
    private val schema = """
        type Query {
            astronaut(id: ID!): Astronaut
            mission(id: ID!): Mission
            nasa: Nasa!
        }
        type Nasa {
            astronaut(id: ID!): Astronaut!
            mission(id: ID!): Mission!
        }
        type Astronaut {
            id: ID!
            name: String
            missions: [Mission]
        }
        type Mission {
            id: ID!
            designation: String!
            crew: [ID]!
        }
    """.trimIndent()

    private val astronautService = AstronautService()
    private val missionService = MissionService()

    private val runtimeWiring = RuntimeWiring.newRuntimeWiring().apply {
        type(
            TypeRuntimeWiring.newTypeWiring("Query")
                .dataFetcher("astronaut") { environment ->
                    astronautService.getAstronaut(
                        AstronautServiceRequest(environment.getArgument<String>("id").toInt()),
                        environment
                    )
                }
                .dataFetcher("mission") { environment ->
                    missionService.getMission(
                        MissionServiceRequest(environment.getArgument<String>("id").toInt()),
                        environment
                    )
                }
                .dataFetcher("nasa") { NasaService(astronautService, missionService) }
        )
        type(
            TypeRuntimeWiring.newTypeWiring("Astronaut")
                .dataFetcher("missions") { env ->
                    val astronaut = env.getSource<Astronaut>()
                    missionService
                        .getMissionsByAstronaut(
                            MissionServiceRequest(id = 0, astronautId = astronaut.id),
                            env
                        )
                }
        )
    }.build()

    private val graphQL = GraphQL
        .newGraphQL(SchemaGenerator().makeExecutableSchema(SchemaParser().parse(schema), runtimeWiring))
        .instrumentation(TransactionLoaderLevelInstrumentation())
        // graphql java adds DataLoaderDispatcherInstrumentation by default
        .doNotAddDefaultInstrumentations()
        .build()

    @BeforeEach
    fun setup() {
        AstronautService.batchArguments.clear()
        MissionService.getMissionBatchArguments.clear()
    }

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
                listOf(AstronautDataLoader(), MissionDataLoader())
            ).generate()
        )

        val graphQLContext = mapOf(
            DataLoaderRegistry::class to dataLoaderRegistry,
            ExecutionLevelInstrumentationState::class to ExecutionLevelInstrumentationState(queries.size)
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

        assertEquals(1, AstronautService.batchArguments.size)
        assertEquals(2, AstronautService.batchArguments[0].size)

        assertEquals(1, MissionService.getMissionBatchArguments.size)
        assertEquals(2, MissionService.getMissionBatchArguments[0].size)

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
                listOf(AstronautDataLoader(), MissionDataLoader())
            ).generate()
        )

        val graphQLContext = mapOf(
            DataLoaderRegistry::class to dataLoaderRegistry,
            ExecutionLevelInstrumentationState::class to ExecutionLevelInstrumentationState(queries.size)
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

        assertEquals(1, AstronautService.batchArguments.size)
        assertEquals(2, AstronautService.batchArguments[0].size)

        assertEquals(1, MissionService.getMissionBatchArguments.size)
        assertEquals(2, MissionService.getMissionBatchArguments[0].size)

        verify(exactly = 3) {
            dataLoaderRegistry.dispatchAll()
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

        val dataLoaderRegistry = spyk(
            DefaultDataLoaderRegistryFactory(
                listOf(AstronautDataLoader(), MissionDataLoader(), MissionsByAstronautDataLoader())
            ).generate()
        )

        val graphQLContext = mapOf(
            DataLoaderRegistry::class to dataLoaderRegistry,
            ExecutionLevelInstrumentationState::class to ExecutionLevelInstrumentationState(queries.size)
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

        // Level 1
        assertEquals(1, AstronautService.batchArguments[0].size)
        assertEquals(1, MissionService.getMissionBatchArguments[0].size)

        // Level 2
        assertEquals(1, AstronautService.batchArguments[1].size)
        assertEquals(1, MissionService.getMissionsByAstronautBatchArguments[0].size)
        assertEquals(1, MissionService.getMissionBatchArguments[1].size)

        // Level 3
        assertEquals(1, MissionService.getMissionsByAstronautBatchArguments[1].size)

        verify(exactly = 4) {
            dataLoaderRegistry.dispatchAll()
        }
    }
}
