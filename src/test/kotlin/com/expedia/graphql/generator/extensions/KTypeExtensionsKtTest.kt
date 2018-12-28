package com.expedia.graphql.generator.extensions

import com.expedia.graphql.exceptions.CouldNotCastToKClassException
import com.expedia.graphql.exceptions.CouldNotGetNameOfKTypeException
import com.expedia.graphql.exceptions.InvalidListTypeException
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import kotlin.reflect.KType
import kotlin.reflect.KTypeProjection
import kotlin.reflect.full.findParameterByName
import kotlin.reflect.full.starProjectedType
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

internal class KTypeExtensionsKtTest {

    internal class MyClass {
        fun listFun(list: List<String>) = list.joinToString(separator = ",") { it }

        fun arrayFun(array: Array<String>) = array.joinToString(separator = ",") { it }

        fun primitiveArrayFun(intArray: IntArray) = intArray.joinToString(separator = ",") { it.toString() }

        fun stringFun(string: String) = "hello $string"
    }

    @Test
    fun getTypeOfFirstArgument() {
        assertEquals(String::class.starProjectedType, MyClass::listFun.findParameterByName("list")?.type?.getTypeOfFirstArgument())

        assertEquals(String::class.starProjectedType, MyClass::arrayFun.findParameterByName("array")?.type?.getTypeOfFirstArgument())

        assertEquals(Int::class.starProjectedType, MyClass::primitiveArrayFun.findParameterByName("intArray")?.type?.getArrayType())

        assertFailsWith(InvalidListTypeException::class) {
            MyClass::stringFun.findParameterByName("string")?.type?.getTypeOfFirstArgument()
        }

        assertFailsWith(InvalidListTypeException::class) {
            val mockType: KType = mockk()
            every { mockType.arguments } returns emptyList()
            mockType.getTypeOfFirstArgument()
        }

        assertFailsWith(InvalidListTypeException::class) {
            val mockArgument: KTypeProjection = mockk()
            every { mockArgument.type } returns null
            val mockType: KType = mockk()
            every { mockType.arguments } returns listOf(mockArgument)
            mockType.getTypeOfFirstArgument()
        }
    }

    @Test
    fun getKClass() {
        assertEquals(MyClass::class, MyClass::class.starProjectedType.getKClass())
    }

    @Test
    fun `getKClass exception`() {
        assertFailsWith(CouldNotCastToKClassException::class) {
            val mockType: KType = mockk()
            every { mockType.classifier } returns null
            mockType.getKClass()
        }
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

    @Test
    fun getSimpleName() {
        assertEquals("MyClass", MyClass::class.starProjectedType.getSimpleName())
    }

    @Test
    fun getSimpleNameException() {
        assertFailsWith(CouldNotGetNameOfKTypeException::class) {
            object { }::class.starProjectedType.getSimpleName()
        }
    }

    @Test
    fun qualifiedName() {
        assertEquals("com.expedia.graphql.generator.extensions.KTypeExtensionsKtTest.MyClass", MyClass::class.starProjectedType.qualifiedName)
        assertEquals("", object { }::class.starProjectedType.qualifiedName)
    }

    @Test
    fun javaTypeClass() {
        assertEquals(expected = String::class.java, actual = MyClass::stringFun.returnType.javaTypeClass)
    }
}
