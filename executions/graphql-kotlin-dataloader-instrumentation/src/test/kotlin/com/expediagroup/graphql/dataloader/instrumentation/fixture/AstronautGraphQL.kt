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

package com.expediagroup.graphql.dataloader.instrumentation.fixture

import com.expediagroup.graphql.dataloader.KotlinDataLoaderRegistry
import com.expediagroup.graphql.dataloader.KotlinDataLoaderRegistryFactory
import com.expediagroup.graphql.dataloader.instrumentation.fixture.datafetcher.AstronautDataLoader
import com.expediagroup.graphql.dataloader.instrumentation.fixture.domain.Astronaut
import com.expediagroup.graphql.dataloader.instrumentation.fixture.datafetcher.AstronautService
import com.expediagroup.graphql.dataloader.instrumentation.fixture.datafetcher.AstronautServiceRequest
import com.expediagroup.graphql.dataloader.instrumentation.fixture.datafetcher.CreateAstronautServiceRequest
import com.expediagroup.graphql.dataloader.instrumentation.fixture.datafetcher.MissionDataLoader
import com.expediagroup.graphql.dataloader.instrumentation.fixture.datafetcher.MissionService
import com.expediagroup.graphql.dataloader.instrumentation.fixture.datafetcher.MissionServiceRequest
import com.expediagroup.graphql.dataloader.instrumentation.fixture.datafetcher.MissionsByAstronautDataLoader
import com.expediagroup.graphql.dataloader.instrumentation.fixture.datafetcher.PlanetsByMissionDataLoader
import com.expediagroup.graphql.dataloader.instrumentation.fixture.datafetcher.PlanetService
import com.expediagroup.graphql.dataloader.instrumentation.fixture.datafetcher.PlanetServiceRequest
import com.expediagroup.graphql.dataloader.instrumentation.fixture.domain.Mission
import com.expediagroup.graphql.dataloader.instrumentation.fixture.domain.Nasa
import com.expediagroup.graphql.dataloader.instrumentation.level.state.ExecutionLevelDispatchedState
import com.expediagroup.graphql.dataloader.instrumentation.syncexhaustion.state.SyncExecutionExhaustedState
import graphql.ExecutionInput
import graphql.ExecutionResult
import graphql.GraphQL
import graphql.schema.DataFetcher
import graphql.schema.idl.RuntimeWiring
import graphql.schema.idl.SchemaGenerator
import graphql.schema.idl.SchemaParser
import graphql.schema.idl.TypeRuntimeWiring
import io.mockk.spyk
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.future.await
import kotlinx.coroutines.runBlocking

object AstronautGraphQL {
    private val schema = """
        type Query {
            astronaut(id: ID!): Astronaut
            mission(id: ID!): Mission
            astronauts(ids: [ID!]): [Astronaut]!
            missions(ids: [ID!]): [Mission]!
            nasa: Nasa!
        }
        type Mutation {
            createAstronaut(name: String!): Astronaut!
        }
        type Nasa {
            astronaut(id: ID!): Astronaut
            mission(id: ID!): Mission
            astronauts(ids: [ID!]): [Astronaut]!
            missions(ids: [ID!]): [Mission]!
            address: Address!
            phoneNumber: String!
            twitter: String!
        }
        type Astronaut {
            id: ID!
            name: String!
            missions: [Mission!]!
            planets: [Planet!]!
        }
        type Mission {
            id: ID!
            designation: String!
            crew: [ID!]!
            planets: [Planet!]!
        }
        type Planet {
            id: ID!
            name: String!
        }
        type Address {
            street: String!
            zipCode: String!
        }
    """.trimIndent()

    private val astronautService = AstronautService()
    private val astronautDataFetcher = DataFetcher { environment ->
        astronautService.getAstronaut(
            AstronautServiceRequest(
                environment.getArgument<String>("id").toInt()
            ),
            environment
        )
    }
    private val createAstronautDataFetcher = DataFetcher { environment ->
        astronautService.createAstronaut(
            CreateAstronautServiceRequest(
                environment.getArgument("name")
            )
        )
    }
    private val astronautsDataFetcher = DataFetcher { environment ->
        astronautService.getAstronauts(
            environment.getArgument<List<String>>("ids")
                ?.map { id -> AstronautServiceRequest(id.toInt()) }
                ?: emptyList(),
            environment
        )
    }

