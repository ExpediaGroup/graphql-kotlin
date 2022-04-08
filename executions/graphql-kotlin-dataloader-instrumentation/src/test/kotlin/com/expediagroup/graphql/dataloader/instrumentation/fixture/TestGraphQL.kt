package com.expediagroup.graphql.dataloader.instrumentation.fixture

import com.expediagroup.graphql.dataloader.instrumentation.fixture.domain.Astronaut
import com.expediagroup.graphql.dataloader.instrumentation.fixture.datafetcher.AstronautService
import com.expediagroup.graphql.dataloader.instrumentation.fixture.datafetcher.AstronautServiceRequest
import com.expediagroup.graphql.dataloader.instrumentation.fixture.datafetcher.MissionService
import com.expediagroup.graphql.dataloader.instrumentation.fixture.datafetcher.MissionServiceRequest
import com.expediagroup.graphql.dataloader.instrumentation.fixture.domain.Nasa
import graphql.GraphQL
import graphql.schema.DataFetcher
import graphql.schema.idl.RuntimeWiring
import graphql.schema.idl.SchemaGenerator
import graphql.schema.idl.SchemaParser
import graphql.schema.idl.TypeRuntimeWiring
import java.util.concurrent.CompletableFuture

object TestGraphQL {
    private val schema = """
        type Query {
            astronaut(id: ID!): Astronaut
            mission(id: ID!): Mission
            nasa: Nasa!
        }
        type Nasa {
            astronaut(id: ID!): Astronaut
            mission(id: ID!): Mission
            address: Address!
            phoneNumber: String!
        }
        type Astronaut {
            id: ID!
            name: String
            missions: [Mission!]!
        }
        type Mission {
            id: ID!
            designation: String!
            crew: [ID]!
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

    private val missionService = MissionService()
    private val missionDataFetcher = DataFetcher { environment ->
        missionService.getMission(
            MissionServiceRequest(
                environment.getArgument<String>("id").toInt()
            ),
            environment
        )
    }


    private val runtimeWiring = RuntimeWiring.newRuntimeWiring().apply {
        type(
            TypeRuntimeWiring.newTypeWiring("Query")
                .dataFetcher("astronaut", astronautDataFetcher)
                .dataFetcher("mission", missionDataFetcher)
                .dataFetcher("nasa") {
                    Nasa()
                }
        )
        type(
            TypeRuntimeWiring.newTypeWiring("Astronaut")
                .dataFetcher("missions") { environment ->
                    val astronaut = environment.getSource<Astronaut>()
                    missionService
                        .getMissionsByAstronaut(
                            MissionServiceRequest(0, astronaut.id),
                            environment
                        )
                }
        )
        type(
            TypeRuntimeWiring.newTypeWiring("Nasa")
                .dataFetcher("astronaut", astronautDataFetcher)
                .dataFetcher("mission", missionDataFetcher)
        )
    }.build()

    val builder: GraphQL.Builder = GraphQL.newGraphQL(
        SchemaGenerator().makeExecutableSchema(
            SchemaParser().parse(schema),
            runtimeWiring
        )
    )
}
