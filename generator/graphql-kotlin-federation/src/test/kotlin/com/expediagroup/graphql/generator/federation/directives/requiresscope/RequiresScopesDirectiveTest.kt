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

package com.expediagroup.graphql.generator.federation.directives.requiresscope

import com.expediagroup.graphql.generator.TopLevelObject
import com.expediagroup.graphql.generator.extensions.print
import com.expediagroup.graphql.generator.federation.FederatedSchemaGeneratorConfig
import com.expediagroup.graphql.generator.federation.FederatedSchemaGeneratorHooks
import com.expediagroup.graphql.generator.federation.directives.FEDERATION_SPEC
import com.expediagroup.graphql.generator.federation.directives.FEDERATION_SPEC_URL_PREFIX
import com.expediagroup.graphql.generator.federation.directives.REQUIRES_SCOPE_DIRECTIVE_NAME
import com.expediagroup.graphql.generator.federation.directives.RequiresScopesDirective
import com.expediagroup.graphql.generator.federation.directives.Scope
import com.expediagroup.graphql.generator.federation.directives.Scopes
import com.expediagroup.graphql.generator.federation.toFederatedSchema
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import kotlin.test.assertNotNull

class RequiresScopesDirectiveTest {

    @Test
    fun `verify we can import federation spec using custom @link`() {
        val expectedSchema =
            """
            schema @link(url : "https://specs.apollo.dev/federation/v2.7"){
              query: Query
            }

            "Marks the field, argument, input field or enum value as deprecated"
            directive @deprecated(
                "The reason for the deprecation"
                reason: String! = "No longer supported"
              ) on FIELD_DEFINITION | ARGUMENT_DEFINITION | ENUM_VALUE | INPUT_FIELD_DEFINITION

            "This directive disables error propagation when a non nullable field returns null for the given operation."
            directive @experimental_disableErrorPropagation on QUERY | MUTATION | SUBSCRIPTION

            "Indicates to composition that the target element is accessible only to the authenticated supergraph users with the appropriate JWT scopes"
            directive @federation__requiresScopes(scopes: [[federation__Scope]!]!) on SCALAR | OBJECT | FIELD_DEFINITION | INTERFACE | ENUM

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
              foo: String! @federation__requiresScopes(scopes : [["scope1", "scope2"], ["scope3"]])
            }

            type _Service {
              sdl: String!
            }

            "Federation type representing a JWT scope"
            scalar federation__Scope

            scalar link__Import
            """.trimIndent()

        val config = FederatedSchemaGeneratorConfig(
            supportedPackages = listOf("com.expediagroup.graphql.generator.federation.directives.requiresscope"),
            hooks = FederatedSchemaGeneratorHooks(emptyList())
        )

        val schema = toFederatedSchema(queries = listOf(TopLevelObject(FooQuery())), config = config)
        Assertions.assertEquals(expectedSchema, schema.print().trim())
        val query = schema.getObjectType("Query")
        assertNotNull(query)
        val fooQuery = query.getField("foo")
        assertNotNull(fooQuery)
        assertNotNull(fooQuery.hasAppliedDirective(REQUIRES_SCOPE_DIRECTIVE_NAME))
    }

    @Test
    fun `verify requiresScopes directive is not created for federation v2_4`() {
        val config = FederatedSchemaGeneratorConfig(
            supportedPackages = listOf("com.expediagroup.graphql.generator.federation.directives.requiresscope"),
            hooks = FederatedSchemaGeneratorHooks(emptyList()).apply {
                this.linkSpecs[FEDERATION_SPEC] = FederatedSchemaGeneratorHooks.LinkSpec(
                    namespace = FEDERATION_SPEC,
                    imports = emptyMap(),
                    url = "$FEDERATION_SPEC_URL_PREFIX/v2.4"
                )
            }
        )
        val exception = Assertions.assertThrows(IllegalArgumentException::class.java) {
            toFederatedSchema(
                queries = listOf(TopLevelObject(FooQuery())),
                config = config
            )
        }
        Assertions.assertEquals(
            "@requiresScopes directive requires Federation 2.5 or later, but version https://specs.apollo.dev/federation/v2.4 was specified",
            exception.message
        )
    }

    class FooQuery {
        @RequiresScopesDirective(scopes = [Scopes([Scope("scope1"), Scope("scope2")]), Scopes([Scope("scope3")])])
        fun foo(): String = TODO()
    }
}
