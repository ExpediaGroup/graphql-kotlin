package com.expediagroup.graphql.transactionbatcher.instrumentation

import com.expediagroup.graphql.transactionbatcher.instrumentation.datafetcher.Astronaut
import com.expediagroup.graphql.transactionbatcher.instrumentation.datafetcher.AstronautService
import com.expediagroup.graphql.transactionbatcher.instrumentation.datafetcher.AstronautServiceRequest
import com.expediagroup.graphql.transactionbatcher.instrumentation.datafetcher.MissionService
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
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class TransactionBatcherLevelInstrumentationTest2 {
    private val schema = """
        type Query {
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
                .dataFetcher("nasa") { NasaService(astronautService, missionService) }
        )
    }.build()

    private val graphQL = GraphQL
        .newGraphQL(SchemaGenerator().makeExecutableSchema(SchemaParser().parse(schema), runtimeWiring))
        .instrumentation(TransactionBatcherLevelInstrumentation())
        .build()

    @Test
    fun `Instrumentation should batch and dispatch transaction`() {
        val queries = listOf(
            "{ nasa { astronaut(id: 1) { name } } }",
            "{ nasa { astronaut(id: 2) { id name } } }"
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

            assertEquals(2, results.size)
            assertEquals(1, astronautService.batchArguments.size)
            assertEquals(2, astronautService.batchArguments[0].size)

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
            confirmVerified(transactionBatcher)
        }
    }
}
