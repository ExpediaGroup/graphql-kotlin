/*
 * Copyright 2022 Expedia, Inc
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

package com.expediagroup.graphql.generator.execution

import com.expediagroup.graphql.generator.annotations.GraphQLName
import graphql.GraphQLContext
import graphql.GraphQLException
import graphql.schema.DataFetchingEnvironment
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.coroutineScope
import org.junit.jupiter.api.Test
import java.util.concurrent.CompletableFuture
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class FunctionDataFetcherTest {

    interface MyInterface {
        fun print(string: String): String
    }

    class MyClass : MyInterface {
        override fun print(string: String) = string

        fun printDefault(string: String? = "hello") = string

        fun printList(items: List<String>) = items.joinToString(separator = ":")

        fun contextClass(environment: DataFetchingEnvironment): String = environment.graphQlContext.get("value")

        fun dataFetchingEnvironment(environment: DataFetchingEnvironment): String = environment.field.name

        suspend fun suspendPrint(string: String): String = coroutineScope {
            string
        }

        fun throwException() { throw GraphQLException("Test Exception") }

        suspend fun suspendThrow(): String = coroutineScope<String> {
            throw GraphQLException("Suspended Exception")
        }

        @GraphQLName("myCustomField")
        fun renamedFields(@GraphQLName("myCustomArgument") arg: MyInputClass) = "You sent ${arg.field1}"

        fun optionalWrapper(input: OptionalInput<String>): String = when (input) {
            is OptionalInput.Undefined -> "input was UNDEFINED"
            is OptionalInput.Defined -> "input was ${input.value}"
        }

        fun optionalListInputObjects(input: OptionalInput<List<MyInputClass>>): String = when (input) {
            is OptionalInput.Undefined -> "input was UNDEFINED"
            is OptionalInput.Defined -> "first input was ${input.value?.first()?.field1}"
        }

        fun optionalWrapperClass(input: InputWrapper) = when (input.optional) {
            is OptionalInput.Undefined -> "optional was UNDEFINED"
            is OptionalInput.Defined -> "optional was ${input.optional.value}"
        }
    }

    data class InputWrapper(val required: String, val optional: OptionalInput<String>)

    @GraphQLName("MyInputClassRenamed")
    data class MyInputClass(
        @GraphQLName("jacksonField")
        val field1: String
    )

    @Test
    fun `null target and null source returns null`() {
        val dataFetcher = FunctionDataFetcher(target = null, fn = MyClass::print)
        val mockEnvironment: DataFetchingEnvironment = mockk {
            every { getSource<Any>() } returns null
        }
        assertNull(dataFetcher.get(mockEnvironment))
    }

    @Test
    fun `null target and valid source returns the value`() {
        val dataFetcher = FunctionDataFetcher(target = null, fn = MyClass::print)
        val mockEnvironment: DataFetchingEnvironment = mockk {
            every { getSource<Any>() } returns MyClass()
            every { arguments } returns mapOf("string" to "hello")
            every { containsArgument("string") } returns true
        }
        assertEquals(expected = "hello", actual = dataFetcher.get(mockEnvironment))
    }

    @Test
    fun `valid target and null source returns the value`() {
        val dataFetcher = FunctionDataFetcher(target = MyClass(), fn = MyClass::print)
        val mockEnvironment: DataFetchingEnvironment = mockk {
            every { arguments } returns mapOf("string" to "hello")
            every { containsArgument("string") } returns true
        }
        assertEquals(expected = "hello", actual = dataFetcher.get(mockEnvironment))
    }

    @Test
    fun `valid target with context class`() {
        val dataFetcher = FunctionDataFetcher(target = MyClass(), fn = MyClass::contextClass)
        val mockEnvironment: DataFetchingEnvironment = mockk {
            every { graphQlContext } returns GraphQLContext.of(
                mapOf(
                    "value" to "foo"
                )
            )
            every { arguments } returns emptyMap()
            every { containsArgument(any()) } returns false
        }
        assertEquals(expected = "foo", actual = dataFetcher.get(mockEnvironment))
    }

    @Test
    fun `target is different than the function instance`() {
        val dataFetcher = FunctionDataFetcher(target = MyClass(), fn = MyInterface::print)
        val mockEnvironment: DataFetchingEnvironment = mockk {
            every { getSource<Any>() } returns MyClass()
            every { arguments } returns mapOf("string" to "hello")
            every { containsArgument("string") } returns true
        }
        assertEquals(expected = "hello", actual = dataFetcher.get(mockEnvironment))
    }

    @Test
    fun `default values are used when argument is not present`() {
        val dataFetcher = FunctionDataFetcher(target = null, fn = MyClass::printDefault)
        val mockEnvironment: DataFetchingEnvironment = mockk {
            every { getSource<Any>() } returns MyClass()
            every { arguments } returns emptyMap()
            every { containsArgument(any()) } returns false
        }
        assertEquals(expected = "hello", actual = dataFetcher.get(mockEnvironment))
    }

    @Test
    fun `default values are overridden when argument is passed in`() {
        val dataFetcher = FunctionDataFetcher(target = null, fn = MyClass::printDefault)
        val mockEnvironment: DataFetchingEnvironment = mockk {
            every { getSource<Any>() } returns MyClass()
            every { arguments } returns mapOf("string" to "foo")
            every { containsArgument("string") } returns true
        }
        assertEquals(expected = "foo", actual = dataFetcher.get(mockEnvironment))
    }

    @Test
    fun `default values are overridden when argument is passed as null`() {
        val dataFetcher = FunctionDataFetcher(target = null, fn = MyClass::printDefault)
        val mockEnvironment: DataFetchingEnvironment = mockk {
            every { getSource<Any>() } returns MyClass()
            every { arguments } returns mapOf("string" to null)
            every { containsArgument("string") } returns true
        }
        assertNull(dataFetcher.get(mockEnvironment))
    }

    @Test
    fun `list inputs can be converted`() {
        val dataFetcher = FunctionDataFetcher(target = MyClass(), fn = MyClass::printList)
        val mockEnvironment: DataFetchingEnvironment = mockk {
            every { arguments } returns mapOf("items" to listOf("foo", "bar"))
            every { containsArgument("items") } returns true
        }

        assertEquals(expected = "foo:bar", actual = dataFetcher.get(mockEnvironment))
    }

    @Test
    fun `dataFetchingEnvironment is passed as an argument`() {
        val dataFetcher = FunctionDataFetcher(target = MyClass(), fn = MyClass::dataFetchingEnvironment)
        val mockEnvironment: DataFetchingEnvironment = mockk {
            every { arguments } returns emptyMap()
            every { containsArgument(any()) } returns false
            every { field } returns mockk {
                every { name } returns "fooBarBaz"
            }
        }
        assertEquals(expected = "fooBarBaz", actual = dataFetcher.get(mockEnvironment))
    }

    @Test
    fun `suspend functions return value wrapped in CompletableFuture`() {
        val dataFetcher = FunctionDataFetcher(target = MyClass(), fn = MyClass::suspendPrint)
        val mockEnvironment: DataFetchingEnvironment = mockk {
            every { arguments } returns mapOf("string" to "hello")
            every { containsArgument("string") } returns true
            every { graphQlContext } returns GraphQLContext.newContext().build()
        }

        val result = dataFetcher.get(mockEnvironment)

        assertTrue(result is CompletableFuture<*>)
        assertEquals(expected = "hello", actual = result.get())
    }

    @Test
    fun `throwException function propagates the original exception`() {
        val dataFetcher = FunctionDataFetcher(target = MyClass(), fn = MyClass::throwException)
        val mockEnvironment: DataFetchingEnvironment = mockk {
            every { arguments } returns emptyMap()
            every { containsArgument(any()) } returns false
        }

        try {
            dataFetcher.get(mockEnvironment)
            assertFalse(true, "Should not be here")
        } catch (e: Exception) {
            assertEquals(e.message, "Test Exception")
        }
    }

    @Test
    fun `suspendThrow throws exception when resolved`() {
        val dataFetcher = FunctionDataFetcher(target = MyClass(), fn = MyClass::suspendThrow)
        val mockEnvironment: DataFetchingEnvironment = mockk {
            every { arguments } returns emptyMap()
            every { containsArgument(any()) } returns false
            every { graphQlContext } returns GraphQLContext.newContext().build()
        }

        try {
            val result = dataFetcher.get(mockEnvironment)
            assertTrue(result is CompletableFuture<*>)
            result.get()
            assertFalse(true, "Should not be here")
        } catch (e: Exception) {
            val message = e.message
            assertNotNull(message)
            assertTrue(message.endsWith("Suspended Exception"), "Exception from function is not returned")
        }
    }

    @Test
    fun `renamed fields can be converted`() {
        val dataFetcher = FunctionDataFetcher(target = MyClass(), fn = MyClass::renamedFields)
        val mockEnvironment: DataFetchingEnvironment = mockk {
            every { arguments } returns mapOf("myCustomArgument" to mapOf("jacksonField" to "foo"))
            every { containsArgument("myCustomArgument") } returns true
        }
        assertEquals(expected = "You sent foo", actual = dataFetcher.get(mockEnvironment))
    }

    @Test
    fun `optional inputs return the value when arguments are passed`() {
        val dataFetcher = FunctionDataFetcher(target = MyClass(), fn = MyClass::optionalWrapper)
        val mockEnvironment: DataFetchingEnvironment = mockk {
            every { arguments } returns mapOf("input" to "hello")
            every { containsArgument("input") } returns true
        }
        assertEquals(expected = "input was hello", actual = dataFetcher.get(mockEnvironment))
    }

    @Test
    fun `optional inputs return the value when null arguments are passed`() {
        val dataFetcher = FunctionDataFetcher(target = MyClass(), fn = MyClass::optionalWrapper)
        val mockEnvironment: DataFetchingEnvironment = mockk {
            every { arguments } returns mapOf("input" to null)
            every { containsArgument("input") } returns true
        }
        assertEquals(expected = "input was null", actual = dataFetcher.get(mockEnvironment))
    }

    @Test
    fun `optional inputs return undefined when arguments are empty`() {
        val dataFetcher = FunctionDataFetcher(target = MyClass(), fn = MyClass::optionalWrapper)
        val mockEnvironment: DataFetchingEnvironment = mockk {
            every { arguments } returns emptyMap()
            every { containsArgument(any()) } returns false
        }
        assertEquals(expected = "input was UNDEFINED", actual = dataFetcher.get(mockEnvironment))
    }

    @Test
    fun `optional list of input objects is deserialized correctly`() {
        val dataFetcher = FunctionDataFetcher(target = MyClass(), fn = MyClass::optionalListInputObjects)
        val mockEnvironment: DataFetchingEnvironment = mockk {
            every { arguments } returns mapOf("input" to listOf(linkedMapOf("jacksonField" to "foo")))
            every { containsArgument("input") } returns true
        }
        val result = dataFetcher.get(mockEnvironment)
        assertEquals(expected = "first input was foo", actual = result)
    }

    @Test
    fun `optional inputs inside class return the value when arguments are passed`() {
        val dataFetcher = FunctionDataFetcher(target = MyClass(), fn = MyClass::optionalWrapperClass)
        val mockEnvironment: DataFetchingEnvironment = mockk {
            every { arguments } returns mapOf("input" to mapOf("required" to "hello", "optional" to "hello"))
            every { containsArgument("input") } returns true
        }
        assertEquals(expected = "optional was hello", actual = dataFetcher.get(mockEnvironment))
    }

    @Test
    fun `optional inputs inside class return undefined when arguments are empty`() {
        val dataFetcher = FunctionDataFetcher(target = MyClass(), fn = MyClass::optionalWrapperClass)
        val mockEnvironment = mockk<DataFetchingEnvironment> {
            every { arguments } returns mapOf("input" to mapOf("required" to "hello"))
            every { containsArgument("input") } returns true
        }
        assertEquals(expected = "optional was UNDEFINED", actual = dataFetcher.get(mockEnvironment))
    }
}
