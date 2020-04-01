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

package com.expediagroup.graphql.plugin.generator

import com.expediagroup.graphql.client.converter.CustomScalarConverter
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

internal class UUIDConverter : CustomScalarConverter<UUID> {
    override fun toScalar(rawValue: String): UUID = UUID.fromString(rawValue)
    override fun toJson(value: UUID): String = value.toString()
}

internal fun verifyGraphQLClientGeneration(
    query: String,
    expected: String,
    graphQLConfig: GraphQLClientGeneratorConfig = GraphQLClientGeneratorConfig(packageName = "com.expediagroup.graphql.plugin.generator.integration")
) {
    val queryFile = createTempFile(suffix = ".graphql")
    queryFile.deleteOnExit()
    queryFile.writeText(query)

    val generator = GraphQLClientGenerator(testSchema(), graphQLConfig)
    val fileSpec = generator.generate(queryFile)

    assertEquals(expected, fileSpec.toString().trim())
}
