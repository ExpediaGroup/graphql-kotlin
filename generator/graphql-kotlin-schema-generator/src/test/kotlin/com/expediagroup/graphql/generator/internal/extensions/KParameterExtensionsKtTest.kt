/*
 * Copyright 2019 Expedia, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.expediagroup.graphql.generator.internal.extensions

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.generator.exceptions.CouldNotGetNameOfKParameterException
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

class KParameterExtensionsKtTest {

    @GraphQLDescription("class description")
    data class MyClass(val foo: String)

    interface MyInterface {
        val value: String
    }

    abstract class MyAbstractClass {

        abstract val implementMe: String

        val value: String = "test"
    }

    class Container {

        fun interfaceInput(myInterface: MyInterface) = myInterface

        fun absctractInput(myAbstractClass: MyAbstractClass) = myAbstractClass

        fun listInput(myList: List<Int>) = myList

        fun arrayListInput(myList: ArrayList<Int>) = myList

        fun arrayInput(myArray: IntArray) = myArray

        fun noDescription(myClass: MyClass) = myClass

        fun paramDescription(@GraphQLDescription("param description") myClass: MyClass) = myClass

        fun dataFetchingEnvironment(environment: DataFetchingEnvironment) = environment.field.name
    }

    class MyKotlinClass {
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
        every { mockParam.annotations } returns emptyList()
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
}
