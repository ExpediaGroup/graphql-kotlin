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

package com.expediagroup.graphql.transactionbatcher.instrumentation

import com.expediagroup.graphql.transactionbatcher.instrumentation.datafetcher.Astronaut
import com.expediagroup.graphql.transactionbatcher.instrumentation.datafetcher.AstronautService
import com.expediagroup.graphql.transactionbatcher.instrumentation.datafetcher.AstronautServiceRequest
import com.expediagroup.graphql.transactionbatcher.instrumentation.datafetcher.Mission
import com.expediagroup.graphql.transactionbatcher.instrumentation.datafetcher.MissionService
import com.expediagroup.graphql.transactionbatcher.instrumentation.datafetcher.MissionServiceRequest
import com.expediagroup.graphql.transactionbatcher.instrumentation.datafetcher.NasaService
import com.expediagroup.graphql.transactionbatcher.instrumentation.state.ExecutionLevelInstrumentationState
import com.expediagroup.graphql.transactionbatcher.publisher.TriggeredPublisher
import com.expediagroup.graphql.transactionbatcher.transaction.TransactionBatcher
import graphql.ExecutionInput
import graphql.GraphQL
import graphql.schema.idl.RuntimeWiring
import graphql.schema.idl.SchemaGenerator
import graphql.schema.idl.SchemaParser
import graphql.schema.idl.TypeRuntimeWiring
import io.mockk.confirmVerified
import io.mockk.spyk
import io.mockk.verify
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.future.await
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class TransactionBatcherLevelInstrumentationTest {
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
    }.build()

    private val graphQL = GraphQL
        .newGraphQL(SchemaGenerator().makeExecutableSchema(SchemaParser().parse(schema), runtimeWiring))
        .instrumentation(TransactionBatcherLevelInstrumentation())
        .build()

    @BeforeEach
    fun setup() {
        astronautService.batchArguments.clear()
        missionService.batchArguments.clear()
    }

    @Test
    fun `Instrumentation should batch transactions on async top level fields`() {
        val queries = listOf(
            "{ astronaut(id: 1) { name } }",
            "{ astronaut(id: 2) { id name } }",
            "{ mission(id: 3) { id designation } }",
            "{ mission(id: 4) { designation } }"
        )

        val transactionBatcher = spyk<TransactionBatcher>()
        val graphQLContext = mapOf(
            TransactionBatcher::class to transactionBatcher,
            ExecutionLevelInstrumentationState::class to ExecutionLevelInstrumentationState(queries.size)
        )

        runBlocking {
            val results = queries.map { query ->
                async {
                    graphQL.executeAsync(
                        ExecutionInput.newExecutionInput(query).graphQLContext(graphQLContext).build()
                    ).await()
                }
            }.awaitAll()

            assertEquals(4, results.size)

            assertEquals(1, astronautService.batchArguments.size)
            assertEquals(2, astronautService.batchArguments[0].size)

            assertEquals(1, missionService.batchArguments.size)
            assertEquals(2, missionService.batchArguments[0].size)

            verify(exactly = 2) {
                transactionBatcher.dispatch()
            }
            verify(exactly = 2) {
                transactionBatcher.batch(
                    ofType<AstronautServiceRequest>(),
                    ofType(),
                    ofType<TriggeredPublisher<AstronautServiceRequest, Astronaut>>()
                )
            }
            verify(exactly = 2) {
                transactionBatcher.batch(
                    ofType<MissionServiceRequest>(),
                    ofType(),
                    ofType<TriggeredPublisher<MissionServiceRequest, Mission>>()
                )
            }
            confirmVerified(transactionBatcher)
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

        val transactionBatcher = spyk<TransactionBatcher>()
        val graphQLContext = mapOf(
            TransactionBatcher::class to transactionBatcher,
            ExecutionLevelInstrumentationState::class to ExecutionLevelInstrumentationState(queries.size)
        )

        runBlocking {
            val results = queries.map { query ->
                async {
                    graphQL.executeAsync(
                        ExecutionInput.newExecutionInput(query).graphQLContext(graphQLContext).build()
                    ).await()
                }
            }.awaitAll()

            assertEquals(4, results.size)

            assertEquals(1, astronautService.batchArguments.size)
            assertEquals(2, astronautService.batchArguments[0].size)

            assertEquals(1, missionService.batchArguments.size)
            assertEquals(2, missionService.batchArguments[0].size)

            verify(exactly = 3) {
                transactionBatcher.dispatch()
            }
            verify(exactly = 2) {
                transactionBatcher.batch(
                    ofType<AstronautServiceRequest>(),
                    ofType(),
                    ofType<TriggeredPublisher<AstronautServiceRequest, Astronaut>>()
                )
            }
            verify(exactly = 2) {
                transactionBatcher.batch(
                    ofType<MissionServiceRequest>(),
                    ofType(),
                    ofType<TriggeredPublisher<MissionServiceRequest, Mission>>()
                )
            }
            confirmVerified(transactionBatcher)
        }
    }
}
