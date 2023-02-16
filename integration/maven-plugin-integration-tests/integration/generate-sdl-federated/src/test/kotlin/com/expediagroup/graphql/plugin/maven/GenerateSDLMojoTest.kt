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
            schema @link(import : ["@composeDirective", "@extends", "@external", "@inaccessible", "@interfaceObject", "@key", "@override", "@provides", "@requires", "@shareable", "@tag", "FieldSet"], url : "https://specs.apollo.dev/federation/v2.3"){
              query: Query
            }

            "Marks underlying custom directive to be included in the Supergraph schema"
            directive @composeDirective(name: String!) repeatable on SCHEMA

            "Marks the field, argument, input field or enum value as deprecated"
            directive @deprecated(
                "The reason for the deprecation"
                reason: String = "No longer supported"
              ) on FIELD_DEFINITION | ARGUMENT_DEFINITION | ENUM_VALUE | INPUT_FIELD_DEFINITION

            "Marks target object as extending part of the federated schema"
            directive @extends on OBJECT | INTERFACE

            "Marks target field as external meaning it will be resolved by federated schema"
            directive @external on OBJECT | FIELD_DEFINITION

            "Marks location within schema as inaccessible from the GraphQL Gateway"
            directive @inaccessible on SCALAR | OBJECT | FIELD_DEFINITION | ARGUMENT_DEFINITION | INTERFACE | UNION | ENUM | ENUM_VALUE | INPUT_OBJECT | INPUT_FIELD_DEFINITION

            "Directs the executor to include this field or fragment only when the `if` argument is true"
            directive @include(
                "Included when true."
                if: Boolean!
              ) on FIELD | FRAGMENT_SPREAD | INLINE_FRAGMENT

            "Provides meta information to the router that this entity type is an interface in the supergraph."
            directive @interfaceObject on OBJECT

            "Space separated list of primary keys needed to access federated object"
            directive @key(fields: FieldSet!, resolvable: Boolean = true) repeatable on OBJECT | INTERFACE

            "Links definitions within the document to external schemas."
            directive @link(import: [String], url: String!) repeatable on SCHEMA

            "Overrides fields resolution logic from other subgraph. Used for migrating fields from one subgraph to another."
            directive @override(from: String!) on FIELD_DEFINITION

            "Specifies the base type field set that will be selectable by the gateway"
            directive @provides(fields: FieldSet!) on FIELD_DEFINITION

            "Specifies required input field set from the base type for a resolver"
            directive @requires(fields: FieldSet!) on FIELD_DEFINITION

            "Indicates that given object and/or field can be resolved by multiple subgraphs"
            directive @shareable repeatable on OBJECT | FIELD_DEFINITION

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

            "Allows users to annotate fields and types with additional metadata information"
            directive @tag(name: String!) repeatable on SCALAR | OBJECT | FIELD_DEFINITION | ARGUMENT_DEFINITION | INTERFACE | UNION | ENUM | ENUM_VALUE | INPUT_OBJECT | INPUT_FIELD_DEFINITION

            type Query {
              _service: _Service!
              helloWorld(name: String): String!
            }

            type _Service {
              sdl: String!
            }

            "Federation type representing set of fields"
            scalar FieldSet
        """.trimIndent()
        assertEquals(expectedSchema, schemaFile.readText().trim())
    }
}
