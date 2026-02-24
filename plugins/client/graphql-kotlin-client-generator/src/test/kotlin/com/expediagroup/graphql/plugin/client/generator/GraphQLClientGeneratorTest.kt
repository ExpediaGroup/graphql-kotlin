/*
 * Copyright 2024 Expedia, Inc
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

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class GraphQLClientGeneratorTest {

    @Test
    fun `verify generation works with implicit schema definition`(@TempDir tempDir: File) {
        val queryFile = File(tempDir, "HelloQuery.graphql")
        queryFile.writeText("query HelloQuery { hello }")

        val config = GraphQLClientGeneratorConfig(packageName = "com.expediagroup.graphql.generated")
        val generator = GraphQLClientGenerator("implicitSchemaDefinition.graphql", config)
        val fileSpecs = generator.generate(listOf(queryFile))

        assertTrue(fileSpecs.isNotEmpty())
        val helloQuerySpec = fileSpecs.find { it.name == "HelloQuery" }
        assertTrue(helloQuerySpec != null, "HelloQuery file spec should be generated")
    }

    @Test
    fun `verify generation fails with multiple operations in file`(@TempDir tempDir: File) {
        val queryFile = File(tempDir, "MultiOp.graphql")
        queryFile.writeText("""
            query FirstQuery { hello }
            query SecondQuery { hello }
        """.trimIndent())

        val config = GraphQLClientGeneratorConfig(packageName = "com.expediagroup.graphql.generated")
        val generator = GraphQLClientGenerator("implicitSchemaDefinition.graphql", config)
        assertFailsWith<Exception> {
            generator.generate(listOf(queryFile))
        }
    }
}
