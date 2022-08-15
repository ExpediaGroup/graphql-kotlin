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
import com.expediagroup.graphql.generator.scalars.ID
import graphql.schema.DataFetchingEnvironment
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.reflect.full.findParameterByName
import kotlin.reflect.jvm.internal.KotlinReflectionInternalError
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
    fun `renamed enum object is parsed`() {
        val kParam = assertNotNull(TestFunctions::enumInput.findParameterByName("input"))
        val inputValue = "baz"
        val result = convertArgumentValue("input", kParam, mapOf("input" to inputValue))
        val castResult = assertIs<Foo>(result)
        assertEquals(Foo.BAZ, castResult)
    }

    @Test
    fun `generic map object is parsed`() {
        val kParam = assertNotNull(TestFunctions::inputObject.findParameterByName("input"))
        val inputValue = mapOf(
            "foo" to "hello",
            "bar" to "world",
            "baz" to listOf("!"),
            "qux" to "1234"
        )
        val result = convertArgumentValue("input", kParam, mapOf("input" to inputValue))
        val castResult = assertIs<TestInput>(result)
        assertEquals("hello", castResult.foo)
        assertEquals("world", castResult.bar)
        assertEquals(listOf("!"), castResult.baz)
        assertEquals("1234", castResult.qux)
    }

    @Test
    fun `generic map object is parsed and defaults are used`() {
        val kParam = assertNotNull(TestFunctions::inputObject.findParameterByName("input"))
        val result = convertArgumentValue(
            "input",
            kParam,
            mapOf(
                "input" to mapOf(
                    "foo" to "hello"
                )
            )
        )

        val castResult = assertIs<TestInput>(result)
        assertEquals("hello", castResult.foo)
        assertEquals(null, castResult.bar)
        assertEquals(null, castResult.baz)
        assertEquals(null, castResult.qux)
    }

    @Test
    fun `generic map object is parsed and all defaults are used`() {
        val kParam = assertNotNull(TestFunctions::inputObjectNested.findParameterByName("input"))
        val result = convertArgumentValue(
            "input",
            kParam,
            mapOf(
                "input" to emptyMap<String, Any>()
            )
        )

        val castResult = assertIs<TestInputNested>(result)
        assertEquals("foo", castResult.foo)
        assertEquals("bar", castResult.bar)
        assertEquals("nested default value", castResult.nested?.value)
    }

    @Test
    fun `generic map object is parsed without using primary constructor`() {
        val kParam = assertNotNull(TestFunctions::inputObjectNoPrimaryConstructor.findParameterByName("input"))
        val result = convertArgumentValue(
            "input",
            kParam,
            mapOf(
                "input" to mapOf(
                    "value" to "hello"
                )
            )
        )

        val castResult = assertIs<TestInputNoPrimaryConstructor>(result)
        assertEquals("hello", castResult.value)
    }

    /**
     * this will be solved in Kotlin 1.7
     * "KotlinReflectionInternalError" when using `callBy` on constructor that has inline class parameters
     * https://youtrack.jetbrains.com/issue/KT-27598
     * https://github.com/JetBrains/kotlin/pull/4746
     */
    @Test
    fun `generic map object is parsed and will assign null to nullable custom scalar type`() {
        val kParam = assertNotNull(TestFunctions::inputObjectNullableScalar.findParameterByName("input"))
        assertThrows<KotlinReflectionInternalError> {
            convertArgumentValue(
                "input",
                kParam,
                mapOf(
                    "input" to mapOf(
                        "foo" to "foo"
                    )
                )
            )
        }
    }

    /**
     * this will be solved in Kotlin 1.7
     * "KotlinReflectionInternalError" when using `callBy` on constructor that has inline class parameters
     * https://youtrack.jetbrains.com/issue/KT-27598
     * https://github.com/JetBrains/kotlin/pull/4746
     */
    @Test
    fun `generic map object is parsed and will throw exception for non nullable custom scalar type`() {
        val kParam = assertNotNull(TestFunctions::inputObjectNotNullableScalar.findParameterByName("input"))
        assertThrows<KotlinReflectionInternalError> {
            convertArgumentValue(
                "input",
                kParam,
                mapOf(
                    "input" to mapOf(
                        "foo" to "foo"
                    )
                )
            )
        }
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

    @Test
    fun `id input is parsed`() {
        val kParam = assertNotNull(TestFunctions::idInput.findParameterByName("input"))
        val result = convertArgumentValue("input", kParam, mapOf("input" to "1234"))
        assertIs<ID>(result)
        assertEquals("1234", result.value)
    }

    @Test
    fun `input object with renamed fields is correctly deserialized`() {
        val kParam = assertNotNull(TestFunctions::inputObjectRenamed.findParameterByName("input"))
        val result = convertArgumentValue(
            "input",
            kParam,
            mapOf(
                "input" to mapOf(
                    "bar" to "renamed"
                )
            )
        )

        val castResult = assertIs<TestInputRenamed>(result)
        assertEquals("renamed", castResult.foo)
    }

    class TestFunctions {
        fun enumInput(input: Foo): String = TODO()
        fun idInput(input: ID): String = TODO()
        fun inputObject(input: TestInput): String = TODO()
        fun inputObjectNoPrimaryConstructor(input: TestInputNoPrimaryConstructor): String = TODO()
        fun inputObjectNested(input: TestInputNested): String = TODO()
        fun inputObjectNullableScalar(input: TestInputNullableScalar): String = TODO()
        fun inputObjectNotNullableScalar(input: TestInputNotNullableScalar): String = TODO()
        fun listStringInput(input: List<String>): String = TODO()
        fun optionalInput(input: OptionalInput<String>): String = TODO()
        fun optionalInputObject(input: OptionalInput<TestInput>): String = TODO()
        fun optionalInputListObject(input: OptionalInput<List<TestInput>>): String = TODO()
        fun stringInput(input: String): String = TODO()
        fun inputObjectRenamed(input: TestInputRenamed): String = TODO()
    }

    class TestInput(val foo: String, val bar: String? = null, val baz: List<String>? = null, val qux: String? = null)
    class TestInputNested(val foo: String? = "foo", val bar: String? = "bar", val nested: TestInputNestedType? = TestInputNestedType())
    class TestInputNestedType(val value: String = "nested default value")
    class TestInputNullableScalar(val foo: String? = null, val id: ID? = null)
    class TestInputNotNullableScalar(val foo: String, val id: ID = ID("1234"))
    class TestInputRenamed(@GraphQLName("bar") val foo: String)
    class TestInputNoPrimaryConstructor {
        val value: String
        constructor(value: String) {
            this.value = value
        }
    }

    enum class Foo {
        BAR,
        @GraphQLName("baz")
        BAZ
    }
}
