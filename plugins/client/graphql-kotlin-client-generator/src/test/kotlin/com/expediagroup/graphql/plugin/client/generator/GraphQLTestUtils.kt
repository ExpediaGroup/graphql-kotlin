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

import com.expediagroup.graphql.client.converter.ScalarConverter
import graphql.schema.idl.SchemaParser
import graphql.schema.idl.TypeDefinitionRegistry
import org.junit.jupiter.params.provider.Arguments
import java.io.File
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertTrue

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
    val expectedFiles = testInput.filter { it.name.endsWith(".kt") }.associateBy { it.name.removeSuffix(".kt") }

    return queries to expectedFiles
}

internal fun testSchema(): TypeDefinitionRegistry {
    val schemaFileStream = ClassLoader.getSystemClassLoader().getResourceAsStream("testSchema.graphql") ?: throw RuntimeException("unable to locate test schema")
    return schemaFileStream.use {
        SchemaParser().parse(schemaFileStream)
    }
}

internal fun verifyClientGeneration(config: GraphQLClientGeneratorConfig, testDirectory: File) {
    val (queries, expectedFiles) = locateTestFiles(testDirectory)

    val generator = GraphQLClientGenerator(testSchema(), config)
    val fileSpecs = generator.generate(queries)
    assertTrue(fileSpecs.isNotEmpty())
    assertEquals(expectedFiles.size, fileSpecs.size)
    for (spec in fileSpecs) {
        val expected = expectedFiles[spec.name]?.readText()
        assertEquals(expected, spec.toString())
    }
}

// used by integration tests
class UUIDScalarConverter : ScalarConverter<UUID> {
    override fun toScalar(rawValue: Any): UUID = UUID.fromString(rawValue.toString())
    override fun toJson(value: UUID): String = value.toString()
}
