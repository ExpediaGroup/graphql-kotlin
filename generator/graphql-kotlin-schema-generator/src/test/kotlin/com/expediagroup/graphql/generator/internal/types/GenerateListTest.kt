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

package com.expediagroup.graphql.generator.internal.types

import graphql.schema.GraphQLList
import graphql.schema.GraphQLNamedType
import graphql.schema.GraphQLTypeUtil
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class GenerateListTest : TypeTestHelper() {

    private data class MyDataClass(val id: String)

    private class ClassWithListAndArray {
        val testList: List<Int> = listOf(1)
        val testListOfClass: List<MyDataClass> = listOf(MyDataClass("bar"))
        val testArray: Array<String> = arrayOf("foo")
        val primitiveArray: BooleanArray = booleanArrayOf(true)
    }

    @Test
    fun `verify a list of primitives for output`() {
        val listProp = ClassWithListAndArray::testList

        val result = generateList(generator, listProp.returnType, GraphQLKTypeMetadata())
        assertEquals("Int", getListTypeName(result))
    }

    @Test
    fun `verify a list of objects for ouput`() {
        val listProp = ClassWithListAndArray::testListOfClass

        val result = generateList(generator, listProp.returnType, GraphQLKTypeMetadata())
        assertEquals("MyDataClass", getListTypeName(result))
    }

    @Test
    fun `verify arrays are valid output`() {
        val arrayProp = ClassWithListAndArray::testArray

        val result = generateList(generator, arrayProp.returnType, GraphQLKTypeMetadata())
        assertEquals("String", getListTypeName(result))
    }

    @Test
    fun `verify primitive arrays are valid output`() {
        val primitiveArray = ClassWithListAndArray::primitiveArray

        val result = generateList(generator, primitiveArray.returnType, GraphQLKTypeMetadata())
        assertEquals("Boolean", getListTypeName(result))
    }

    @Test
    fun `verify arrays are valid input`() {
        val arrayProp = ClassWithListAndArray::testArray

        val result = generateList(generator, arrayProp.returnType, GraphQLKTypeMetadata(inputType = true))
        assertEquals("String", getListTypeName(result))
    }

    @Test
    fun `verify primitive arrays are valid input`() {
        val primitiveArray = ClassWithListAndArray::primitiveArray

        val result = generateList(generator, primitiveArray.returnType, GraphQLKTypeMetadata(inputType = true))
        assertEquals("Boolean", getListTypeName(result))
    }

    @Test
    fun `verify a list of primitives for input`() {
        val listProp = ClassWithListAndArray::testList

        val result = generateList(generator, listProp.returnType, GraphQLKTypeMetadata(inputType = true))
        assertEquals("Int", getListTypeName(result))
    }

    @Test
    fun `verify a list of objects for input`() {
        val listProp = ClassWithListAndArray::testListOfClass

        val result = generateList(generator, listProp.returnType, GraphQLKTypeMetadata(inputType = true))
        assertEquals("MyDataClassInput", getListTypeName(result))
    }

    private fun getListTypeName(list: GraphQLList) = (GraphQLTypeUtil.unwrapNonNull(list.wrappedType) as? GraphQLNamedType)?.name
}
