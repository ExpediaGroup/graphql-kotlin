package com.expedia.graphql.schema.extensions

import com.expedia.graphql.schema.exceptions.InvalidListTypeException
import org.junit.jupiter.api.Test
import kotlin.reflect.full.findParameterByName
import kotlin.reflect.full.starProjectedType
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

internal class KTypeExtensionsKtTest {

    internal class MyClass {
        fun listFun(list: List<String>) = list.joinToString(separator = ",") { it }

        fun stringFun(string: String) = "hello $string"
    }

    @Test
    fun getTypeOfFirstArgument() {
        assertEquals(String::class.starProjectedType, MyClass::listFun.findParameterByName("list")?.type?.getTypeOfFirstArgument())

        assertFailsWith(InvalidListTypeException::class) {
            MyClass::stringFun.findParameterByName("string")?.type?.getTypeOfFirstArgument()
        }
    }

    @Test
    fun getKClass() {
        assertEquals(MyClass::class, MyClass::class.starProjectedType.getKClass())
    }

    @Test
    fun getArrayType() {
        assertEquals(Int::class.starProjectedType, IntArray::class.starProjectedType.getArrayType())
        assertEquals(Long::class.starProjectedType, LongArray::class.starProjectedType.getArrayType())
        assertEquals(Short::class.starProjectedType, ShortArray::class.starProjectedType.getArrayType())
        assertEquals(Float::class.starProjectedType, FloatArray::class.starProjectedType.getArrayType())
        assertEquals(Double::class.starProjectedType, DoubleArray::class.starProjectedType.getArrayType())
        assertEquals(Char::class.starProjectedType, CharArray::class.starProjectedType.getArrayType())
        assertEquals(Boolean::class.starProjectedType, BooleanArray::class.starProjectedType.getArrayType())
        assertEquals(String::class.starProjectedType, MyClass::listFun.findParameterByName("list")?.type?.getArrayType())

        assertFailsWith(InvalidListTypeException::class) {
            MyClass::stringFun.findParameterByName("string")?.type?.getArrayType()
        }
    }
}
