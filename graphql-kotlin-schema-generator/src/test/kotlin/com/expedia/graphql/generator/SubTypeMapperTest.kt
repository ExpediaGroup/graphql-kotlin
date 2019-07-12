package com.expedia.graphql.generator

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

@Suppress("Detekt.UnusedPrivateClass")
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

    @Suppress("Detekt.UnnecessaryAbstractClass")
    private abstract class MyAbstractClass {
        abstract fun someValue(): Int
    }

    private class ThirdClass : MyAbstractClass() {
        override fun someValue() = 3
    }

    private abstract class FourthClass : MyAbstractClass() {
        override fun someValue() = 3

        abstract fun getOtherValue(): Int
    }

    @Test
    fun `valid subtypes`() {

        val mapper = SubTypeMapper(listOf("com.expedia.graphql"))
        val list = mapper.getSubTypesOf(MyInterface::class)

        assertEquals(expected = 2, actual = list.size)
    }

    @Test
    fun `abstract subtypes`() {

        val mapper = SubTypeMapper(listOf("com.expedia.graphql"))
        val list = mapper.getSubTypesOf(MyAbstractClass::class)

        assertEquals(expected = 1, actual = list.size)
    }

    @Test
    fun `subtypes of non-supported packages`() {

        val mapper = SubTypeMapper(listOf("com.example"))
        val list = mapper.getSubTypesOf(MyInterface::class)

        assertEquals(expected = 0, actual = list.size)
    }
}
