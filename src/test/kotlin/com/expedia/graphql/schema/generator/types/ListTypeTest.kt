package com.expedia.graphql.schema.generator.types

import com.expedia.graphql.schema.extensions.getValidProperties
import graphql.schema.GraphQLNonNull
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class ListTypeTest : TypeTestHelper() {

    @Suppress("Detekt.UnusedPrivateClass")
    private class ClassWithListAndArray {
        val testList = listOf<Int>()
        val testArray = arrayOf<String>()
        val primitiveArray = booleanArrayOf(true)
    }

    private lateinit var builder: ListTypeBuilder

    override fun beforeTest() {
        builder = ListTypeBuilder(generator)
    }

    @Test
    fun `test list`() {
        val listProp = ClassWithListAndArray::class.getValidProperties(hooks).first { it.name == "testList" }

        val result = builder.listType(listProp.returnType, false)
        assertEquals(Int::class.simpleName, (result.wrappedType as? GraphQLNonNull)?.wrappedType?.name)
    }

    @Test
    fun `test array`() {
        val arrayProp = ClassWithListAndArray::class.getValidProperties(hooks).first { it.name == "testArray" }

        val result = builder.arrayType(arrayProp.returnType, false)
        assertEquals(String::class.simpleName, (result.wrappedType as? GraphQLNonNull)?.wrappedType?.name)
    }

    @Test
    fun `test array of primitives`() {
        val primitiveArray = ClassWithListAndArray::class.getValidProperties(hooks).first { it.name == "primitiveArray" }

        val result = builder.arrayType(primitiveArray.returnType, false)
        assertEquals(Boolean::class.simpleName, (result.wrappedType as? GraphQLNonNull)?.wrappedType?.name)
    }
}
