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

package com.expediagroup.graphql.plugin.client.generator

import com.expediagroup.graphql.client.converter.ScalarConverter
import com.ibm.icu.util.ULocale
import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.junit.jupiter.params.provider.Arguments
import java.io.File
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.fail

internal val defaultConfig = GraphQLClientGeneratorConfig(packageName = "com.expediagroup.graphql.generated")

internal fun locateTestCaseArguments(directory: String) = File(directory)
    .listFiles()
    ?.filter { it.isDirectory }
    ?.map {
        Arguments.of(it)
    } ?: emptyList()

internal fun locateTestFiles(directory: File): Pair<List<File>, Map<String, File>> {
    val testInput = directory.walkTopDown()
    val queries = testInput.filter { it.name.endsWith(".graphql") }.toList()
    val expectedFiles = testInput.filter { it.name.endsWith(".kt") }.associateBy {
        val subpackage = if (it.parentFile != directory) {
            "${it.parentFile.name}."
        } else {
            ""
        }
        "com.expediagroup.graphql.generated.$subpackage${it.name.removeSuffix(".kt")}"
    }

    return queries to expectedFiles
}

internal const val TEST_SCHEMA_PATH = "testSchema.graphql"

@OptIn(ExperimentalCompilerApi::class)
internal fun verifyClientGeneration(config: GraphQLClientGeneratorConfig, testDirectory: File) {
    val (queries, expectedFiles) = locateTestFiles(testDirectory)

    val generator = GraphQLClientGenerator(TEST_SCHEMA_PATH, config)
    val fileSpecs = generator.generate(queries)
    assertTrue(fileSpecs.isNotEmpty())
    assertEquals(expectedFiles.size, fileSpecs.size)
    for (spec in fileSpecs) {
        val expected = expectedFiles[spec.packageName + "." + spec.name]?.readText()
        assertEquals(expected, spec.toString())
    }

    val generatedSources: List<SourceFile> = fileSpecs.map { spec ->
        val fileName = spec.packageName + "." + spec.name + ".kt"
        SourceFile.kotlin(fileName, spec.toString())
    }

    val compilationResult = KotlinCompilation().apply {
        jvmTarget = "17"
        sources = generatedSources
        inheritClassPath = true
        if (config.serializer == GraphQLSerializer.KOTLINX) {
        }
    }.compile()
    if (compilationResult.exitCode != KotlinCompilation.ExitCode.OK &&
        (testDirectory.parentFile.name != "kotlinx" || !setOf("custom_scalar_input", "multiple_queries", "variables").contains(testDirectory.name))
    ) {
        fail("failed to compile generated files: ${compilationResult.messages}")
    }
}

// used by integration tests
class UUIDScalarConverter : ScalarConverter<UUID> {
    override fun toScalar(rawValue: Any): UUID = UUID.fromString(rawValue.toString())
    override fun toJson(value: UUID): String = value.toString()
}

class ULocaleScalarConverter : ScalarConverter<ULocale> {
    override fun toScalar(rawValue: Any): ULocale = ULocale(rawValue.toString())
    override fun toJson(value: ULocale): Any = value.toString()
}
