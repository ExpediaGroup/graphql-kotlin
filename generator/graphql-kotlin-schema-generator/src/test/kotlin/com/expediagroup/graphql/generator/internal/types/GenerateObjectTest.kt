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
import com.expediagroup.graphql.generator.annotations.GraphQLDirective
import com.expediagroup.graphql.generator.annotations.GraphQLIgnore
import com.expediagroup.graphql.generator.annotations.GraphQLName
import com.expediagroup.graphql.generator.annotations.GraphQLValidObjectLocations
import com.expediagroup.graphql.generator.exceptions.InvalidGraphQLNameException
import com.expediagroup.graphql.generator.exceptions.InvalidObjectLocationException
import com.expediagroup.graphql.generator.exceptions.InvalidPropertyReturnTypeException
import graphql.Scalars
import graphql.introspection.Introspection
import graphql.schema.GraphQLNonNull
import graphql.schema.GraphQLObjectType
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class GenerateObjectTest : TypeTestHelper() {

    @GraphQLDirective(locations = [Introspection.DirectiveLocation.OBJECT])
    annotation class ObjectDirective(val arg: String)

    @GraphQLDescription("The truth")
    @ObjectDirective("Don't worry")
    class BeHappy

    @GraphQLName("BeHappyRenamed")
    class BeHappyCustomName

    interface MyInterface {
        val foo: String
    }

    class ClassWithInterface(override val foo: String) : MyInterface

    @GraphQLValidObjectLocations(locations = [GraphQLValidObjectLocations.Locations.INPUT_OBJECT])
    class InputOnly {
        val myField: String = "car"
    }

    @GraphQLValidObjectLocations(locations = [GraphQLValidObjectLocations.Locations.OBJECT])
    class OutputOnly {
        val myField: String = "car"
    }

    class `Invalid$OutputTypeName`

    @GraphQLName("Invalid\$Name")
    class InvalidOutputTypeNameOverride

    data class FooLambda(
        val foo: () -> String
    )

    data class SuspendableFooLambda(
        val foo: suspend () -> String?
    )

    data class Foo(
        val foo: String,
        @GraphQLIgnore val fooLambda: () -> String?,
        private val fooSuspendable: suspend () -> String?
    )

    @Test
    fun `Test naming`() {
        val result = generateObject(generator, BeHappy::class) as? GraphQLObjectType
        assertNotNull(result)
        assertEquals("BeHappy", result.name)
    }

    @Test
    fun `Test custom naming`() {
        val result = generateObject(generator, BeHappyCustomName::class) as? GraphQLObjectType
        assertNotNull(result)
        assertEquals("BeHappyRenamed", result.name)
    }

    @Test
    fun `Test description`() {
        val result = generateObject(generator, BeHappy::class) as? GraphQLObjectType
        assertNotNull(result)
        assertEquals("The truth", result.description)
    }

    @Test
    fun `Test custom directive`() {
        val result = generateObject(generator, BeHappy::class) as? GraphQLObjectType
        assertNotNull(result)
        assertEquals(1, result.appliedDirectives.size)

        val appliedDirective = result.appliedDirectives[0]
        assertEquals("objectDirective", appliedDirective.name)
        assertEquals("Don't worry", appliedDirective.arguments[0].argumentValue.value)
        assertEquals("arg", appliedDirective.arguments[0].name)
        assertTrue(GraphQLNonNull(Scalars.GraphQLString).isEqualTo(appliedDirective.arguments[0].type))

        val schemaDirective = generator.directives[appliedDirective.name]
        assertEquals(
            schemaDirective?.validLocations()?.toSet(),
            setOf(Introspection.DirectiveLocation.OBJECT)
        )
    }

    @Test
    fun `Test object with interface`() {
        val result = generateObject(generator, ClassWithInterface::class) as? GraphQLObjectType
        assertNotNull(result)
        assertEquals(1, result.interfaces.size)
        assertEquals(1, result.fieldDefinitions.size)
    }

    @Test
    fun `input only objects are generated`() {
        assertDoesNotThrow {
            generateInputObject(generator, InputOnly::class)
        }
    }

    @Test
    fun `output only objects throw an exception`() {
        assertFailsWith(InvalidObjectLocationException::class) {
            generateInputObject(generator, OutputOnly::class)
        }
    }

    @Test
    fun `Generation of output object will fail if it specifies invalid name`() {
        assertFailsWith(InvalidGraphQLNameException::class) {
            generateInputObject(generator, `Invalid$OutputTypeName`::class)
        }
    }

    @Test
    fun `Generation of output object will fail if it specifies invalid name override`() {
        assertFailsWith(InvalidGraphQLNameException::class) {
            generateInputObject(generator, InvalidOutputTypeNameOverride::class)
        }
    }

    @Test
    fun `Generation of output object will fail if one of the properties is lambda`() {
        assertThrows<InvalidPropertyReturnTypeException> {
            generateObject(generator, FooLambda::class)
        }
    }

    @Test
    fun `Generation of output object will fail if one of the properties is suspendable lambda`() {
        assertThrows<InvalidPropertyReturnTypeException> {
            generateObject(generator, SuspendableFooLambda::class)
        }
    }

    @Test
    fun `Generation of output objects ignores private or @GraphQLIgnored lambdas `() {
        val result = generateObject(generator, Foo::class)
        assertNotNull(result)
        assertEquals("Foo", result.name)
        assertEquals(1, result.fieldDefinitions.size)
        assertEquals("foo", result.fieldDefinitions[0].name)
    }
}
