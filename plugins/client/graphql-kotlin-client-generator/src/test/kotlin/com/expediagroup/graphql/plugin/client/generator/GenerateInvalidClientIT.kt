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
import org.junit.jupiter.api.io.TempDir
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.io.File
import java.nio.file.Path
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertFailsWith

class GenerateInvalidClientIT {

    @TempDir
    lateinit var tempDir: Path

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
    fun `verify generation fails when SDL root type name does not exist in the registry`() {
        // schema declares `query: NonExistentQueryType` but that type is never defined
        val schemaFile = tempDir.resolve("schema.graphql").toFile().also {
            it.writeText(
                """
                schema { query: NonExistentQueryType }
                type Query { placeholder: String }
                """.trimIndent()
            )
        }
        val queryFile = tempDir.resolve("SimpleQuery.graphql").toFile().also {
            it.writeText("query SimpleQuery { placeholder }")
        }
        val generator = GraphQLClientGenerator(schemaFile.absolutePath, defaultConfig)
        val exception = assertFailsWith<IllegalStateException> {
            generator.generate(listOf(queryFile))
        }
        assertContains(exception.message!!, "NonExistentQueryType")
    }

    @Test
    fun `verify generation fails when operation type is not declared in the SDL schema definition`() {
        // schema only declares a mutation root — no query root — so looking up "QUERY" in the map yields null
        val schemaFile = tempDir.resolve("schema.graphql").toFile().also {
            it.writeText(
                """
                schema { mutation: Mutation }
                type Mutation { doSomething: String }
                """.trimIndent()
            )
        }
        val queryFile = tempDir.resolve("SimpleQuery.graphql").toFile().also {
            it.writeText("query SimpleQuery { doSomething }")
        }
        val generator = GraphQLClientGenerator(schemaFile.absolutePath, defaultConfig)
        val exception = assertFailsWith<IllegalStateException> {
            generator.generate(listOf(queryFile))
        }
        assertContains(exception.message!!, "QUERY")
    }

    @Test
    fun `verify generation fails when SDL root type resolves to a non-ObjectTypeDefinition`() {
        // schema points `query` at an enum instead of an object type
        val schemaFile = tempDir.resolve("schema.graphql").toFile().also {
            it.writeText(
                """
                schema { query: NotAnObject }
                enum NotAnObject { VALUE }
                """.trimIndent()
            )
        }
        val queryFile = tempDir.resolve("SimpleQuery.graphql").toFile().also {
            it.writeText("query SimpleQuery { placeholder }")
        }
        val generator = GraphQLClientGenerator(schemaFile.absolutePath, defaultConfig)
        val exception = assertFailsWith<IllegalStateException> {
            generator.generate(listOf(queryFile))
        }
        assertContains(exception.message!!, "NotAnObject")
        assertContains(exception.message!!, "ObjectTypeDefinition")
    }

    companion object {
        @JvmStatic
        fun invalidTests(): List<Arguments> = locateTestCaseArguments("src/test/data/invalid")
    }
}
