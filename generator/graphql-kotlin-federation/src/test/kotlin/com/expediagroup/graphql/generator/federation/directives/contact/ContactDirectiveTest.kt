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
package com.expediagroup.graphql.generator.federation.directives.contact

import com.expediagroup.graphql.generator.TopLevelObject
import com.expediagroup.graphql.generator.extensions.print
import com.expediagroup.graphql.generator.federation.FederatedSchemaGeneratorConfig
import com.expediagroup.graphql.generator.federation.FederatedSchemaGeneratorHooks
import com.expediagroup.graphql.generator.federation.directives.ContactDirective
import com.expediagroup.graphql.generator.federation.toFederatedSchema
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ContactDirectiveTest {

    @Test
    fun `verify we can import federation spec using custom @link`() {
        val expectedSchema =
            """
            schema @contact(description : "Send emails to foo@myteamname.com", name : "My Team Name", url : "https://myteam.slack.com/room") @link(url : "https://specs.apollo.dev/federation/v2.5"){
              query: Query
            }

            "Provides contact information of the owner responsible for this subgraph schema."
            directive @contact(description: String, name: String!, url: String) on SCHEMA

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
              foo: String!
            }

            type _Service {
              sdl: String!
            }

            scalar link__Import
            """.trimIndent()

        val config = FederatedSchemaGeneratorConfig(
            supportedPackages = listOf("com.expediagroup.graphql.generator.federation.directives.contact"),
            hooks = FederatedSchemaGeneratorHooks(emptyList())
        )

        val schema = toFederatedSchema(
            queries = listOf(TopLevelObject(FooQuery())),
            schemaObject = TopLevelObject(CustomSchema()),
            config = config
        )
        assertEquals(expectedSchema, schema.print().trim())
    }

    @ContactDirective(name = "My Team Name", url = "https://myteam.slack.com/room", description = "Send emails to foo@myteamname.com")
    class CustomSchema

    class FooQuery {
        fun foo(): String = TODO()
    }
}
