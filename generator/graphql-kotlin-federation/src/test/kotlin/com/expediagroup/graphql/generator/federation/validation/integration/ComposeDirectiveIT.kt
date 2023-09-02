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
import com.expediagroup.graphql.generator.federation.data.integration.composeDirective.CustomSchema
import com.expediagroup.graphql.generator.federation.data.integration.composeDirective.SimpleQuery
import com.expediagroup.graphql.generator.federation.directives.FEDERATION_SPEC_LATEST_VERSION
import com.expediagroup.graphql.generator.federation.toFederatedSchema
import kotlin.test.assertEquals
import org.junit.jupiter.api.Test

class ComposeDirectiveIT {

    @Test
    fun `verifies applying @composeDirective generates valid schema`() {
        val schema = toFederatedSchema(
            config = federatedTestConfig("com.expediagroup.graphql.generator.federation.data.integration.composeDirective"),
            queries = listOf(TopLevelObject(SimpleQuery())),
            schemaObject = TopLevelObject(CustomSchema())
        )

        val expected = """
            schema @composeDirective(name : "custom") @link(import : ["@composeDirective"], url : "https://specs.apollo.dev/federation/v$FEDERATION_SPEC_LATEST_VERSION"){
              query: Query
            }

            "Marks underlying custom directive to be included in the Supergraph schema"
            directive @composeDirective(name: String!) repeatable on SCHEMA

            directive @custom on FIELD_DEFINITION

            "Links definitions within the document to external schemas."
            directive @link(as: String, import: [link__Import], url: String!) repeatable on SCHEMA

            type Query {
              _service: _Service!
              helloWorld: String! @custom
            }

            type _Service {
              sdl: String!
            }

            scalar link__Import
        """.trimIndent()
        val actual = schema.print(
            includeDirectivesFilter = { directive -> "link" == directive || "composeDirective" == directive || "custom" == directive },
        ).trim()
        assertEquals(expected, actual)
    }
}
