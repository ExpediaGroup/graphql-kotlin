/*
 * Copyright 2021 Expedia, Inc
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

package com.expediagroup.graphql.plugin.client.generator

import com.expediagroup.graphql.plugin.client.generator.exceptions.SchemaUnavailableException
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.io.File
import java.nio.file.Files
import kotlin.test.assertEquals
import kotlin.test.assertFails

class GenerateInvalidClientIT {

    @ParameterizedTest
    @MethodSource("invalidTests")
    fun `verify generation fails on invalid inputs`(testDirectory: File) {
        val (queries, _) = locateTestFiles(testDirectory)
        val expectedException = File(testDirectory, "exception.txt").readText().trim()

        val config = defaultConfig.copy(
            serializer = GraphQLSerializer.JACKSON,
            customScalarMap = mapOf(
                "UUID" to GraphQLScalar("UUID", "java.util.UUID", "com.expediagroup.graphql.plugin.client.generator.UUIDScalarConverter"),
                "Locale" to GraphQLScalar("Locale", "com.ibm.icu.util.ULocale", "com.expediagroup.graphql.plugin.client.generator.ULocaleScalarConverter")
            ),
            useOptionalInputWrapper = true
        )
        val generator = GraphQLClientGenerator(TEST_SCHEMA_PATH, config)
        val exception = assertFails {
            generator.generate(queries)
        }
        assertEquals(expectedException, exception::class.simpleName)
    }

    @Test
    fun `verify an invalid schema path will raise an exception`() {
        val exception = assertFails {
            GraphQLClientGenerator("missingSchema.graphql", defaultConfig)
        }
        assertEquals(SchemaUnavailableException::class, exception::class)
    }

    @Test
    fun `verify missing operation mapping in schema definition throws meaningful exception`() {
        val testDir = Files.createTempDirectory("graphql-client-generator-root-types").toFile()
        try {
            val schemaFile = writeFile(
                testDir,
                "schema.graphql",
                """
                schema {
                    query: Query
                }

                type Query {
                    hello: String
                }
                """
            )
            val queryFile = writeFile(
                testDir,
                "mutationQuery.graphql",
                """
                mutation MutationQuery {
                    setHello
                }
                """
            )

            val exception = assertFails {
                GraphQLClientGenerator(schemaFile.absolutePath, defaultConfig).generate(listOf(queryFile))
            }
            assertEquals("No root type mapping found for operation 'MUTATION'", exception.message)
        } finally {
            testDir.deleteRecursively()
        }
    }

    @Test
    fun `verify missing root type throws meaningful exception`() {
        val testDir = Files.createTempDirectory("graphql-client-generator-root-types").toFile()
        try {
            val schemaFile = writeFile(
                testDir,
                "schema.graphql",
                """
                schema {
                    query: MissingRootType
                }

                type Query {
                    hello: String
                }
                """
            )
            val queryFile = writeFile(
                testDir,
                "missingRootTypeQuery.graphql",
                """
                query MissingRootTypeQuery {
                    hello
                }
                """
            )

            val exception = assertFails {
                GraphQLClientGenerator(schemaFile.absolutePath, defaultConfig).generate(listOf(queryFile))
            }
            assertEquals("Root type 'MissingRootType' for operation 'QUERY' not found in schema", exception.message)
        } finally {
            testDir.deleteRecursively()
        }
    }

    @Test
    fun `verify non-object root type throws meaningful exception`() {
        val testDir = Files.createTempDirectory("graphql-client-generator-root-types").toFile()
        try {
            val schemaFile = writeFile(
                testDir,
                "schema.graphql",
                """
                schema {
                    query: RootScalar
                }

                scalar RootScalar
                """
            )
            val queryFile = writeFile(
                testDir,
                "nonObjectRootQuery.graphql",
                """
                query NonObjectRootQuery {
                    __typename
                }
                """
            )

            val exception = assertFails {
                GraphQLClientGenerator(schemaFile.absolutePath, defaultConfig).generate(listOf(queryFile))
            }
            assertEquals(
                "Root type 'RootScalar' is not an ObjectTypeDefinition (found ScalarTypeDefinition)",
                exception.message
            )
        } finally {
            testDir.deleteRecursively()
        }
    }

    companion object {
        @JvmStatic
        fun invalidTests(): List<Arguments> = locateTestCaseArguments("src/test/data/invalid")
    }
}
