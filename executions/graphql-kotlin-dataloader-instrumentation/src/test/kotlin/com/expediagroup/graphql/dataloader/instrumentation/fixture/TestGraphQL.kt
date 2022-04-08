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
