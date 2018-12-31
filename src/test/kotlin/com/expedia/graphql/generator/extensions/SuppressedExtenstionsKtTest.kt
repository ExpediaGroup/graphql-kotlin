package com.expedia.graphql.generator.extensions

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class SuppressedExtenstionsKtTest {

    internal class MyClass {
        fun stringFun(string: String) = "hello $string"
    }

    @Test
    fun javaTypeClass() {
        assertEquals(expected = String::class.java, actual = MyClass::stringFun.returnType.javaTypeClass)
    }
}
