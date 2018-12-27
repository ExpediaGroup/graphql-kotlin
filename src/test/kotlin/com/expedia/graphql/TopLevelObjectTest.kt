package com.expedia.graphql

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class TopLevelObjectTest {

    internal class TestClass

    internal object TestObject

    @Test
    fun `basic container`() {
        val obj = TestClass()
        val top = TopLevelObject(obj)
        assertEquals(expected = obj, actual = top.obj)
        assertEquals(expected = TestClass::class, actual = top.kClass)
    }

    @Test
    fun `custom class`() {
        val top = TopLevelObject(TestObject, TestClass::class)
        assertEquals(expected = TestObject, actual = top.obj)
        assertEquals(expected = TestClass::class, actual = top.kClass)
    }
}
