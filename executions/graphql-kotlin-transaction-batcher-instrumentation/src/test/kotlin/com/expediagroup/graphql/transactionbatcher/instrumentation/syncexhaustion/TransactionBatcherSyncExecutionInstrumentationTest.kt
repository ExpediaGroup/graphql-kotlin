package com.expediagroup.graphql.transactionbatcher.instrumentation.syncexhaustion

import com.expediagroup.graphql.transactionbatcher.instrumentation.TransactionLoader
import com.expediagroup.graphql.transactionbatcher.instrumentation.datafetcher.transactionbatcher.Astronaut
import com.expediagroup.graphql.transactionbatcher.instrumentation.datafetcher.transactionbatcher.AstronautService
import com.expediagroup.graphql.transactionbatcher.instrumentation.datafetcher.transactionbatcher.AstronautServiceRequest
import com.expediagroup.graphql.transactionbatcher.instrumentation.datafetcher.transactionbatcher.MissionService
import com.expediagroup.graphql.transactionbatcher.instrumentation.datafetcher.transactionbatcher.MissionServiceRequest
import com.expediagroup.graphql.transactionbatcher.instrumentation.datafetcher.transactionbatcher.NasaService
import com.expediagroup.graphql.transactionbatcher.instrumentation.syncexhaustion.state.SyncExecutionExhaustionInstrumentationState
import com.expediagroup.graphql.transactionbatcher.transaction.TransactionBatcher
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
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class TransactionBatcherSyncExecutionInstrumentationTest {
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
        .instrumentation(TransactionLoaderSyncExecutionInstrumentation())
        .build()

    @BeforeEach
    fun setup() {
        astronautService.batchArguments.clear()
        missionService.getMissionBatchArguments.clear()
    }

    @Test
    fun `Instrumentation should exhaust sync execution and batch transactions`() {
        val queries = listOf(
            "{ nasa { astronaut(id: 1) { id name missions { designation } } } }",
            "{ astronaut(id: 2) { id name missions { designation } } }",
            "{ nasa { mission(id: 3) { designation } } }",
            "{ mission(id: 4) { designation } }"
        )

        val transactionBatcher = spyk<TransactionBatcher>()
        val batchLoader = object : TransactionLoader<TransactionBatcher> {
            override val loader = transactionBatcher
            override fun dispatch() = transactionBatcher.dispatch()
            override fun isDispatchCompleted(): Boolean = transactionBatcher.isDispatchCompleted()
        }
        val graphQLContext = mapOf(
            TransactionLoader::class to batchLoader,
            SyncExecutionExhaustionInstrumentationState::class to SyncExecutionExhaustionInstrumentationState(queries.size, batchLoader)
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

        assertEquals(1, astronautService.batchArguments.size)
        assertEquals(2, astronautService.batchArguments[0].size)

        assertEquals(1, missionService.getMissionBatchArguments.size)
        assertEquals(2, missionService.getMissionBatchArguments[0].size)

        assertEquals(1, missionService.getMissionsByAstronautBatchArguments.size)
        assertEquals(2, missionService.getMissionsByAstronautBatchArguments[0].size)

        verify(exactly = 3) {
            transactionBatcher.dispatch()
        }
    }
}
