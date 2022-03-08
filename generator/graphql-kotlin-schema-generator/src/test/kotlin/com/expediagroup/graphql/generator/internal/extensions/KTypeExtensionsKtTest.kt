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

package com.expediagroup.graphql.generator.internal.extensions

import com.expediagroup.graphql.generator.exceptions.CouldNotGetNameOfKClassException
import com.expediagroup.graphql.generator.exceptions.InvalidWrappedTypeException
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import kotlin.reflect.KType
import kotlin.reflect.KTypeProjection
import kotlin.reflect.full.createType
import kotlin.reflect.full.findParameterByName
import kotlin.reflect.full.starProjectedType
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class KTypeExtensionsKtTest {

    class MyClass {
        fun listFun(list: List<String>) = list.joinToString(separator = ",") { it }

        fun arrayFun(array: Array<String>) = array.joinToString(separator = ",") { it }

        fun stringFun(string: String) = "hello $string"
    }

    interface SimpleInterface

    class SimpleClass(val id: String) : SimpleInterface

    @Test
    fun getTypeOfFirstArgument() {
        assertEquals(String::class.starProjectedType, MyClass::listFun.findParameterByName("list")?.type?.getTypeOfFirstArgument())

        assertEquals(String::class.starProjectedType, MyClass::arrayFun.findParameterByName("array")?.type?.getTypeOfFirstArgument())

        assertFailsWith(InvalidWrappedTypeException::class) {
            MyClass::stringFun.findParameterByName("string")?.type?.getTypeOfFirstArgument()
        }

        assertFailsWith(InvalidWrappedTypeException::class) {
            val mockType: KType = mockk()
            every { mockType.arguments } returns emptyList()
            mockType.getTypeOfFirstArgument()
        }

        assertFailsWith(InvalidWrappedTypeException::class) {
            val mockArgument: KTypeProjection = mockk()
            every { mockArgument.type } returns null
            val mockType: KType = mockk()
            every { mockType.arguments } returns listOf(mockArgument)
            mockType.getTypeOfFirstArgument()
        }
    }

    @Test
    fun getKClass() {
        assertEquals(MyClass::class, MyClass::class.createType().getKClass())
    }

    @Test
    fun getJavaClass() {
        val listType = assertNotNull(MyClass::listFun.findParameterByName("list")?.type)
        assertEquals(List::class.java, listType.getJavaClass())

        val arrayType = assertNotNull(MyClass::arrayFun.findParameterByName("array")?.type)
        assertEquals(Array<String>::class.java, arrayType.getJavaClass())

        val stringType = assertNotNull(MyClass::stringFun.findParameterByName("string")?.type)
        assertEquals(String::class.java, stringType.getJavaClass())
    }

    @Test
    fun isSubclassOf() {
        assertTrue(MyClass::class.starProjectedType.isSubclassOf(MyClass::class))
        assertTrue(SimpleClass::class.starProjectedType.isSubclassOf(SimpleInterface::class))
        assertFalse(SimpleInterface::class.starProjectedType.isSubclassOf(SimpleClass::class))
        assertFalse(MyClass::class.starProjectedType.isSubclassOf(SimpleInterface::class))
    }

    @Test
    fun isList() {
        assertTrue(List::class.starProjectedType.isList())
        assertFalse(Array::class.starProjectedType.isList())
        assertFalse(IntArray::class.starProjectedType.isList())
        assertFalse(MyClass::class.starProjectedType.isList())
    }

    @Test
    fun isArray() {
        assertTrue(Array::class.starProjectedType.isArray())
        assertTrue(IntArray::class.starProjectedType.isArray())
        assertFalse(List::class.starProjectedType.isArray())
        assertFalse(MyClass::class.starProjectedType.isArray())
    }

    @Test
    fun isListType() {
        assertTrue(List::class.starProjectedType.isListType())
        assertTrue(Array::class.starProjectedType.isListType())
        assertTrue(IntArray::class.starProjectedType.isListType())
        assertFalse(MyClass::class.starProjectedType.isListType())
    }

    @Test
    fun getSimpleName() {
        assertEquals("MyClass", MyClass::class.starProjectedType.getSimpleName())
        assertEquals("MyClassInput", MyClass::class.starProjectedType.getSimpleName(isInputType = true))
        assertFailsWith(CouldNotGetNameOfKClassException::class) {
            object {}::class.starProjectedType.getSimpleName()
        }
    }

    @Test
    fun qualifiedName() {
        assertEquals("com.expediagroup.graphql.generator.internal.extensions.KTypeExtensionsKtTest.MyClass", MyClass::class.starProjectedType.qualifiedName)
        assertEquals("", object { }::class.starProjectedType.qualifiedName)
    }
}
