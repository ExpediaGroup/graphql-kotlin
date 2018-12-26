package com.expedia.graphql.generator.extensions

import com.expedia.graphql.exceptions.CouldNotGetNameOfKParameterException
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import kotlin.reflect.KParameter
import kotlin.reflect.full.primaryConstructor
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

internal class KParameterExtensionsKtTest {

    internal data class MyClass(val foo: String)

    @Test
    fun getName() {
        val param = MyClass::class.primaryConstructor?.parameters?.first()
        assertEquals(expected = "foo", actual = param?.getName())
    }

    @Test
    fun getNameException() {
        val mockParam: KParameter = mockk()
        every { mockParam.name } returns null
        assertFailsWith(CouldNotGetNameOfKParameterException::class) {
            mockParam.getName()
        }
    }
}
