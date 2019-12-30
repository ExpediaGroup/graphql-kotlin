/*
 * Copyright 2019 Expedia, Inc
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

package com.expediagroup.graphql.generator.types

import graphql.schema.GraphQLNonNull
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

@Suppress("Detekt.UnusedPrivateClass")
internal class ListBuilderTest : TypeTestHelper() {

    private data class MyDataClass(val id: String)

    private class ClassWithListAndArray {
        val testList = listOf<Int>()
        val testListOfClass = listOf<MyDataClass>()
        val testArray = arrayOf<String>()
        val primitiveArray = booleanArrayOf(true)
    }

    @Test
    fun `test list of primitive`() {
        val listProp = ClassWithListAndArray::testList

        val result = generateList(generator, listProp.returnType, false)
        assertEquals(Int::class.simpleName, (result.wrappedType as? GraphQLNonNull)?.wrappedType?.name)
    }

    @Test
    fun `test list of class`() {
        val listProp = ClassWithListAndArray::testListOfClass

        val result = generateList(generator, listProp.returnType, false)
        assertEquals(MyDataClass::class.simpleName, (result.wrappedType as? GraphQLNonNull)?.wrappedType?.name)
    }

    @Test
    fun `test array`() {
        val arrayProp = ClassWithListAndArray::testArray

        val result = generateList(generator, arrayProp.returnType, false)
        assertEquals(String::class.simpleName, (result.wrappedType as? GraphQLNonNull)?.wrappedType?.name)
    }

    @Test
    fun `test array of primitives`() {
        val primitiveArray = ClassWithListAndArray::primitiveArray

        val result = generateList(generator, primitiveArray.returnType, false)
        assertEquals(Boolean::class.simpleName, (result.wrappedType as? GraphQLNonNull)?.wrappedType?.name)
    }
}