    private val missionService = MissionService()
    private val missionDataFetcher = DataFetcher { environment ->
        missionService.getMission(
            MissionServiceRequest(
                environment.getArgument<String>("id").toInt()
            ),
            environment
        )
    }
    private val missionsDataFetcher = DataFetcher { environment ->
        missionService.getMissions(
            environment.getArgument<List<String>>("ids")
                ?.map { id -> MissionServiceRequest(id.toInt()) }
                ?: emptyList(),
            environment
        )
    }
    private val missionsByAstronautDataFetcher = DataFetcher { environment ->
        val astronaut = environment.getSource<Astronaut>()
        missionService
            .getMissionsByAstronaut(
                MissionServiceRequest(0, astronaut.id),
                environment
            )
    }

    private val planetService = PlanetService()
    private val planetsByMissionDataFetcher = DataFetcher { environment ->
        val mission = environment.getSource<Mission>()
        planetService.getPlanets(
            PlanetServiceRequest(0, mission.id),
            environment
        )
    }
    private val planetsByAstronautDataFetcher = DataFetcher { environment ->
        val astronaut = environment.getSource<Astronaut>()
        astronautService.getPlanets(
            AstronautServiceRequest(astronaut.id),
            environment
        )
    }

    private val runtimeWiring = RuntimeWiring.newRuntimeWiring().apply {
        type(
            TypeRuntimeWiring.newTypeWiring("Query")
                .dataFetcher("astronaut", astronautDataFetcher)
                .dataFetcher("mission", missionDataFetcher)
                .dataFetcher("astronauts", astronautsDataFetcher)
                .dataFetcher("missions", missionsDataFetcher)
                .dataFetcher("nasa") { Nasa() }
        )
        type(
            TypeRuntimeWiring.newTypeWiring("Mutation")
                .dataFetcher("createAstronaut", createAstronautDataFetcher)
        )
        type(
            TypeRuntimeWiring.newTypeWiring("Astronaut")
                .dataFetcher("missions", missionsByAstronautDataFetcher)
                .dataFetcher("planets", planetsByAstronautDataFetcher)
        )
        type(
            TypeRuntimeWiring.newTypeWiring("Mission")
                .dataFetcher("planets", planetsByMissionDataFetcher)
        )
        type(
            TypeRuntimeWiring.newTypeWiring("Nasa")
                .dataFetcher("astronaut", astronautDataFetcher)
                .dataFetcher("mission", missionDataFetcher)
                .dataFetcher("astronauts", astronautsDataFetcher)
                .dataFetcher("missions", missionsDataFetcher)
        )
    }.build()

    val builder: GraphQL.Builder = GraphQL.newGraphQL(
        SchemaGenerator().makeExecutableSchema(
            SchemaParser().parse(schema),
            runtimeWiring
        )
    )

    fun execute(
        graphQL: GraphQL,
        queries: List<String>,
        dataLoaderInstrumentationStrategy: DataLoaderInstrumentationStrategy
    ): Pair<List<ExecutionResult>, KotlinDataLoaderRegistry> {
        val kotlinDataLoaderRegistry = spyk(
            KotlinDataLoaderRegistryFactory(
                AstronautDataLoader(),
                MissionDataLoader(), MissionsByAstronautDataLoader(),
                PlanetsByMissionDataLoader()
            ).generate()
        )

        val graphQLContext = mapOf(
            when (dataLoaderInstrumentationStrategy) {
                DataLoaderInstrumentationStrategy.SYNC_EXHAUSTION ->
                    SyncExecutionExhaustedState::class to SyncExecutionExhaustedState(
                        queries.size,
                        kotlinDataLoaderRegistry
                    )
                DataLoaderInstrumentationStrategy.LEVEL_DISPATCHED ->
                    ExecutionLevelDispatchedState::class to ExecutionLevelDispatchedState(
                        queries.size
                    )
            }
        )

        val results = runBlocking {
            queries.map { query ->
                async {
                    graphQL.executeAsync(
                        ExecutionInput
                            .newExecutionInput(query)
                            .dataLoaderRegistry(kotlinDataLoaderRegistry)
                            .graphQLContext(graphQLContext)
                            .build()
                    ).await()
                }
            }.awaitAll()
        }

        return Pair(results, kotlinDataLoaderRegistry)
    }
}
