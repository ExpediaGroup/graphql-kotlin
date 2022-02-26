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

import graphql.schema.DataFetchingEnvironment
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import kotlin.reflect.full.findParameterByName
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull

class ConvertArgumentValueTest {

    @Test
    fun `string input is parsed`() {
        val kParam = assertNotNull(TestFunctions::stringInput.findParameterByName("input"))
        val result = convertArgumentValue("input", kParam, mapOf("input" to "hello"))
        assertEquals("hello", result)
    }

    @Test
    fun `pre-parsed object is returned`() {
        val kParam = assertNotNull(TestFunctions::inputObject.findParameterByName("input"))
        val result = convertArgumentValue("input", kParam, mapOf("input" to TestInput("hello")))
        val castResult = assertIs<TestInput>(result)
        assertEquals("hello", castResult.foo)
    }

    @Test
    fun `enum object is parsed`() {
        val kParam = assertNotNull(TestFunctions::enumInput.findParameterByName("input"))
        val inputValue = "BAR"
        val result = convertArgumentValue("input", kParam, mapOf("input" to inputValue))
        val castResult = assertIs<Foo>(result)
        assertEquals(Foo.BAR, castResult)
    }

    @Test
    fun `generic map object is parsed`() {
        val kParam = assertNotNull(TestFunctions::inputObject.findParameterByName("input"))
        val inputValue = mapOf(
            "foo" to "hello",
            "bar" to "world",
            "baz" to listOf("!")
        )
        val result = convertArgumentValue("input", kParam, mapOf("input" to inputValue))
        val castResult = assertIs<TestInput>(result)
        assertEquals("hello", castResult.foo)
        assertEquals("world", castResult.bar)
        assertEquals(listOf("!"), castResult.baz)
    }

    @Test
    fun `generic map object is parsed and defaults are used`() {
        val kParam = assertNotNull(TestFunctions::inputObject.findParameterByName("input"))
        val result = convertArgumentValue("input", kParam, mapOf("input" to mapOf("foo" to "hello")))
        val castResult = assertIs<TestInput>(result)
        assertEquals("hello", castResult.foo)
        assertEquals(null, castResult.bar)
        assertEquals(null, castResult.baz)
    }

    @Test
    fun `list string input is parsed`() {
        val kParam = assertNotNull(TestFunctions::listStringInput.findParameterByName("input"))
        val result = convertArgumentValue("input", kParam, mapOf("input" to listOf("hello")))
        assertEquals(listOf("hello"), result)
    }

    @Test
    fun `optional input when undefined is parsed`() {
        val kParam = assertNotNull(TestFunctions::optionalInput.findParameterByName("input"))
        val result = convertArgumentValue("input", kParam, mapOf())
        assertEquals(OptionalInput.Undefined, result)
    }

    @Test
    fun `optional input with defined null is parsed`() {
        val kParam = assertNotNull(TestFunctions::optionalInput.findParameterByName("input"))
        val result = convertArgumentValue("input", kParam, mapOf("input" to null))
        val castResult = assertIs<OptionalInput.Defined<*>>(result)
        assertEquals(null, castResult.value)
    }

    @Test
    fun `optional input with defined value is parsed`() {
        val kParam = assertNotNull(TestFunctions::optionalInput.findParameterByName("input"))
        val mockEnv = mockk<DataFetchingEnvironment> {
            every { containsArgument("input") } returns true
        }
        val result = convertArgumentValue("input", kParam, mapOf("input" to "hello"))
        val castResult = assertIs<OptionalInput.Defined<*>>(result)
        assertEquals("hello", castResult.value)
    }

    @Test
    fun `optional input with object is parsed`() {
        val kParam = assertNotNull(TestFunctions::optionalInputObject.findParameterByName("input"))
        val result = convertArgumentValue("input", kParam, mapOf("input" to TestInput("hello")))
        val castResult = assertIs<OptionalInput.Defined<*>>(result)
        val castResult2 = assertIs<TestInput>(castResult.value)
        assertEquals("hello", castResult2.foo)
    }

    @Test
    fun `optional input with list object is parsed`() {
        val kParam = assertNotNull(TestFunctions::optionalInputListObject.findParameterByName("input"))
        val result = convertArgumentValue("input", kParam, mapOf("input" to listOf(TestInput("hello"))))
        val castResult = assertIs<OptionalInput.Defined<*>>(result)
        val castResult2 = assertIs<List<TestInput>>(castResult.value)
        assertEquals("hello", castResult2.firstOrNull()?.foo)
    }

    class TestFunctions {
        fun stringInput(input: String): String = TODO()
        fun listStringInput(input: List<String>): String = TODO()
        fun enumInput(input: Foo): String = TODO()
        fun inputObject(input: TestInput): String = TODO()
        fun optionalInput(input: OptionalInput<String>): String = TODO()
        fun optionalInputObject(input: OptionalInput<TestInput>): String = TODO()
        fun optionalInputListObject(input: OptionalInput<List<TestInput>>): String = TODO()
    }

    class TestInput(
        val foo: String,
        val bar: String? = null,
        val baz: List<String>? = null
    )

    enum class Foo {
        BAR,
        BAZ
    }
}
