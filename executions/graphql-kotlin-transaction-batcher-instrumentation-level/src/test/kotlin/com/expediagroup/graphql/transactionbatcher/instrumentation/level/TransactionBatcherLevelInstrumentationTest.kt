package com.expediagroup.graphql.transactionbatcher.instrumentation.level

import com.expediagroup.graphql.transactionbatcher.instrumentation.level.datafetcher.AstronautService
import com.expediagroup.graphql.transactionbatcher.instrumentation.level.datafetcher.AstronautServiceRequest
import com.expediagroup.graphql.transactionbatcher.instrumentation.level.datafetcher.MissionService
import com.expediagroup.graphql.transactionbatcher.instrumentation.level.datafetcher.MissionServiceRequest
import com.expediagroup.graphql.transactionbatcher.instrumentation.level.state.ExecutionLevelInstrumentationState
import com.expediagroup.graphql.transactionbatcher.transaction.TransactionBatcher
import graphql.ExecutionInput
import graphql.GraphQL
import graphql.schema.DataFetcher
import graphql.schema.idl.RuntimeWiring
import graphql.schema.idl.SchemaGenerator
import graphql.schema.idl.SchemaParser
import graphql.schema.idl.TypeRuntimeWiring
import org.junit.jupiter.api.Test
import reactor.core.publisher.Flux
import reactor.kotlin.core.publisher.toMono
import reactor.test.StepVerifier

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
                    astronautService
                        .getAstronaut(
                            AstronautServiceRequest(environment.getArgument<String>("id").toInt()),
                            environment
                        )
                }
                .dataFetcher("mission") { environment ->
                    missionService
                        .getMission(
                            MissionServiceRequest(environment.getArgument<String>("id").toInt()),
                            environment
                        )
                }
        )
    }.build()

    @Test
    fun `Instrumentation should batch and dispatch transaction`() {
        val graphQL = GraphQL
            .newGraphQL(SchemaGenerator().makeExecutableSchema(SchemaParser().parse(schema), runtimeWiring))
            .instrumentation(TransactionBatcherByLevelInstrumentation())
            .build()

        val queries = listOf(
            "query { astronaut(id: 1) { id name } }",
            "query { astronaut(id: 2) { id name } }",
            "query { mission(id: 3) { id designation } }",
            "query { mission(id: 4) { id designation } }"
        )

        val graphQLContext = mapOf(
            TransactionBatcher::class to TransactionBatcher(),
            ExecutionLevelInstrumentationState::class to ExecutionLevelInstrumentationState(queries.size)
        )

        StepVerifier.create(
            Flux.mergeSequential(
                queries.map { query ->
                    graphQL.executeAsync(
                        ExecutionInput.Builder().graphQLContext(graphQLContext).query(query).build()
                    ).toMono()
                }
            ).collectList()
        ).expectNextMatches { results ->
             results.size == queries.size &&
                 astronautService.getAstronautCallCount.get() == 2 &&
                 astronautService.produceArguments.size == 1 &&
                 astronautService.produceArguments[0][0].id == 1 &&
                 astronautService.produceArguments[0][1].id == 2 &&
                 missionService.getMissionCallCount.get() == 2 &&
                 missionService.produceArguments.size == 1 &&
                 missionService.produceArguments[0][0].id == 3 &&
                 missionService.produceArguments[0][1].id == 4
        }.verifyComplete()
    }
}
