/*
 * Copyright 2019 Expedia, Inc
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

package com.expediagroup.graphql.federation

import com.expediagroup.graphql.TopLevelObject
import com.expediagroup.graphql.extensions.print
import com.expediagroup.graphql.federation.execution.FederatedTypeRegistry
import graphql.schema.GraphQLUnionType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import test.data.queries.simple.NestedQuery
import test.data.queries.simple.SimpleQuery
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

private const val FEDERATED_SDL = """schema {
  query: Query
}

#Marks target field as external meaning it will be resolved by federated schema
directive @external on FIELD_DEFINITION

#Marks target object as extending part of the federated schema
directive @extends on OBJECT | INTERFACE

#Specifies the base type field set that will be selectable by the gateway
directive @provides(fields: _FieldSet!) on FIELD_DEFINITION

#Space separated list of primary keys needed to access federated object
directive @key(fields: _FieldSet!) on OBJECT | INTERFACE

#Specifies required input field set from the base type for a resolver
directive @requires(fields: _FieldSet!) on FIELD_DEFINITION

interface Product @extends @key(fields : "id") {
  id: String! @external
  reviews: [Review!]!
}

union _Entity = Book | User

type Book implements Product @extends @key(fields : "id") {
  author: User! @provides(fields : "name")
  id: String! @external
  reviews: [Review!]!
  shippingCost: String! @requires(fields : "weight")
  weight: Float! @external
}

type Query @extends {
  #Union of all types that use the @key directive, including both types native to the schema and extended types
  _entities(representations: [_Any!]!): [_Entity]!
  _service: _Service
}

type Review {
  body: String!
  id: String!
}

type User @extends @key(fields : "userId") {
  name: String! @external
  userId: Int! @external
}

type _Service {
  sdl: String!
}

#Federation scalar type used to represent any external entities passed to _entities query.
scalar _Any

#Federation type representing set of fields
scalar _FieldSet

#Directs the executor to include this field or fragment only when the `if` argument is true
directive @include(if: Boolean!) on FIELD | FRAGMENT_SPREAD | INLINE_FRAGMENT

#Directs the executor to skip this field or fragment when the `if`'argument is true.
directive @skip(if: Boolean!) on FIELD | FRAGMENT_SPREAD | INLINE_FRAGMENT

#Marks the target field/enum value as deprecated
directive @deprecated(reason: String = "No longer supported") on FIELD_DEFINITION | ENUM_VALUE"""

class FederatedSchemaGeneratorTest {

    @Test
    fun `verify can generate federated schema`() {
        val config = FederatedSchemaGeneratorConfig(
            supportedPackages = listOf("test.data.queries.federated"),
            hooks = FederatedSchemaGeneratorHooks(FederatedTypeRegistry())
        )

        val schema = toFederatedSchema(config)
        assertEquals(FEDERATED_SDL, schema.print().trim())
        val productType = schema.getObjectType("Book")
        assertNotNull(productType)
        assertNotNull(productType.getDirective("key"))

        val entityUnion = schema.getType("_Entity") as? GraphQLUnionType
        assertNotNull(entityUnion)
        assertTrue(entityUnion.types.contains(productType))
    }

    @Test
    fun `verify generator does not add federation queries for non-federated schemas`() {
        val expectedSchema = """
            schema {
              query: Query
            }

            type Query {
              _service: _Service
              hello(name: String!): String!
            }

            type _Service {
              sdl: String!
            }
        """.trimIndent()

        val config = FederatedSchemaGeneratorConfig(
            supportedPackages = listOf("test.data.queries.simple"),
            hooks = FederatedSchemaGeneratorHooks(FederatedTypeRegistry())
        )

        val schema = toFederatedSchema(config, listOf(TopLevelObject(SimpleQuery())))
        assertEquals(expectedSchema, schema.print(includeDirectives = false).trim())
    }

    @Test
    fun `verify a nested federated schema still works`() {
        val expectedSchema = """
            schema {
              query: Query
            }

            type Query {
              _service: _Service
              getSimpleNestedObject: [SelfReferenceObject]!
            }

            type SelfReferenceObject {
              description: String
              id: Int!
              nextObject: [SelfReferenceObject]!
            }

            type _Service {
              sdl: String!
            }
        """.trimIndent()

        val config = FederatedSchemaGeneratorConfig(
            supportedPackages = listOf("test.data.queries.simple"),
            hooks = FederatedSchemaGeneratorHooks(FederatedTypeRegistry())
        )

        val schema = toFederatedSchema(config, listOf(TopLevelObject(NestedQuery())))
        assertEquals(expectedSchema, schema.print(includeDirectives = false).trim())
    }
}
