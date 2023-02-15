/*
 * Copyright 2023 Expedia, Inc
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

package com.expediagroup.graphql.generator.federation.validation.integration

import com.expediagroup.graphql.generator.TopLevelObject
import com.expediagroup.graphql.generator.extensions.print
import com.expediagroup.graphql.generator.federation.data.integration.intfObject.IntfObjectQuery
import com.expediagroup.graphql.generator.federation.toFederatedSchema
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class InterfaceObjectIT {

    @Test
    fun `verifies applying @composeDirective generates valid schema`() {
        val schema = toFederatedSchema(
            config = federatedTestConfig("com.expediagroup.graphql.generator.federation.data.integration.intfObject"),
            queries = listOf(TopLevelObject(IntfObjectQuery()))
        )

        val expected = """
            "Provides meta information to the router that this entity type is an interface in the supergraph."
            directive @interfaceObject on OBJECT

            union _Entity = Product

            type Product @interfaceObject {
              id: ID!
              reviews: [Review!]!
            }

            type Query {
              "Union of all types that use the @key directive, including both types native to the schema and extended types"
              _entities(representations: [_Any!]!): [_Entity]!
              _service: _Service!
              product(id: ID!): Product!
            }

            type Review {
              body: String!
              id: String!
            }

            type _Service {
              sdl: String!
            }
        """.trimIndent()
        val actual = schema.print(
            includeDefaultSchemaDefinition = false,
            includeDirectivesFilter = { directive -> "interfaceObject" == directive },
            includeScalarTypes = false
        ).trim()
        Assertions.assertEquals(expected, actual)
    }
}
