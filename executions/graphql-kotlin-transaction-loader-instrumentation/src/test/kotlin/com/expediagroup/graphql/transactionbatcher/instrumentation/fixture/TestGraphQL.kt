package com.expediagroup.graphql.transactionbatcher.instrumentation.fixture

import com.expediagroup.graphql.transactionbatcher.instrumentation.fixture.datafetcher.Astronaut
import com.expediagroup.graphql.transactionbatcher.instrumentation.fixture.datafetcher.AstronautService
import com.expediagroup.graphql.transactionbatcher.instrumentation.fixture.datafetcher.AstronautServiceRequest
import com.expediagroup.graphql.transactionbatcher.instrumentation.fixture.datafetcher.MissionService
import com.expediagroup.graphql.transactionbatcher.instrumentation.fixture.datafetcher.MissionServiceRequest
import com.expediagroup.graphql.transactionbatcher.instrumentation.fixture.datafetcher.Nasa
import graphql.GraphQL
import graphql.schema.idl.RuntimeWiring
import graphql.schema.idl.SchemaGenerator
import graphql.schema.idl.SchemaParser
import graphql.schema.idl.TypeRuntimeWiring

object TestGraphQL {
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
                        AstronautServiceRequest(
                            environment.getArgument<String>("id").toInt()
                        ),
                        environment
                    )
                }
                .dataFetcher("mission") { environment ->
                    missionService.getMission(
                        MissionServiceRequest(
                            environment.getArgument<String>("id").toInt()
                        ),
                        environment
                    )
                }
                .dataFetcher("nasa") {
                    Nasa(astronautService, missionService)
                }
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

    val builder: GraphQL.Builder = GraphQL.newGraphQL(
        SchemaGenerator().makeExecutableSchema(
            SchemaParser().parse(schema),
            runtimeWiring
        )
    )
}
