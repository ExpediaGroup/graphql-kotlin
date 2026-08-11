/*
 * Copyright 2026 Expedia, Inc
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

package com.expediagroup.graphql.plugin.schema

import com.expediagroup.graphql.generator.hooks.NoopSchemaGeneratorHooks
import com.expediagroup.graphql.generator.hooks.SchemaGeneratorHooks
import com.expediagroup.graphql.plugin.schema.hooks.SchemaGeneratorHooksProvider
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.slf4j.LoggerFactory
import kotlin.test.assertEquals
import kotlin.test.assertSame
import kotlin.test.assertTrue

class SelectHooksProviderTest {

    private val logger = LoggerFactory.getLogger(SelectHooksProviderTest::class.java)

    private class FixedPriorityProvider(private val priority: Int) : SchemaGeneratorHooksProvider {
        override fun hooks(): SchemaGeneratorHooks = NoopSchemaGeneratorHooks
        override fun priority(): Int = priority
    }

    private class DefaultPriorityProvider : SchemaGeneratorHooksProvider {
        override fun hooks(): SchemaGeneratorHooks = NoopSchemaGeneratorHooks
    }

    @Test
    fun `requires a non-empty provider list`() {
        val exception = assertThrows<IllegalArgumentException> {
            selectHooksProvider(emptyList(), logger)
        }
        assertTrue(exception.message!!.contains("empty list of providers"))
    }

    @Test
    fun `returns the single provider when only one is registered`() {
        val only = DefaultPriorityProvider()
        val selected = selectHooksProvider(listOf(only), logger)
        assertSame(only, selected)
    }

    @Test
    fun `selects the provider with the highest priority when multiple are registered`() {
        val low = FixedPriorityProvider(priority = 0)
        val high = FixedPriorityProvider(priority = 10)
        val mid = FixedPriorityProvider(priority = 5)

        val selected = selectHooksProvider(listOf(low, mid, high), logger)

        assertSame(high, selected)
    }

    @Test
    fun `throws when the highest priority is tied between multiple providers`() {
        val first = FixedPriorityProvider(priority = 5)
        val second = FixedPriorityProvider(priority = 5)

        val exception = assertThrows<RuntimeException> {
            selectHooksProvider(listOf(first, second), logger)
        }

        assertTrue(
            exception.message!!.contains("Cannot generate SDL as multiple SchemaGeneratorHooksProviders"),
            "expected the legacy error prefix to be preserved but was: ${exception.message}"
        )
        assertTrue(
            exception.message!!.contains("priority (5)"),
            "expected the tied priority value to be in the message but was: ${exception.message}"
        )
        assertTrue(
            exception.message!!.contains(FixedPriorityProvider::class.java.name),
            "expected the tied provider class name to be in the message but was: ${exception.message}"
        )
    }

    @Test
    fun `throws when all providers share the default priority`() {
        val first = DefaultPriorityProvider()
        val second = DefaultPriorityProvider()

        val exception = assertThrows<RuntimeException> {
            selectHooksProvider(listOf(first, second), logger)
        }

        assertTrue(
            exception.message!!.contains("priority (0)"),
            "expected tie at default priority (0) to be reported, was: ${exception.message}"
        )
    }

    @Test
    fun `default priority on the SPI is zero`() {
        assertEquals(0, DefaultPriorityProvider().priority())
    }
}
