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
import com.squareup.kotlinpoet.FileSpec
import graphql.schema.idl.SchemaParser
import graphql.schema.idl.TypeDefinitionRegistry
import java.util.UUID
import kotlin.test.assertEquals

internal fun testSchema(): TypeDefinitionRegistry {
    val schemaFileStream = ClassLoader.getSystemClassLoader().getResourceAsStream("testSchema.graphql") ?: throw RuntimeException("unable to locate test schema")
    return schemaFileStream.use {
        SchemaParser().parse(schemaFileStream)
    }
}

internal fun generateTestFileSpec(
    query: String,
    graphQLConfig: GraphQLClientGeneratorConfig = GraphQLClientGeneratorConfig(packageName = "com.expediagroup.graphql.plugin.generator.integration")
): List<FileSpec> {
    val queryFile = createTempFile(suffix = ".graphql")
    queryFile.deleteOnExit()
    queryFile.writeText(query)

    val generator = GraphQLClientGenerator(testSchema(), graphQLConfig)
    return generator.generate(listOf(queryFile))
}

internal fun verifyGeneratedFileSpecContents(
    query: String,
    expected: String,
    graphQLConfig: GraphQLClientGeneratorConfig = GraphQLClientGeneratorConfig(packageName = "com.expediagroup.graphql.plugin.generator.integration")
) {
    val fileSpecs = generateTestFileSpec(query, graphQLConfig)
    assertEquals(expected, fileSpecs.first().toString().trim())
}

class UUIDScalarConverter : ScalarConverter<UUID> {
    override fun toScalar(rawValue: Any): UUID = UUID.fromString(rawValue.toString())
    override fun toJson(value: UUID): String = value.toString()
}
