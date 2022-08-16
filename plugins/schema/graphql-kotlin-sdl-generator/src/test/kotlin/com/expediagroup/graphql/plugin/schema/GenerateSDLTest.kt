/*
 * Copyright 2022 Expedia, Inc
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

package com.expediagroup.graphql.plugin.schema

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

class GenerateSDLTest {

    @Test
    fun `verify we can generate SDL using default hooks provider`() {
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

                "Directs the executor to include this field or fragment only when the `if` argument is true"
                directive @include(
                    "Included when true."
                    if: Boolean!
                  ) on FIELD | FRAGMENT_SPREAD | INLINE_FRAGMENT

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
                  helloWorld(name: String): String!
                }
            """.trimIndent()
        val generatedSchema = generateSDL(listOf("com.expediagroup.graphql.plugin.test"))

        assertEquals(expectedSchema, generatedSchema.trim())
    }

    @Test
    fun `verify that generation of SDL will fail if no queries are found`() {
        val exception = assertThrows<RuntimeException> {
            generateSDL(listOf("com.expediagroup.graphql.plugin.schema"))
        }
        assertEquals("Invalid query object type - no valid queries are available.", exception.message)
    }
}
