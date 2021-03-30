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

package com.expediagroup.graphql.server.spring.subscriptions

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SubscriptionUtilsKtTest {

    @Test
    fun `casting null returns empty map`() {
        val result = castToMapOfStringString(null)
        assertTrue(result.isEmpty())
    }

    @Test
    fun `casting non-map returns empty map`() {
        val result = castToMapOfStringString("foo")
        assertTrue(result.isEmpty())
    }

    @Test
    fun `casting empty map returns empty map`() {
        val result = castToMapOfStringString(emptyMap<String, String>())
        assertTrue(result.isEmpty())
    }

    @Test
    fun `casting map of String-Int returns empty map`() {
        val result = castToMapOfStringString(mapOf("foo" to 1))
        assertTrue(result.isEmpty())
    }

    @Test
    fun `casting map of Int-String returns empty map`() {
        val result = castToMapOfStringString(mapOf(1 to "foo"))
        assertTrue(result.isEmpty())
    }

    @Test
    fun `casting map of String-String returns the map`() {
        val result = castToMapOfStringString(mapOf("foo" to "bar"))
        assertFalse(result.isEmpty())
        assertEquals("bar", result["foo"])
    }
}
