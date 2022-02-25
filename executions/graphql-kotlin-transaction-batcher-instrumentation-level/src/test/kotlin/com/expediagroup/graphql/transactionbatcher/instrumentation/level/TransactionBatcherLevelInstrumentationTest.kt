package com.expediagroup.graphql.transactionbatcher.instrumentation.level

import com.expediagroup.graphql.transactionbatcher.instrumentation.level.datafetcher.AstronautService
import com.expediagroup.graphql.transactionbatcher.instrumentation.level.datafetcher.AstronautServiceRequest
import com.expediagroup.graphql.transactionbatcher.instrumentation.level.datafetcher.MissionService
import com.expediagroup.graphql.transactionbatcher.instrumentation.level.datafetcher.MissionServiceRequest
import com.expediagroup.graphql.transactionbatcher.instrumentation.level.state.ExecutionLevelInstrumentationState
import com.expediagroup.graphql.transactionbatcher.transaction.TransactionBatcher
import graphql.ExecutionInput
import graphql.GraphQL
import graphql.schema.idl.RuntimeWiring
import graphql.schema.idl.SchemaGenerator
import graphql.schema.idl.SchemaParser
import graphql.schema.idl.TypeRuntimeWiring
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.future.await
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class TransactionBatcherLevelInstrumentationTest {
    private val schema = """
        type Query {
            astronaut(id: ID!): Astronaut
            mission(id: ID!): Mission
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
        )
    }.build()

    private val graphQL = GraphQL
        .newGraphQL(SchemaGenerator().makeExecutableSchema(SchemaParser().parse(schema), runtimeWiring))
        .instrumentation(TransactionBatcherLevelInstrumentation())
        .build()

    @Test
    fun `Instrumentation should batch and dispatch transaction`() {
        val queries = listOf(
            "{ astronaut(id: 1) { name } }",
            "{ astronaut(id: 2) { id name } }",
            "{ mission(id: 3) { id designation } }",
            "{ mission(id: 4) { designation } }"
        )

        val graphQLContext = mapOf(
            TransactionBatcher::class to TransactionBatcher(),
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

            assertEquals(2, astronautService.getAstronautCallCount.get())
            assertEquals(1, astronautService.produceArguments.size)
            assertEquals(1, astronautService.produceArguments[0][0].id)
            assertEquals(2, astronautService.produceArguments[0][1].id)

            assertEquals(2, missionService.getMissionCallCount.get())
            assertEquals(1, missionService.produceArguments.size)
            assertEquals(3, missionService.produceArguments[0][0].id)
            assertEquals(4, missionService.produceArguments[0][1].id)
        }
    }
}
