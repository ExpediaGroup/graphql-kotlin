package com.expediagroup.graphql.transactionbatcher.instrumentation

import com.expediagroup.graphql.server.execution.DefaultDataLoaderRegistryFactory
import com.expediagroup.graphql.transactionbatcher.instrumentation.datafetcher.dataloader.AstronautDataLoader
import com.expediagroup.graphql.transactionbatcher.instrumentation.datafetcher.dataloader.AstronautService
import com.expediagroup.graphql.transactionbatcher.instrumentation.datafetcher.dataloader.AstronautServiceRequest
import com.expediagroup.graphql.transactionbatcher.instrumentation.datafetcher.dataloader.MissionDataLoader
import com.expediagroup.graphql.transactionbatcher.instrumentation.datafetcher.dataloader.MissionService
import com.expediagroup.graphql.transactionbatcher.instrumentation.datafetcher.dataloader.MissionServiceRequest
import com.expediagroup.graphql.transactionbatcher.instrumentation.datafetcher.dataloader.NasaService
import com.expediagroup.graphql.transactionbatcher.instrumentation.state.ExecutionLevelInstrumentationState
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
        .instrumentation(TransactionLoaderLevelInstrumentation())
        // graphql java adds DataLoaderDispatcherInstrumentation by default
        .doNotAddDefaultInstrumentations()
        .build()

    @BeforeEach
    fun setup() {
        AstronautService.batchArguments.clear()
        MissionService.batchArguments.clear()
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

        val batchLoader = object : TransactionLoader<DataLoaderRegistry> {
            override val loader = dataLoaderRegistry
            override fun load() = dataLoaderRegistry.dispatchAll()
        }
        val graphQLContext = mapOf(
            TransactionLoader::class to batchLoader,
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

            assertEquals(1, AstronautService.batchArguments.size)
            assertEquals(2, AstronautService.batchArguments[0].size)

            assertEquals(1, AstronautService.batchArguments.size)
            assertEquals(2, AstronautService.batchArguments[0].size)

            verify(exactly = 2) {
                dataLoaderRegistry.dispatchAll()
            }
        }
    }
}
