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
package com.expediagroup.graphql.generator.federation.directives.compose

import com.expediagroup.graphql.generator.TopLevelObject
import com.expediagroup.graphql.generator.annotations.GraphQLDirective
import com.expediagroup.graphql.generator.extensions.print
import com.expediagroup.graphql.generator.federation.FederatedSchemaGeneratorConfig
import com.expediagroup.graphql.generator.federation.FederatedSchemaGeneratorHooks
import com.expediagroup.graphql.generator.federation.directives.ComposeDirective
import com.expediagroup.graphql.generator.federation.directives.FieldSet
import com.expediagroup.graphql.generator.federation.directives.KEY_DIRECTIVE_NAME
import com.expediagroup.graphql.generator.federation.directives.KeyDirective
import com.expediagroup.graphql.generator.federation.directives.LinkDirective
import com.expediagroup.graphql.generator.federation.directives.LinkImport
import com.expediagroup.graphql.generator.federation.directives.LinkedSpec
import com.expediagroup.graphql.generator.federation.toFederatedSchema
import com.expediagroup.graphql.generator.federation.types.ENTITY_UNION_NAME
import com.expediagroup.graphql.generator.scalars.ID
import graphql.introspection.Introspection
import graphql.schema.GraphQLUnionType
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class ComposeDirectiveTest {

    @Test
    fun `verify we can generate valid schema with @composeDirective`() {
        val expectedSchema =
            """
            schema @composeDirective(name : "custom") @link(as : "myspec", import : ["@custom"], url : "https://www.myspecs.dev/myspec/v1.0") @link(import : ["@composeDirective", "@key", "FieldSet"], url : "https://specs.apollo.dev/federation/v2.3"){
              query: Query
            }

            "Marks underlying custom directive to be included in the Supergraph schema"
            directive @composeDirective(name: String!) repeatable on SCHEMA

            directive @custom on FIELD_DEFINITION

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

            "Space separated list of primary keys needed to access federated object"
            directive @key(fields: FieldSet!, resolvable: Boolean = true) repeatable on OBJECT | INTERFACE

            "Links definitions within the document to external schemas."
            directive @link(as: String, import: [link__Import], url: String!) repeatable on SCHEMA

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

            union _Entity = Foo

            type Foo @key(fields : "id", resolvable : true) {
              id: ID!
              name: String
            }

            type Query {
              "Union of all types that use the @key directive, including both types native to the schema and extended types"
              _entities(representations: [_Any!]!): [_Entity]!
              _service: _Service!
              foo: Foo! @custom
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
            supportedPackages = listOf("com.expediagroup.graphql.generator.federation.directives.compose"),
            hooks = FederatedSchemaGeneratorHooks(emptyList())
        )

        val schema = toFederatedSchema(queries = listOf(TopLevelObject(FooQuery())), schemaObject = TopLevelObject(CustomSchema()), config = config)
        Assertions.assertEquals(expectedSchema, schema.print().trim())
        val fooType = schema.getObjectType("Foo")
        assertNotNull(fooType)
        assertNotNull(fooType.hasAppliedDirective(KEY_DIRECTIVE_NAME))

        val entityUnion = schema.getType(ENTITY_UNION_NAME) as? GraphQLUnionType
        assertNotNull(entityUnion)
        assertTrue(entityUnion.types.contains(fooType))
    }

    @LinkDirective(url = "https://www.myspecs.dev/myspec/v1.0", `as` = "myspec", import = [LinkImport("@custom")])
    @ComposeDirective(name = "custom")
    class CustomSchema

    @KeyDirective(fields = FieldSet("id"))
    data class Foo(val id: ID, val name: String?)

    @LinkedSpec("myspec")
    @GraphQLDirective(
        name = "custom",
        locations = [Introspection.DirectiveLocation.FIELD_DEFINITION]
    )
    annotation class CustomDirective

    class FooQuery {
        @CustomDirective
        fun foo(): Foo = TODO()
    }
}
