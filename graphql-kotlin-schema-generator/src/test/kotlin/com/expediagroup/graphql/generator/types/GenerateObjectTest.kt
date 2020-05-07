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
import com.expediagroup.graphql.annotations.GraphQLDirective
import com.expediagroup.graphql.annotations.GraphQLName
import com.expediagroup.graphql.annotations.GraphQLTypeRestriction
import com.expediagroup.graphql.annotations.GraphQLTypeRestriction.GraphQLType
import com.expediagroup.graphql.exceptions.TypeRestrictionException
import graphql.Scalars
import graphql.introspection.Introspection
import graphql.schema.GraphQLNonNull
import graphql.schema.GraphQLObjectType
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull

class GenerateObjectTest : TypeTestHelper() {

    @GraphQLDirective(locations = [Introspection.DirectiveLocation.OBJECT])
    internal annotation class ObjectDirective(val arg: String)

    @GraphQLDescription("The truth")
    @ObjectDirective("Don't worry")
    private class BeHappy

    @GraphQLName("BeHappyRenamed")
    private class BeHappyCustomName

    interface MyInterface {
        val foo: String
    }

    class ClassWithInterface(override val foo: String) : MyInterface

    @GraphQLTypeRestriction(GraphQLType.INPUT)
    private class InputOnly {
        val myField: String = "car"
    }

    @GraphQLTypeRestriction(GraphQLType.OUTPUT)
    private class OutputOnly {
        val myField: String = "car"
    }

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
        assertEquals(1, result.directives.size)

        val directive = result.directives[0]
        assertEquals("objectDirective", directive.name)
        assertEquals("Don't worry", directive.arguments[0].value)
        assertEquals("arg", directive.arguments[0].name)
        assertEquals(GraphQLNonNull(Scalars.GraphQLString), directive.arguments[0].type)
        assertEquals(
            directive.validLocations()?.toSet(),
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
    fun `Test type restrictions for output classes`() {
        val result = generateObject(generator, OutputOnly::class)
        assertNotNull(result)

        assertFailsWith(TypeRestrictionException::class) {
            generateObject(generator, InputOnly::class)
        }
    }
}
