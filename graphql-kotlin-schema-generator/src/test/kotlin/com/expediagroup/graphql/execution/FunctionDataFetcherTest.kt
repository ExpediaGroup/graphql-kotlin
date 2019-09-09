/*
 * Copyright 2019 Expedia Group, Inc.
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

package com.expediagroup.graphql.execution

import com.expediagroup.graphql.annotations.GraphQLContext
import com.expediagroup.graphql.exceptions.CouldNotCastArgumentException
import graphql.GraphQLException
import graphql.schema.DataFetchingEnvironment
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import org.junit.jupiter.api.Test
import java.util.concurrent.CompletableFuture
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlin.test.assertFalse

internal class FunctionDataFetcherTest {

    internal class MyClass {
        fun print(string: String) = string

        fun printArray(items: Array<String>) = items.joinToString(separator = ":")

        fun printList(items: List<String>) = items.joinToString(separator = ":")

        fun context(@GraphQLContext string: String) = string

        fun dataFetchingEnvironment(environment: DataFetchingEnvironment) = environment.field.name

        suspend fun suspendPrint(string: String): String = coroutineScope {
            delay(10)
            string
        }

        fun throwException() { throw GraphQLException("Test Exception") }

        suspend fun suspendThrow(): String = coroutineScope {
            delay(10)
            throw GraphQLException("Suspended Exception")
        }
    }

    @Test
    fun `null target and null source returns null`() {
        val dataFetcher = FunctionDataFetcher(target = null, fn = MyClass::print)
        val mockEnvironmet: DataFetchingEnvironment = mockk()
        every { mockEnvironmet.getSource<Any>() } returns null
        assertNull(dataFetcher.get(mockEnvironmet))
    }

    @Test
    fun `null target and valid source returns the value`() {
        val dataFetcher = FunctionDataFetcher(target = null, fn = MyClass::print)
        val mockEnvironmet: DataFetchingEnvironment = mockk()
        every { mockEnvironmet.getSource<Any>() } returns MyClass()
        every { mockEnvironmet.arguments } returns mapOf("string" to "hello")
        assertEquals(expected = "hello", actual = dataFetcher.get(mockEnvironmet))
    }

    @Test
    fun `valid target and null source returns the value`() {
        val dataFetcher = FunctionDataFetcher(target = MyClass(), fn = MyClass::print)
        val mockEnvironmet: DataFetchingEnvironment = mockk()
        every { mockEnvironmet.arguments } returns mapOf("string" to "hello")
        assertEquals(expected = "hello", actual = dataFetcher.get(mockEnvironmet))
    }

    @Test
    fun `valid target with context`() {
        val dataFetcher = FunctionDataFetcher(target = MyClass(), fn = MyClass::context)
        val mockEnvironmet: DataFetchingEnvironment = mockk()
        every { mockEnvironmet.getContext<String>() } returns "foo"
        assertEquals(expected = "foo", actual = dataFetcher.get(mockEnvironmet))
    }

    @Test
    fun `valid target and value from predicate`() {
        val mockPredicate: DataFetcherExecutionPredicate = mockk()
        every { mockPredicate.evaluate<String>(any(), any(), any()) } returns "baz"
        val dataFetcher = FunctionDataFetcher(target = MyClass(), fn = MyClass::print, executionPredicate = mockPredicate)
        val mockEnvironmet: DataFetchingEnvironment = mockk()
        every { mockEnvironmet.arguments } returns mapOf("string" to "hello")
        assertEquals(expected = "baz", actual = dataFetcher.get(mockEnvironmet))
    }

    @Test
    fun `valid target and null from predicate`() {
        val mockPredicate: DataFetcherExecutionPredicate = mockk()
        every { mockPredicate.evaluate<String?>(any(), any(), any()) } returns null
        val dataFetcher = FunctionDataFetcher(target = MyClass(), fn = MyClass::print, executionPredicate = mockPredicate)
        val mockEnvironmet: DataFetchingEnvironment = mockk()
        every { mockEnvironmet.arguments } returns mapOf("string" to "hello")
        assertEquals(expected = "hello", actual = dataFetcher.get(mockEnvironmet))
    }

    @Test
    fun `array inputs can be converted by the object mapper`() {
        val dataFetcher = FunctionDataFetcher(target = MyClass(), fn = MyClass::printArray, executionPredicate = null)
        val mockEnvironmet: DataFetchingEnvironment = mockk()
        every { mockEnvironmet.arguments } returns mapOf("items" to arrayOf("foo", "bar"))
        assertEquals(expected = "foo:bar", actual = dataFetcher.get(mockEnvironmet))
    }

    @Test
    fun `list inputs throws exception`() {
        val dataFetcher = FunctionDataFetcher(target = MyClass(), fn = MyClass::printList, executionPredicate = null)
        val mockEnvironmet: DataFetchingEnvironment = mockk()
        every { mockEnvironmet.arguments } returns mapOf("items" to listOf("foo", "bar"))

        assertFailsWith(CouldNotCastArgumentException::class) {
            dataFetcher.get(mockEnvironmet)
        }
    }

    @Test
    fun `dataFetchingEnvironement is passed as an argument`() {
        val dataFetcher = FunctionDataFetcher(target = MyClass(), fn = MyClass::dataFetchingEnvironment)
        val mockEnvironmet: DataFetchingEnvironment = mockk()
        every { mockEnvironmet.field } returns mockk {
            every { name } returns "fooBarBaz"
        }
        assertEquals(expected = "fooBarBaz", actual = dataFetcher.get(mockEnvironmet))
    }

    @Test
    fun `suspend functions return value wrapped in CompletableFuture`() {
        val dataFetcher = FunctionDataFetcher(target = MyClass(), fn = MyClass::suspendPrint)
        val mockEnvironmet: DataFetchingEnvironment = mockk()
        every { mockEnvironmet.arguments } returns mapOf("string" to "hello")

        val result = dataFetcher.get(mockEnvironmet)

        assertTrue(result is CompletableFuture<*>)
        assertEquals(expected = "hello", actual = result.get())
    }

    @Test
    fun `throwException function propagates the original exception`() {
        val dataFetcher = FunctionDataFetcher(target = MyClass(), fn = MyClass::throwException)
        val mockEnvironmet: DataFetchingEnvironment = mockk()

        try {
            dataFetcher.get(mockEnvironmet)
            assertFalse(true, "Should not be here")
        } catch (e: Exception) {
            assertEquals(e.message, "Test Exception")
        }
    }

    @Test
    fun `suspendThrow throws exception when resolved`() {
        val dataFetcher = FunctionDataFetcher(target = MyClass(), fn = MyClass::suspendThrow)
        val mockEnvironmet: DataFetchingEnvironment = mockk()

        try {
            val result = dataFetcher.get(mockEnvironmet)
            assertTrue(result is CompletableFuture<*>)
            result.get()
            assertFalse(true, "Should not be here")
        } catch (e: Exception) {
            val message = e.message
            assertNotNull(message)
            assertTrue(message.endsWith("Suspended Exception"))
        }
    }
}
