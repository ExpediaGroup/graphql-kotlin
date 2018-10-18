package com.expedia.graphql.schema.generator

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class SubTypeMapperTest {

    private interface MyInterface {
        fun getValue(): Int
    }

    private class FirstClass : MyInterface {
        override fun getValue() = 1
    }

    private class SecondClass : MyInterface {
        override fun getValue() = 2
    }

    @Test
    fun `valid subtypes`() {

        val mapper = SubTypeMapper(listOf("com.expedia.graphql"))
        val set = mapper.getSubTypesOf(MyInterface::class)

        assertEquals(expected = 2, actual = set.size)
    }

    @Test
    fun `subtypes of non-supported packages`() {

        val mapper = SubTypeMapper(listOf("com.example"))
        val set = mapper.getSubTypesOf(MyInterface::class)

        assertEquals(expected = 0, actual = set.size)
    }
}
