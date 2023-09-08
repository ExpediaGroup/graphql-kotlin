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
package com.expediagroup.graphql.generator.federation.directives.link

import com.expediagroup.graphql.generator.TopLevelObject
import com.expediagroup.graphql.generator.annotations.GraphQLDirective
import com.expediagroup.graphql.generator.extensions.print
import com.expediagroup.graphql.generator.federation.FederatedSchemaGeneratorConfig
import com.expediagroup.graphql.generator.federation.FederatedSchemaGeneratorHooks
import com.expediagroup.graphql.generator.federation.directives.FEDERATION_SPEC_LATEST_URL
import com.expediagroup.graphql.generator.federation.directives.FieldSet
import com.expediagroup.graphql.generator.federation.directives.KEY_DIRECTIVE_NAME
import com.expediagroup.graphql.generator.federation.directives.KeyDirective
import com.expediagroup.graphql.generator.federation.directives.LinkDirective
import com.expediagroup.graphql.generator.federation.directives.LinkImport
import com.expediagroup.graphql.generator.federation.directives.LinkedSpec
import com.expediagroup.graphql.generator.federation.exception.DuplicateSpecificationLinkImport
import com.expediagroup.graphql.generator.federation.exception.UnknownSpecificationException
import com.expediagroup.graphql.generator.federation.toFederatedSchema
import com.expediagroup.graphql.generator.federation.types.ENTITY_UNION_NAME
import com.expediagroup.graphql.generator.scalars.ID
import graphql.schema.GraphQLUnionType
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class LinkDirectiveTest {

    @Test
    fun `verify we can import federation spec using custom @link`() {
        val expectedSchema =
            """
            schema @link(as : "fed", import : [{name : "@key", as : "@myKey"}], url : "https://specs.apollo.dev/federation/v2.3"){
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

            "Space separated list of primary keys needed to access federated object"
            directive @myKey(fields: fed__FieldSet!, resolvable: Boolean = true) repeatable on OBJECT | INTERFACE

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

            type Foo @myKey(fields : "id", resolvable : true) {
              id: ID!
              name: String
            }

            type Query {
              "Union of all types that use the @key directive, including both types native to the schema and extended types"
              _entities(representations: [_Any!]!): [_Entity]!
              _service: _Service!
            }

            type _Service {
              sdl: String!
            }

            "Federation scalar type used to represent any external entities passed to _entities query."
            scalar _Any

            "Federation type representing set of fields"
            scalar fed__FieldSet

            scalar link__Import
            """.trimIndent()

        val config = FederatedSchemaGeneratorConfig(
            supportedPackages = listOf("com.expediagroup.graphql.generator.federation.directives.link"),
            hooks = FederatedSchemaGeneratorHooks(emptyList())
        )

        val schema = toFederatedSchema(schemaObject = TopLevelObject(CustomSchema()), config = config)
        Assertions.assertEquals(expectedSchema, schema.print().trim())
        val fooType = schema.getObjectType("Foo")
        assertNotNull(fooType)
        assertNotNull(fooType.hasAppliedDirective(KEY_DIRECTIVE_NAME))

        val entityUnion = schema.getType(ENTITY_UNION_NAME) as? GraphQLUnionType
        assertNotNull(entityUnion)
        assertTrue(entityUnion.types.contains(fooType))
    }

    @Test
    fun `verifies exception is thrown if we attempt to import same specification multiple times`() {
        val config = FederatedSchemaGeneratorConfig(
            supportedPackages = listOf("com.expediagroup.graphql.generator.federation.directives.link"),
            hooks = FederatedSchemaGeneratorHooks(emptyList())
        )

        assertThrows<DuplicateSpecificationLinkImport> {
            toFederatedSchema(schemaObject = TopLevelObject(MultipleSpecImportsSchema()), config = config)
        }
    }

    @Test
    fun `verifies exception is thrown when attempting to use external type without importing it from specification`() {
        val config = FederatedSchemaGeneratorConfig(
            supportedPackages = listOf("com.expediagroup.graphql.generator.federation.directives.link"),
            hooks = FederatedSchemaGeneratorHooks(emptyList())
        )

        assertThrows<UnknownSpecificationException> {
            toFederatedSchema(queries = listOf(TopLevelObject(FooQuery())), config = config)
        }
    }

    @LinkDirective(url = FEDERATION_SPEC_LATEST_URL, `as` = "fed", import = [LinkImport("@key", "@myKey")])
    class CustomSchema

    @LinkDirective(url = FEDERATION_SPEC_LATEST_URL, `as` = "fed", import = [LinkImport("@key", "@myKey")])
    @LinkDirective(url = FEDERATION_SPEC_LATEST_URL, `as` = "federation", import = [LinkImport("@key")])
    class MultipleSpecImportsSchema

    @KeyDirective(fields = FieldSet("id"))
    data class Foo(val id: ID, val name: String?)

    @LinkedSpec("mySpec")
    @GraphQLDirective
    annotation class CustomDirective

    class FooQuery {
        @CustomDirective
        fun foo(): Foo = TODO()
    }
}
