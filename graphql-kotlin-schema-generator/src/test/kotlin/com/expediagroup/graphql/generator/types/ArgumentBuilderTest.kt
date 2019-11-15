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

package com.expediagroup.graphql.generator.types

import com.expediagroup.graphql.annotations.GraphQLDescription
import com.expediagroup.graphql.annotations.GraphQLID
import com.expediagroup.graphql.annotations.GraphQLInputNullable
import com.expediagroup.graphql.annotations.GraphQLName
import com.expediagroup.graphql.exceptions.InvalidInputFieldTypeException
import com.expediagroup.graphql.test.utils.SimpleDirective
import graphql.Scalars
import graphql.schema.GraphQLNonNull
import org.junit.jupiter.api.Test
import kotlin.reflect.full.findParameterByName
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

internal class ArgumentBuilderTest : TypeTestHelper() {

    private lateinit var builder: ArgumentBuilder

    override fun beforeTest() {
        builder = ArgumentBuilder(generator)
    }

    internal interface MyInterface {
        val id: String
    }

    internal class ArgumentTestClass {
        fun description(@GraphQLDescription("Argument description") input: String) = input

        fun directive(@SimpleDirective input: String) = input

        fun changeName(@GraphQLName("newName") input: String) = input

        fun id(@GraphQLID idArg: String) = "Your id is $idArg"

        fun interfaceArg(input: MyInterface) = input.id

        fun changeNullability(@GraphQLInputNullable(false) input: String? = null) = "Your input is $input"
    }

    @Test
    fun `Description is set on arguments`() {
        val kParameter = ArgumentTestClass::description.findParameterByName("input")
        assertNotNull(kParameter)
        val result = builder.argument(kParameter)

        assertEquals("String", (result.type as? GraphQLNonNull)?.wrappedType?.name)
        assertEquals("Argument description", result.description)
    }

    @Test
    fun `Directives are included on arguments`() {
        val kParameter = ArgumentTestClass::directive.findParameterByName("input")
        assertNotNull(kParameter)
        val result = builder.argument(kParameter)

        assertEquals("String", (result.type as? GraphQLNonNull)?.wrappedType?.name)
        assertEquals(1, result.directives.size)
        assertEquals("simpleDirective", result.directives.firstOrNull()?.name)
    }

    @Test
    fun `Argument names can be changed with @GraphQLName`() {
        val kParameter = ArgumentTestClass::changeName.findParameterByName("input")
        assertNotNull(kParameter)
        val result = builder.argument(kParameter)

        assertEquals("String", (result.type as? GraphQLNonNull)?.wrappedType?.name)
        assertEquals("newName", result.name)
    }

    @Test
    fun `ID argument type is valid`() {
        val kParameter = ArgumentTestClass::id.findParameterByName("idArg")
        assertNotNull(kParameter)
        val result = builder.argument(kParameter)

        assertEquals(expected = "idArg", actual = result.name)
        assertEquals(Scalars.GraphQLID, (result.type as? GraphQLNonNull)?.wrappedType)
    }

    @Test
    fun `Interface argument type throws exception`() {
        val kParameter = ArgumentTestClass::interfaceArg.findParameterByName("input")
        assertNotNull(kParameter)

        assertFailsWith(InvalidInputFieldTypeException::class) {
            builder.argument(kParameter)
        }
    }

    @Test
    fun `Argument Nullability can be changed with @GraphQLInputNullable`() {
        val kParameter = ArgumentTestClass::changeNullability.findParameterByName("input")
        assertNotNull(kParameter)
        val result = builder.argument(kParameter)
        assertTrue(result.type is GraphQLNonNull)
    }
}
