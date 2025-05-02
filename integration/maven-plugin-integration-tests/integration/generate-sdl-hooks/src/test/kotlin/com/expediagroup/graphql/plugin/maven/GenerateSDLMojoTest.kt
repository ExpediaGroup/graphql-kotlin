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

package com.expediagroup.graphql.plugin.maven

import kotlinx.coroutines.runBlocking
import kotlin.test.assertEquals
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import java.io.File
import java.net.URL
import java.nio.file.Paths

class GenerateSDLMojoTest {

    @Test
    fun `verify GraphQL schema was generated`() {
        val buildDirectory = System.getProperty("buildDirectory")
        val schemaFile = File(buildDirectory, "schema.graphql")
        assertTrue(schemaFile.exists(), "schema file was generated")

        val expectedSchema = """
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
              helloWorld(name: String): String!
              randomUUID: UUID!
            }

            type _Service {
              sdl: String!
            }

            "Custom scalar representing UUID"
            scalar UUID

            scalar link__Import
        """.trimIndent()
        assertEquals(expectedSchema, schemaFile.readText().trim())
    }
}
