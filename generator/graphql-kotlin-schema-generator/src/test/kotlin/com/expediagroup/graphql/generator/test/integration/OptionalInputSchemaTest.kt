/*
 * Copyright 2020 Expedia, Inc
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

package com.expediagroup.graphql.generator.test.integration

import com.expediagroup.graphql.generator.SchemaGenerator
import com.expediagroup.graphql.generator.TopLevelObject
import com.expediagroup.graphql.generator.extensions.print
import com.expediagroup.graphql.generator.testSchemaConfig
import org.junit.jupiter.api.Test
import kotlin.reflect.full.createType
import kotlin.test.assertEquals

class OptionalInputSchemaTest {

    @Test
    fun `SchemaGenerator generates a simple GraphQL schema`() {
        val generator = SchemaGenerator(testSchemaConfig)
        val schema = generator.generateSchema(
            queries = listOf(TopLevelObject(Query())),
            additionalInputTypes = setOf(MyAdditionalInput::class.createType())
        )
        val sdl =
            """
            schema {
              query: Query
            }

            type Query {
              printList(input: [String]!): [String]!
              printMessage(input: String): String
              printOptionalList(input: [String]): [String]
              printType(input: MyObjectInput): String!
              printTypeList(input: [MyObjectInput]): String!
            }

            input MyAdditionalInput {
              listOptional: [String]
              listRequired: [String!]!
              optional: String
              required: String!
            }

            input MyObjectInput {
              id: String!
              value: String
            }

            """.trimIndent()

        assertEquals(sdl, schema.print(includeDirectiveDefinitions = false))
    }

    class Query {

        fun printMessage(input: String?) = input
        fun printList(input: List<String?>) = input
        fun printOptionalList(input: List<String?>?) = input
        fun printType(input: MyObject?) = input.toString()
        fun printTypeList(input: List<MyObject?>?) = input.toString()
    }

    data class MyObject(
        val id: String,
        val value: String?
    )

    data class MyAdditionalInput(
        val required: String,
        val optional: String?,
        val listOptional: List<String?>?,
        val listRequired: List<String>
    )
}
