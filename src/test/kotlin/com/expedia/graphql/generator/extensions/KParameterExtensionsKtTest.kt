package com.expedia.graphql.generator.extensions

import com.expedia.graphql.annotations.GraphQLDescription
import com.expedia.graphql.exceptions.CouldNotGetNameOfKParameterException
import graphql.schema.DataFetchingEnvironment
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import kotlin.reflect.KParameter
import kotlin.reflect.full.findParameterByName
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

internal class KParameterExtensionsKtTest {

    @GraphQLDescription("class description")
    internal data class MyClass(val foo: String)

    internal interface MyInterface {
        val value: String
    }

    internal abstract class MyAbstractClass {
        val value: String = "test"
    }

    internal class Container {

        internal fun interfaceInput(myInterface: MyInterface) = myInterface

        internal fun absctractInput(myAbstractClass: MyAbstractClass) = myAbstractClass

        internal fun noDescription(myClass: MyClass) = myClass

        internal fun paramDescription(@GraphQLDescription("param description") myClass: MyClass) = myClass

        internal fun dataFetchingEnvironment(environment: DataFetchingEnvironment) = environment.field.name
    }

    internal class MyKotlinClass {
        fun stringFun(string: String) = "hello $string"
    }

    @Test
    fun getName() {
        val param = Container::noDescription.findParameterByName("myClass")
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
    fun `parameter description`() {
        val param = Container::paramDescription.findParameterByName("myClass")
        assertEquals(expected = "param description", actual = param?.getGraphQLDescription())
    }

    @Test
    fun `no description`() {
        val param = Container::noDescription.findParameterByName("myClass")
        assertNull(param?.getGraphQLDescription())
    }

    @Test
    fun `class input is invalid`() {
        val param = Container::noDescription.findParameterByName("myClass")
        assertFalse(param?.isInterface().isTrue())
    }

    @Test
    fun `interface input is invalid`() {
        val param = Container::interfaceInput.findParameterByName("myInterface")
        assertTrue(param?.isInterface().isTrue())
    }

    @Test
    fun `abstract class input is invalid`() {
        val param = Container::absctractInput.findParameterByName("myAbstractClass")
        assertTrue(param?.isInterface().isTrue())
    }

    @Test
    fun `valid DataFetchingEnvironment passes`() {
        val param = Container::dataFetchingEnvironment.findParameterByName("environment")
        assertTrue(param?.isDataFetchingEnvironment().isTrue())
    }

    @Test
    fun `invalid DataFetchingEnvironment fails`() {
        val param = Container::interfaceInput.findParameterByName("myInterface")
        assertFalse(param?.isDataFetchingEnvironment().isTrue())
    }

    @Test
    fun javaTypeClass() {
        assertEquals(expected = String::class.java, actual = MyKotlinClass::stringFun.findParameterByName("string")?.javaTypeClass())
    }
}
