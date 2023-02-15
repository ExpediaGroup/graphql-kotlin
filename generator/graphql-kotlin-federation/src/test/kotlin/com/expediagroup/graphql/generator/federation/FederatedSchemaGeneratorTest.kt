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

package com.expediagroup.graphql.generator.federation

import com.expediagroup.graphql.generator.TopLevelObject
import com.expediagroup.graphql.generator.extensions.print
import com.expediagroup.graphql.generator.federation.data.queries.simple.NestedQuery
import com.expediagroup.graphql.generator.federation.data.queries.simple.SimpleQuery
import com.expediagroup.graphql.generator.federation.directives.KEY_DIRECTIVE_NAME
import com.expediagroup.graphql.generator.federation.types.ENTITY_UNION_NAME
import graphql.schema.GraphQLUnionType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class FederatedSchemaGeneratorTest {
    @Test
    fun `verify can generate federated schema`() {
        val expectedSchema =
            """
            schema {
              query: Query
            }

            directive @custom on SCHEMA | SCALAR | OBJECT | FIELD_DEFINITION | ARGUMENT_DEFINITION | INTERFACE | UNION | ENUM | ENUM_VALUE | INPUT_OBJECT | INPUT_FIELD_DEFINITION

            "Marks the field, argument, input field or enum value as deprecated"
            directive @deprecated(
                "The reason for the deprecation"
                reason: String = "No longer supported"
              ) on FIELD_DEFINITION | ARGUMENT_DEFINITION | ENUM_VALUE | INPUT_FIELD_DEFINITION

            "Marks target object as extending part of the federated schema"
            directive @extends on OBJECT | INTERFACE

            "Marks target field as external meaning it will be resolved by federated schema"
            directive @external on FIELD_DEFINITION

            "Directs the executor to include this field or fragment only when the `if` argument is true"
            directive @include(
                "Included when true."
                if: Boolean!
              ) on FIELD | FRAGMENT_SPREAD | INLINE_FRAGMENT

            "Space separated list of primary keys needed to access federated object"
            directive @key(fields: _FieldSet!) repeatable on OBJECT | INTERFACE

            "Specifies the base type field set that will be selectable by the gateway"
            directive @provides(fields: _FieldSet!) on FIELD_DEFINITION

            "Specifies required input field set from the base type for a resolver"
            directive @requires(fields: _FieldSet!) on FIELD_DEFINITION

            "Directs the executor to skip this field or fragment when the `if` argument is true."
            directive @skip(
                "Skipped when true."
                if: Boolean!
              ) on FIELD | FRAGMENT_SPREAD | INLINE_FRAGMENT

            "Exposes a URL that specifies the behaviour of this scalar."
            directive @specifiedBy(
                "The URL that specifies the behaviour of this scalar."
                url: String!
              ) on SCALAR

            interface Product @extends @key(fields : "id") @key(fields : "upc") {
              id: String! @external
              reviews: [Review!]!
              upc: String! @external
            }

            union _Entity = Author | Book | User

            type Author @extends @key(fields : "authorId") {
              authorId: Int! @external
              name: String! @external
            }

            type Book implements Product @extends @key(fields : "id") @key(fields : "upc") {
              author: User! @provides(fields : "name")
              id: String! @external
              reviews: [Review!]!
              shippingCost: String! @requires(fields : "weight")
              upc: String! @external
              weight: Float! @external
            }

            type CustomScalar {
              value: String!
            }

            type Query @extends {
              "Union of all types that use the @key directive, including both types native to the schema and extended types"
              _entities(representations: [_Any!]!): [_Entity]!
              _service: _Service!
            }

            type Review {
              body: String! @custom
              content: String @deprecated(reason : "no longer supported, replace with use Review.body instead")
              customScalar: CustomScalar!
              id: String!
            }

            type User @extends @key(fields : "userId") {
              name: String! @external
              userId: Int! @external
            }

            type _Service {
              sdl: String!
            }

            "Federation scalar type used to represent any external entities passed to _entities query."
            scalar _Any

            "Federation type representing set of fields"
            scalar _FieldSet
            """.trimIndent()

        val config = FederatedSchemaGeneratorConfig(
            supportedPackages = listOf("com.expediagroup.graphql.generator.federation.data.queries.federated.v1"),
            hooks = FederatedSchemaGeneratorHooks(emptyList(), optInFederationV2 = false)
        )

        val schema = toFederatedSchema(config = config)

        assertEquals(expectedSchema, schema.print().trim())
        val productType = schema.getObjectType("Book")
        assertNotNull(productType)
        assertNotNull(productType.hasAppliedDirective(KEY_DIRECTIVE_NAME))

        val entityUnion = schema.getType(ENTITY_UNION_NAME) as? GraphQLUnionType
        assertNotNull(entityUnion)
        assertTrue(entityUnion.types.contains(productType))
    }

    @Test
    fun `verify generator does not add federation queries for non-federated schemas`() {
        val expectedSchema =
            """
            schema {
              query: Query
            }

            "Marks the field, argument, input field or enum value as deprecated"
            directive @deprecated(
                "The reason for the deprecation"
                reason: String = "No longer supported"
              ) on FIELD_DEFINITION | ARGUMENT_DEFINITION | ENUM_VALUE | INPUT_FIELD_DEFINITION

            "Marks target object as extending part of the federated schema"
            directive @extends on OBJECT | INTERFACE

            "Marks target field as external meaning it will be resolved by federated schema"
            directive @external on FIELD_DEFINITION

            "Directs the executor to include this field or fragment only when the `if` argument is true"
            directive @include(
                "Included when true."
                if: Boolean!
              ) on FIELD | FRAGMENT_SPREAD | INLINE_FRAGMENT

            "Space separated list of primary keys needed to access federated object"
            directive @key(fields: _FieldSet!) repeatable on OBJECT | INTERFACE

            "Specifies the base type field set that will be selectable by the gateway"
            directive @provides(fields: _FieldSet!) on FIELD_DEFINITION

            "Specifies required input field set from the base type for a resolver"
            directive @requires(fields: _FieldSet!) on FIELD_DEFINITION

            "Directs the executor to skip this field or fragment when the `if` argument is true."
            directive @skip(
                "Skipped when true."
                if: Boolean!
              ) on FIELD | FRAGMENT_SPREAD | INLINE_FRAGMENT

            "Exposes a URL that specifies the behaviour of this scalar."
            directive @specifiedBy(
                "The URL that specifies the behaviour of this scalar."
                url: String!
              ) on SCALAR

            type Query @extends {
              _service: _Service!
              hello(name: String!): String!
            }

            type _Service {
              sdl: String!
            }

            "Federation type representing set of fields"
            scalar _FieldSet
            """.trimIndent()

        val config = FederatedSchemaGeneratorConfig(
            supportedPackages = listOf("com.expediagroup.graphql.generator.federation.data.queries.simple"),
            hooks = FederatedSchemaGeneratorHooks(emptyList(), optInFederationV2 = false)
        )

        val schema = toFederatedSchema(config, listOf(TopLevelObject(SimpleQuery())))
        assertEquals(expectedSchema, schema.print().trim())
    }

    @Test
    fun `verify a nested federated schema still works`() {
        val expectedSchema =
            """
            schema {
              query: Query
            }

            type Query {
              _service: _Service!
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

            "Federation type representing set of fields"
            scalar _FieldSet
            """.trimIndent()

        val config = FederatedSchemaGeneratorConfig(
            supportedPackages = listOf("com.expediagroup.graphql.generator.federation.data.queries.simple"),
            hooks = FederatedSchemaGeneratorHooks(emptyList(), optInFederationV2 = false)
        )

        val schema = toFederatedSchema(config, listOf(TopLevelObject(NestedQuery())))
        assertEquals(expectedSchema, schema.print(includeDirectives = false).trim())
    }
}
