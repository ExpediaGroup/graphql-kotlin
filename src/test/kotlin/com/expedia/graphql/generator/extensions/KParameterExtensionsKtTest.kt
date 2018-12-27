package com.expedia.graphql.generator.extensions

import com.expedia.graphql.annotations.GraphQLDescription
import com.expedia.graphql.exceptions.CouldNotGetNameOfKParameterException
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import kotlin.reflect.KParameter
import kotlin.reflect.full.findParameterByName
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

internal class KParameterExtensionsKtTest {

    @GraphQLDescription("class description")
    internal data class MyClass(val foo: String)

    internal class Container {
        internal fun classDescription(myClass: MyClass) = myClass

        internal fun paramDescription(@GraphQLDescription("param description") myClass: MyClass) = myClass
    }

    @Test
    fun getName() {
        val param = Container::classDescription.findParameterByName("myClass")
        assertEquals(expected = "myClass", actual = param?.getName())
    }

    @Test
    fun getNameException() {
        val mockParam: KParameter = mockk()
        every { mockParam.name } returns null
        assertFailsWith(CouldNotGetNameOfKParameterException::class) {
            mockParam.getName()
        }
    }

    @Test
    fun `class description`() {
        val param = Container::classDescription.findParameterByName("myClass")
        assertEquals(expected = "class description", actual = param?.getParamterGraphQLDescription())
    }

    @Test
    fun `parameter description`() {
        val param = Container::paramDescription.findParameterByName("myClass")
        assertEquals(expected = "param description", actual = param?.getParamterGraphQLDescription())
    }
}
