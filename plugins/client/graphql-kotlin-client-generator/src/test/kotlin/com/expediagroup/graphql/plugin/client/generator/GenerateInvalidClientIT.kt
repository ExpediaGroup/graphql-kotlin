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

    companion object {
        @JvmStatic
        fun invalidTests(): List<Arguments> = locateTestCaseArguments("src/test/data/invalid")
    }
}
