package com.expedia.graphql.generator.types

import graphql.schema.GraphQLNonNull
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

@Suppress("Detekt.UnusedPrivateClass")
internal class ListTypeBuilderTest : TypeTestHelper() {

    private data class MyDataClass(val id: String)

    private class ClassWithListAndArray {
        val testList = listOf<Int>()
        val testListOfClass = listOf<MyDataClass>()
        val testArray = arrayOf<String>()
        val primitiveArray = booleanArrayOf(true)
    }

    private lateinit var builder: ListTypeBuilder

    override fun beforeTest() {
        builder = ListTypeBuilder(generator)
    }

    @Test
    fun `test list of primitive`() {
        val listProp = ClassWithListAndArray::testList

        val result = builder.listType(listProp.returnType, false)
        assertEquals(Int::class.simpleName, (result.wrappedType as? GraphQLNonNull)?.wrappedType?.name)
    }

    @Test
    fun `test list of class`() {
        val listProp = ClassWithListAndArray::testListOfClass

        val result = builder.listType(listProp.returnType, false)
        assertEquals(MyDataClass::class.simpleName, (result.wrappedType as? GraphQLNonNull)?.wrappedType?.name)
    }

    @Test
    fun `test array`() {
        val arrayProp = ClassWithListAndArray::testArray

        val result = builder.listType(arrayProp.returnType, false)
        assertEquals(String::class.simpleName, (result.wrappedType as? GraphQLNonNull)?.wrappedType?.name)
    }

    @Test
    fun `test array of primitives`() {
        val primitiveArray = ClassWithListAndArray::primitiveArray

        val result = builder.listType(primitiveArray.returnType, false)
        assertEquals(Boolean::class.simpleName, (result.wrappedType as? GraphQLNonNull)?.wrappedType?.name)
    }
}
