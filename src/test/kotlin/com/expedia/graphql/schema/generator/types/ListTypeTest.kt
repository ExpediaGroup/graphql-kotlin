package com.expedia.graphql.schema.generator.types

import com.expedia.graphql.schema.extensions.getValidProperties
import graphql.schema.GraphQLNonNull
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

@Suppress("Detekt.UnsafeCast")
internal class ListTypeTest : TypeTestHelper() {

    private class HappyClass {
        val testList = listOf<String>()
        val testArray = arrayOf<String>()
    }

    private lateinit var builder: ListTypeBuilder

    override fun beforeTest() {
        builder = ListTypeBuilder(generator)
    }

    @Test
    fun `Test list`() {
        val listProp = HappyClass::class.getValidProperties(hooks)[0]

        val result = builder.listType(listProp.returnType, false)
        assertEquals(String::class.simpleName, (result.wrappedType as GraphQLNonNull).wrappedType.name)
    }

    @Test
    fun `Test array`() {
        val arrayProp = HappyClass::class.getValidProperties(hooks)[0]

        val result = builder.listType(arrayProp.returnType, false)
        assertEquals(String::class.simpleName, (result.wrappedType as GraphQLNonNull).wrappedType.name)
    }
}
