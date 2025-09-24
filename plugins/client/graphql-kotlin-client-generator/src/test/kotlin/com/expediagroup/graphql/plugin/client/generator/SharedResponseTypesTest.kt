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

import org.junit.jupiter.api.Test
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SharedResponseTypesTest {

    @Test
    fun `verify shared response types are generated when feature is enabled`() {
        val configWithSharedTypes = GraphQLClientGeneratorConfig(
            packageName = "com.expediagroup.graphql.generated",
            useSharedResponseTypes = true
        )

        val testDir = File("src/test/data/kotlinx/multiple_queries")
        val queries = testDir.walkTopDown()
            .filter { it.name.endsWith(".graphql") }
            .toList()

        val generator = GraphQLClientGenerator(TEST_SCHEMA_PATH, configWithSharedTypes)
        val fileSpecs = generator.generate(queries)

        assertTrue(fileSpecs.isNotEmpty())

        // Check if shared response types are generated in .responses package
        val sharedResponseTypes = fileSpecs.filter { it.packageName.endsWith(".responses") }
        assertTrue(sharedResponseTypes.isNotEmpty(), "Expected shared response types to be generated")

        // Verify that ComplexObject is generated as a shared type
        val complexObjectSpec = sharedResponseTypes.find { it.name == "ComplexObject" }
        assertTrue(complexObjectSpec != null, "Expected ComplexObject to be generated as shared response type")
        assertEquals("com.expediagroup.graphql.generated.responses", complexObjectSpec.packageName)
    }

    @Test
    fun `verify shared response types are not generated when feature is disabled`() {
        val configWithoutSharedTypes = GraphQLClientGeneratorConfig(
            packageName = "com.expediagroup.graphql.generated",
            useSharedResponseTypes = false
        )

        val testDir = File("src/test/data/kotlinx/multiple_queries")
        val queries = testDir.walkTopDown()
            .filter { it.name.endsWith(".graphql") }
            .toList()

        val generator = GraphQLClientGenerator(TEST_SCHEMA_PATH, configWithoutSharedTypes)
        val fileSpecs = generator.generate(queries)

        assertTrue(fileSpecs.isNotEmpty())

        // Check that no shared response types are generated
        val sharedResponseTypes = fileSpecs.filter { it.packageName.endsWith(".responses") }
        assertEquals(0, sharedResponseTypes.size, "Expected no shared response types when feature is disabled")
    }

    @Test
    fun `verify config flag controls shared response type behavior`() {
        // Test with feature enabled
        val configEnabled = GraphQLClientGeneratorConfig(
            packageName = "com.expediagroup.graphql.generated",
            useSharedResponseTypes = true
        )
        assertTrue(configEnabled.useSharedResponseTypes, "Expected useSharedResponseTypes to be true when enabled")

        // Test with feature disabled (default)
        val configDisabled = GraphQLClientGeneratorConfig(
            packageName = "com.expediagroup.graphql.generated"
        )
        assertFalse(configDisabled.useSharedResponseTypes, "Expected useSharedResponseTypes to be false by default")

        // Test with feature explicitly disabled
        val configExplicitlyDisabled = GraphQLClientGeneratorConfig(
            packageName = "com.expediagroup.graphql.generated",
            useSharedResponseTypes = false
        )
        assertFalse(configExplicitlyDisabled.useSharedResponseTypes, "Expected useSharedResponseTypes to be false when explicitly disabled")
    }
}
