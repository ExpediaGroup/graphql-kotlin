/*
 * Copyright 2025 Expedia, Inc
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
import com.expediagroup.graphql.generator.federation.directives.FEDERATION_SPEC
import com.expediagroup.graphql.generator.federation.directives.FEDERATION_SPEC_LATEST_URL
import com.expediagroup.graphql.generator.federation.directives.FEDERATION_SPEC_URL_PREFIX
import com.expediagroup.graphql.generator.federation.directives.KEY_DIRECTIVE_NAME
import com.expediagroup.graphql.generator.federation.types.ENTITY_UNION_NAME
import graphql.schema.GraphQLUnionType
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class FederatedSchemaGeneratorTest {
    @Test
    fun `verify can generate federated schema`() {
        val expectedSchema =
            """
            schema @link(import : ["@external", "@key", "@provides", "@requires", "FieldSet"], url : "https://specs.apollo.dev/federation/v2.7"){
              query: Query
            }

            directive @custom on SCHEMA | SCALAR | OBJECT | FIELD_DEFINITION | ARGUMENT_DEFINITION | INTERFACE | UNION | ENUM | ENUM_VALUE | INPUT_OBJECT | INPUT_FIELD_DEFINITION

            "Marks the field, argument, input field or enum value as deprecated"
            directive @deprecated(
                "The reason for the deprecation"
                reason: String = "No longer supported"
              ) on FIELD_DEFINITION | ARGUMENT_DEFINITION | ENUM_VALUE | INPUT_FIELD_DEFINITION

            "Marks target field as external meaning it will be resolved by federated schema"
            directive @external on OBJECT | FIELD_DEFINITION

            "Directs the executor to include this field or fragment only when the `if` argument is true"
            directive @include(
                "Included when true."
                if: Boolean!
              ) on FIELD | FRAGMENT_SPREAD | INLINE_FRAGMENT

            "Space separated list of primary keys needed to access federated object"
            directive @key(fields: FieldSet!, resolvable: Boolean = true) repeatable on OBJECT | INTERFACE

            "Links definitions within the document to external schemas."
            directive @link(as: String, import: [link__Import], url: String!) repeatable on SCHEMA

            "Indicates an Input Object is a OneOf Input Object."
            directive @oneOf on INPUT_OBJECT

            "Specifies the base type field set that will be selectable by the gateway"
            directive @provides(fields: FieldSet!) on FIELD_DEFINITION

            "Specifies required input field set from the base type for a resolver"
            directive @requires(fields: FieldSet!) on FIELD_DEFINITION

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

            interface Product @key(fields : "id", resolvable : true) @key(fields : "upc", resolvable : true) {
              id: String!
              reviews: [Review!]!
              upc: String!
            }

            union _Entity = Author | Book | User

            type Author @key(fields : "authorId", resolvable : true) {
              authorId: Int!
              name: String!
            }

            type Book implements Product @key(fields : "id", resolvable : true) @key(fields : "upc", resolvable : true) {
              author: User! @provides(fields : "name")
              id: String!
              reviews: [Review!]!
              shippingCost: String! @requires(fields : "weight")
              upc: String!
              weight: Float! @external
            }

            type CustomScalar {
              value: String!
            }

            type Query {
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

            type User @key(fields : "userId", resolvable : true) {
              name: String!
              userId: Int!
            }

            type _Service {
              sdl: String!
            }

            "Federation type representing set of fields"
            scalar FieldSet

            "Federation scalar type used to represent any external entities passed to _entities query."
            scalar _Any

            scalar link__Import
            """.trimIndent()

        val config = FederatedSchemaGeneratorConfig(
            supportedPackages = listOf("com.expediagroup.graphql.generator.federation.data.queries.federated"),
            hooks = FederatedSchemaGeneratorHooks(emptyList())
        )

        val schema = toFederatedSchema(config = config)
        Assertions.assertEquals(expectedSchema, schema.print().trim())
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
            schema @link(url : "https://specs.apollo.dev/federation/v2.7"){
              query: Query
            }

            "Marks the field, argument, input field or enum value as deprecated"
            directive @deprecated(
                "The reason for the deprecation"
                reason: String = "No longer supported"
              ) on FIELD_DEFINITION | ARGUMENT_DEFINITION | ENUM_VALUE | INPUT_FIELD_DEFINITION

            "Directs the executor to include this field or fragment only when the `if` argument is true"
            directive @include(
                "Included when true."
                if: Boolean!
              ) on FIELD | FRAGMENT_SPREAD | INLINE_FRAGMENT

            "Links definitions within the document to external schemas."
            directive @link(as: String, import: [link__Import], url: String!) repeatable on SCHEMA

            "Indicates an Input Object is a OneOf Input Object."
            directive @oneOf on INPUT_OBJECT

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

            type Query {
              _service: _Service!
              hello(name: String!): String!
            }

            type _Service {
              sdl: String!
            }

            scalar link__Import
            """.trimIndent()

        val config = FederatedSchemaGeneratorConfig(
            supportedPackages = listOf("com.expediagroup.graphql.generator.federation.data.queries.simple"),
            hooks = FederatedSchemaGeneratorHooks(emptyList())
        )

        val schema = toFederatedSchema(config, listOf(TopLevelObject(SimpleQuery())))
        assertEquals(expectedSchema, schema.print().trim())
    }

    @Test
    fun `verify a schema with self nested query still works`() {
        val expectedSchema =
            """
            schema @link(url : "https://specs.apollo.dev/federation/v2.7"){
              query: Query
            }

            "Marks the field, argument, input field or enum value as deprecated"
            directive @deprecated(
                "The reason for the deprecation"
                reason: String = "No longer supported"
              ) on FIELD_DEFINITION | ARGUMENT_DEFINITION | ENUM_VALUE | INPUT_FIELD_DEFINITION

            "Directs the executor to include this field or fragment only when the `if` argument is true"
            directive @include(
                "Included when true."
                if: Boolean!
              ) on FIELD | FRAGMENT_SPREAD | INLINE_FRAGMENT

            "Links definitions within the document to external schemas."
            directive @link(as: String, import: [link__Import], url: String!) repeatable on SCHEMA

            "Indicates an Input Object is a OneOf Input Object."
            directive @oneOf on INPUT_OBJECT

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

            scalar link__Import
            """.trimIndent()

        val config = FederatedSchemaGeneratorConfig(
            supportedPackages = listOf("com.expediagroup.graphql.generator.federation.data.queries.simple"),
            hooks = FederatedSchemaGeneratorHooks(emptyList())
        )

        val schema = toFederatedSchema(config, listOf(TopLevelObject(NestedQuery())))
        assertEquals(expectedSchema, schema.print(includeDirectives = true).trim())
    }

    @Test
    fun `verify federationUrl property returns correct URL`() {
        val hooks = FederatedSchemaGeneratorHooks(emptyList()).apply {
            this.linkSpecs[FEDERATION_SPEC] = FederatedSchemaGeneratorHooks.LinkSpec(
                namespace = FEDERATION_SPEC,
                imports = emptyMap(),
                url = "$FEDERATION_SPEC_URL_PREFIX/v2.5"
            )
        }
        assertEquals("$FEDERATION_SPEC_URL_PREFIX/v2.5", hooks.federationUrl)
    }

    @Test
    fun `verify federationUrl property returns default when not specified`() {
        val hooks = FederatedSchemaGeneratorHooks(emptyList())
        assertEquals(FEDERATION_SPEC_LATEST_URL, hooks.federationUrl)
    }
}
