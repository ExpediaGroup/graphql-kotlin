/*
 * Copyright 2026 Expedia, Inc
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

package com.expediagroup.graphql.generator.federation.directives.listsize

import com.expediagroup.graphql.generator.TopLevelObject
import com.expediagroup.graphql.generator.extensions.print
import com.expediagroup.graphql.generator.federation.FederatedSchemaGeneratorConfig
import com.expediagroup.graphql.generator.federation.FederatedSchemaGeneratorHooks
import com.expediagroup.graphql.generator.federation.directives.FEDERATION_SPEC
import com.expediagroup.graphql.generator.federation.directives.FEDERATION_SPEC_URL_PREFIX
import com.expediagroup.graphql.generator.federation.directives.LIST_SIZE_DIRECTIVE_NAME
import com.expediagroup.graphql.generator.federation.directives.ListSizeDirective
import com.expediagroup.graphql.generator.federation.toFederatedSchema
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class ListSizeDirectiveTest {

    @Test
    fun `verify listSize directive definition for fed215`() {
        val expectedSchema =
            // language=GraphQL
            """
            schema @link(import : ["@listSize"], url : "https://specs.apollo.dev/federation/v2.15"){
              query: Query
            }

            "Marks the field, argument, input field or enum value as deprecated"
            directive @deprecated(
                "The reason for the deprecation"
                reason: String! = "No longer supported"
              ) on FIELD_DEFINITION | ARGUMENT_DEFINITION | ENUM_VALUE | INPUT_FIELD_DEFINITION

            "This directive disables error propagation when a non nullable field returns null for the given operation."
            directive @experimental_disableErrorPropagation on QUERY | MUTATION | SUBSCRIPTION

            "Directs the executor to include this field or fragment only when the `if` argument is true"
            directive @include(
                "Included when true."
                if: Boolean!
              ) on FIELD | FRAGMENT_SPREAD | INLINE_FRAGMENT

            "Links definitions within the document to external schemas."
            directive @link(as: String, import: [link__Import], url: String!) repeatable on SCHEMA

            "Used to customize the cost calculation of a list field as used by demand control features"
            directive @listSize(assumedSize: Int, requireOneSlicingArgument: Boolean = true, sizedFields: [String!], slicingArguments: [String!]) on FIELD_DEFINITION

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
              assumed: [String!]! @listSize(assumedSize : 10)
              sliced(first: Int!): [String!]! @listSize(requireOneSlicingArgument : false, slicingArguments : ["first"])
            }

            type _Service {
              sdl: String!
            }

            scalar link__Import
            """.trimIndent()

        val config = FederatedSchemaGeneratorConfig(
            supportedPackages = listOf("com.expediagroup.graphql.generator.federation.directives.listsize"),
            hooks = FederatedSchemaGeneratorHooks(emptyList()).apply {
                this.linkSpecs[FEDERATION_SPEC] = FederatedSchemaGeneratorHooks.LinkSpec(
                    namespace = FEDERATION_SPEC,
                    imports = mapOf("listSize" to "listSize"),
                    url = "$FEDERATION_SPEC_URL_PREFIX/v2.15"
                )
            }
        )
        val schema = toFederatedSchema(config, listOf(TopLevelObject(Query())))
        Assertions.assertEquals(expectedSchema, schema.print().trim())

        val query = schema.getObjectType("Query")
        assertNotNull(query)
        assertTrue(query.getField("assumed").hasAppliedDirective(LIST_SIZE_DIRECTIVE_NAME))
        assertTrue(query.getField("sliced").hasAppliedDirective(LIST_SIZE_DIRECTIVE_NAME))
    }

    @Test
    fun `verify listSize directive is not created for federation v2_8`() {
        val config = FederatedSchemaGeneratorConfig(
            supportedPackages = listOf("com.expediagroup.graphql.generator.federation.directives.listsize"),
            hooks = FederatedSchemaGeneratorHooks(emptyList()).apply {
                this.linkSpecs[FEDERATION_SPEC] = FederatedSchemaGeneratorHooks.LinkSpec(
                    namespace = FEDERATION_SPEC,
                    imports = emptyMap(),
                    url = "$FEDERATION_SPEC_URL_PREFIX/v2.8"
                )
            }
        )
        val exception = Assertions.assertThrows(IllegalArgumentException::class.java) {
            toFederatedSchema(
                queries = listOf(TopLevelObject(Query())),
                config = config
            )
        }
        Assertions.assertEquals(
            "@listSize directive requires Federation 2.9 or later, but version https://specs.apollo.dev/federation/v2.8 was specified",
            exception.message
        )
    }

    @Test
    fun `verify listSize sizedFields argument is emitted`() {
        val config = FederatedSchemaGeneratorConfig(
            supportedPackages = listOf("com.expediagroup.graphql.generator.federation.directives.listsize"),
            hooks = FederatedSchemaGeneratorHooks(emptyList()).apply {
                this.linkSpecs[FEDERATION_SPEC] = FederatedSchemaGeneratorHooks.LinkSpec(
                    namespace = FEDERATION_SPEC,
                    imports = mapOf("listSize" to "listSize"),
                    url = "$FEDERATION_SPEC_URL_PREFIX/v2.15"
                )
            }
        )
        val schema = toFederatedSchema(config, listOf(TopLevelObject(SizedFieldsQuery())))
        assertTrue(schema.print().contains("@listSize(sizedFields : [\"items\"])"))
    }

    class Query {
        @ListSizeDirective(assumedSize = 10)
        fun assumed(): List<String> = emptyList()

        @ListSizeDirective(slicingArguments = ["first"], requireOneSlicingArgument = false)
        fun sliced(first: Int): List<String> = emptyList()
    }

    class SizedFieldsQuery {
        @ListSizeDirective(sizedFields = ["items"])
        fun connection(): Connection = Connection(emptyList())
    }

    class Connection(val items: List<String>)
}
