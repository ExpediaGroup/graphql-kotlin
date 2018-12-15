package com.expedia.graphql

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class TopLevelObjectDefTest {

    internal class TestClass

    internal object TestObject

    @Test
    fun `basic container`() {
        val obj = TestClass()
        val top = TopLevelObjectDef(obj)
        assertEquals(expected = obj, actual = top.obj)
        assertEquals(expected = TestClass::class, actual = top.klazz)
    }

    @Test
    fun `custom class`() {
        val top = TopLevelObjectDef(TestObject, TestClass::class)
        assertEquals(expected = TestObject, actual = top.obj)
        assertEquals(expected = TestClass::class, actual = top.klazz)
    }
}
