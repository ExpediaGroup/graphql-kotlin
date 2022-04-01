/*
 * Copyright 2022 Expedia, Inc
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

package com.expediagroup.graphql.generator.internal.types

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.generator.annotations.GraphQLName
import com.expediagroup.graphql.generator.exceptions.InvalidInputFieldTypeException
import com.expediagroup.graphql.generator.execution.OptionalInput
import com.expediagroup.graphql.generator.extensions.unwrapType
import com.expediagroup.graphql.generator.scalars.ID
import com.expediagroup.graphql.generator.test.utils.SimpleDirective
import graphql.Scalars
import graphql.schema.GraphQLList
import graphql.schema.GraphQLNamedType
import graphql.schema.GraphQLNonNull
import graphql.schema.GraphQLTypeUtil
import org.junit.jupiter.api.Test
import kotlin.reflect.full.findParameterByName
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull

class GenerateArgumentTest : TypeTestHelper() {

    interface MyInterface {
        val id: String
    }

    interface MyUnion

    class MyClassInput(val internalValue: String)

    class ArgumentTestClass {
        fun description(@GraphQLDescription("Argument description") input: String) = input

        fun directive(@SimpleDirective input: String) = input

        fun changeName(@GraphQLName("newName") input: String) = input

        fun idClass(idArg: ID) = "Your id is $idArg"

        fun interfaceArg(input: MyInterface) = input.id

        fun listArg(input: List<String>) = input

        fun listInterfaceArg(input: List<MyInterface>) = input

        fun listUnionArg(input: List<MyUnion>) = input

        fun optionalArg(input: OptionalInput<String>): String? = when (input) {
            is OptionalInput.Undefined -> null
            is OptionalInput.Defined -> input.value
        }

        fun optionalListString(input: OptionalInput<List<String>>): List<String>? = when (input) {
            is OptionalInput.Undefined -> null
            is OptionalInput.Defined -> input.value
        }

        fun optionalListClass(input: OptionalInput<List<MyClassInput>>): List<String>? = when (input) {
            is OptionalInput.Undefined -> null
            is OptionalInput.Defined -> input.value?.map { it.internalValue }
        }
    }

    @Test
    fun `Description is set on arguments`() {
        val kParameter = ArgumentTestClass::description.findParameterByName("input")
        assertNotNull(kParameter)
        val result = generateArgument(generator, kParameter)

        assertEquals(Scalars.GraphQLString, (result.type as? GraphQLNonNull)?.wrappedType)
        assertEquals("Argument description", result.description)
    }

    @Test
    fun `Directives are included on arguments`() {
        val kParameter = ArgumentTestClass::directive.findParameterByName("input")
        assertNotNull(kParameter)
        val result = generateArgument(generator, kParameter)

        assertEquals(Scalars.GraphQLString, (result.type as? GraphQLNonNull)?.wrappedType)
        assertEquals(1, result.appliedDirectives.size)
        assertEquals("simpleDirective", result.appliedDirectives.firstOrNull()?.name)
    }

    @Test
    fun `Argument names can be changed with @GraphQLName`() {
        val kParameter = ArgumentTestClass::changeName.findParameterByName("input")
        assertNotNull(kParameter)
        val result = generateArgument(generator, kParameter)

        assertEquals(Scalars.GraphQLString, (result.type as? GraphQLNonNull)?.wrappedType)
        assertEquals("newName", result.name)
    }

    @Test
    fun `Wrapper ID class argument type is valid`() {
        val kParameter = ArgumentTestClass::idClass.findParameterByName("idArg")
        assertNotNull(kParameter)
        val result = generateArgument(generator, kParameter)

        assertEquals(expected = "idArg", actual = result.name)
        assertEquals(Scalars.GraphQLID, (result.type as? GraphQLNonNull)?.wrappedType)
    }

    @Test
    fun `Interface argument type throws exception`() {
        val kParameter = ArgumentTestClass::interfaceArg.findParameterByName("input")
        assertNotNull(kParameter)

        assertFailsWith(InvalidInputFieldTypeException::class) {
            generateArgument(generator, kParameter)
        }
    }

    @Test
    fun `List argument type is valid`() {
        val kParameter = ArgumentTestClass::listArg.findParameterByName("input")
        assertNotNull(kParameter)
        val result = generateArgument(generator, kParameter)

        assertEquals(expected = "input", actual = result.name)
        assertNotNull(GraphQLTypeUtil.unwrapNonNull(result.type) as? GraphQLList)
    }

    @Test
    fun `List of interfaces as input is invalid`() {
        val kParameter = ArgumentTestClass::listInterfaceArg.findParameterByName("input")
        assertNotNull(kParameter)

        assertFailsWith(InvalidInputFieldTypeException::class) {
            generateArgument(generator, kParameter)
        }
    }

    @Test
    fun `List of unions as input is invalid`() {
        val kParameter = ArgumentTestClass::listUnionArg.findParameterByName("input")
        assertNotNull(kParameter)

        assertFailsWith(InvalidInputFieldTypeException::class) {
            generateArgument(generator, kParameter)
        }
    }

    @Test
    fun `Input wrapped in optional is valid`() {
        val kParameter = ArgumentTestClass::optionalArg.findParameterByName("input")
        assertNotNull(kParameter)

        val result = generateArgument(generator, kParameter)

        assertEquals(expected = "input", actual = result.name)
        assertEquals(Scalars.GraphQLString, result.type)
    }

    @Test
    fun `Optional list of strings is valid`() {
        val kParameter = ArgumentTestClass::optionalListString.findParameterByName("input")
        assertNotNull(kParameter)

        val result = generateArgument(generator, kParameter)

        assertEquals(expected = "input", actual = result.name)
        val list = assertNotNull(GraphQLTypeUtil.unwrapNonNull(result.type) as? GraphQLList)
        val wrappedType = assertNotNull(list.wrappedType.unwrapType() as? GraphQLNamedType)
        assertEquals(Scalars.GraphQLString, wrappedType)
    }

    @Test
    fun `Optional list of input objects is valid`() {
        val kParameter = ArgumentTestClass::optionalListClass.findParameterByName("input")
        assertNotNull(kParameter)

        val result = generateArgument(generator, kParameter)

        assertEquals(expected = "input", actual = result.name)
        val list = assertNotNull(GraphQLTypeUtil.unwrapNonNull(result.type) as? GraphQLList)
        val wrappedType = assertNotNull(list.wrappedType.unwrapType() as? GraphQLNamedType)
        assertEquals("MyClassInput", wrappedType.name)
    }
}
