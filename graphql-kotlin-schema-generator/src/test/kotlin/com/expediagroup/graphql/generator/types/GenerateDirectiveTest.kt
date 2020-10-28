/*
 * Copyright 2020 Expedia, Inc
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
import com.expediagroup.graphql.annotations.GraphQLIgnore
import com.expediagroup.graphql.generator.SchemaGenerator
import com.expediagroup.graphql.generator.extensions.isTrue
import com.expediagroup.graphql.getTestSchemaConfigWithMockedDirectives
import com.expediagroup.graphql.test.utils.SimpleDirective
import graphql.introspection.Introspection.DirectiveLocation
import kotlin.reflect.KClass
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Test

class GenerateDirectiveTest {

    @GraphQLDirective
    annotation class DirectiveWithString(val string: String)

    @GraphQLDirective
    annotation class DirectiveWithIgnoredArgs(
        val string: String,
        @get:GraphQLIgnore
        val ignoreMe: String
    )

    enum class Type {
        @DirectiveWithString("my string")
        @DirectiveWithClass(SimpleDirective::class)
        ONE,

        @GraphQLDescription("my description")
        TWO
    }

    @DirectiveOnInputObjectOnly
    @DirectiveOnObjectOnly
    @DirectiveOnFieldDefinitionOnly
    data class MyExampleObject(val value: String)

    @GraphQLDirective
    annotation class DirectiveWithEnum(val type: Type)

    @GraphQLDirective
    annotation class DirectiveWithClass(val kclass: KClass<*>)

    @GraphQLDirective(locations = [DirectiveLocation.INPUT_OBJECT])
    annotation class DirectiveOnInputObjectOnly

    @GraphQLDirective(locations = [DirectiveLocation.OBJECT])
    annotation class DirectiveOnObjectOnly

    @GraphQLDirective(locations = [DirectiveLocation.FIELD_DEFINITION])
    annotation class DirectiveOnFieldDefinitionOnly

    class MyClass {

        fun noAnnotation(string: String) = string

        @GraphQLDescription("test")
        fun noDirective(string: String) = string

        @SimpleDirective
        fun simpleDirective(string: String) = string

        @DirectiveWithString(string = "foo")
        fun directiveWithString(string: String) = string

        @DirectiveWithString(string = "bar")
        fun directiveWithAnotherString(string: String) = string

        @DirectiveWithEnum(type = Type.TWO)
        fun directiveWithEnum(string: String) = string

        @DirectiveWithClass(kclass = Type::class)
        fun directiveWithClass(string: String) = string

        @DirectiveWithIgnoredArgs(string = "foo", ignoreMe = "bar")
        fun directiveWithIgnoredArgs(string: String) = string

        // While all of these annotations are valid kotlin code, only the ones with valid locations should be added
        @DirectiveOnFieldDefinitionOnly
        @DirectiveOnObjectOnly
        @DirectiveOnInputObjectOnly
        fun invalidDirectives(string: String) = string
    }

    data class MyClassWithConstructorArgs(
        @SimpleDirective val noPrefix: String,
        @property:SimpleDirective val propertyPrefix: String,
        val noDirective: String
    )

    private val basicGenerator = SchemaGenerator(getTestSchemaConfigWithMockedDirectives())

    @Test
    fun `no annotation`() {
        assertTrue(generateDirectives(basicGenerator, MyClass::noAnnotation, DirectiveLocation.FIELD_DEFINITION).isEmpty().isTrue())
    }

    @Test
    fun `no directive`() {
        assertTrue(generateDirectives(basicGenerator, MyClass::noDirective, DirectiveLocation.FIELD_DEFINITION).isEmpty().isTrue())
    }

    @Test
    fun `has directive`() {
        assertEquals(expected = 1, actual = generateDirectives(basicGenerator, MyClass::simpleDirective, DirectiveLocation.FIELD_DEFINITION).size)
    }

    @Test
    fun `has directive with string`() {
        assertEquals(expected = 1, actual = generateDirectives(basicGenerator, MyClass::directiveWithString, DirectiveLocation.FIELD_DEFINITION).size)
    }

    @Test
    fun `has directive with enum`() {
        assertEquals(expected = 1, actual = generateDirectives(basicGenerator, MyClass::directiveWithEnum, DirectiveLocation.FIELD_DEFINITION).size)
    }

    @Test
    fun `has directive with class`() {
        assertEquals(expected = 1, actual = generateDirectives(basicGenerator, MyClass::directiveWithClass, DirectiveLocation.FIELD_DEFINITION).size)
    }

    @Test
    fun `directives are only added to the schema once`() {
        val initialCount = basicGenerator.directives.size
        val firstInvocation = generateDirectives(basicGenerator, MyClass::simpleDirective, DirectiveLocation.FIELD_DEFINITION)
        assertEquals(1, firstInvocation.size)
        val secondInvocation = generateDirectives(basicGenerator, MyClass::simpleDirective, DirectiveLocation.FIELD_DEFINITION)
        assertEquals(1, secondInvocation.size)
        assertEquals(firstInvocation.first(), secondInvocation.first())
        assertEquals(initialCount + 1, basicGenerator.directives.size)
    }

    @Test
    fun `directives are valid on fields (enum values)`() {
        val field = Type::class.java.getField("ONE")

        val directives = generateFieldDirectives(basicGenerator, field, DirectiveLocation.ENUM_VALUE)

        assertEquals(2, directives.size)
        assertEquals("directiveWithString", directives.first().name)
        assertEquals("directiveWithClass", directives.last().name)
    }

    @Test
    fun `directives are empty on an enum with no valid annotations`() {
        val field = Type::class.java.getField("TWO")

        val directives = generateFieldDirectives(basicGenerator, field, DirectiveLocation.ENUM_VALUE)

        assertEquals(0, directives.size)
    }

    @Test
    fun `directives are created per each declaration`() {
        val initialCount = basicGenerator.directives.size
        val directivesOnFirstField = generateDirectives(basicGenerator, MyClass::directiveWithString, DirectiveLocation.FIELD_DEFINITION)
        val directivesOnSecondField = generateDirectives(basicGenerator, MyClass::directiveWithAnotherString, DirectiveLocation.FIELD_DEFINITION)
        assertEquals(expected = 1, actual = directivesOnFirstField.size)
        assertEquals(expected = 1, actual = directivesOnSecondField.size)

        val firstDirective = directivesOnFirstField.first()
        val seconDirective = directivesOnSecondField.first()
        assertEquals("directiveWithString", firstDirective.name)
        assertEquals("directiveWithString", seconDirective.name)
        assertEquals("foo", firstDirective.getArgument("string")?.value)
        assertEquals("bar", seconDirective.getArgument("string")?.value)

        assertEquals(initialCount + 1, basicGenerator.directives.size)
    }

    @Test
    fun `directives on constructor arguments can be used with or without annotation prefix`() {
        val noDirectiveResult = generateDirectives(basicGenerator, MyClassWithConstructorArgs::noDirective, DirectiveLocation.FIELD_DEFINITION)
        assertEquals(expected = 0, actual = noDirectiveResult.size)

        val propertyPrefixResult = generateDirectives(basicGenerator, MyClassWithConstructorArgs::propertyPrefix, DirectiveLocation.FIELD_DEFINITION)
        assertEquals(expected = 1, actual = propertyPrefixResult.size)
        assertEquals(expected = "simpleDirective", actual = propertyPrefixResult.first().name)

        val noPrefixResult = generateDirectives(basicGenerator, MyClassWithConstructorArgs::noPrefix, DirectiveLocation.FIELD_DEFINITION, MyClassWithConstructorArgs::class)
        assertEquals(expected = 1, actual = noPrefixResult.size)
        assertEquals(expected = "simpleDirective", actual = noPrefixResult.first().name)
    }

    @Test
    fun `directives on constructor arguments only works with parent class`() {
        val noPrefixResult = generateDirectives(basicGenerator, MyClassWithConstructorArgs::noPrefix, DirectiveLocation.FIELD_DEFINITION, null)
        assertEquals(expected = 0, actual = noPrefixResult.size)
    }

    @Test
    fun `exlude directive arguments @GraphQLIgnore`() {
        val directives = generateDirectives(basicGenerator, MyClass::directiveWithIgnoredArgs, DirectiveLocation.FIELD_DEFINITION)
        assertEquals(expected = 1, actual = directives.size)
        assertEquals(expected = 1, actual = directives.first().arguments.size)
        assertEquals(expected = "string", actual = directives.first().arguments.first().name)
    }

    @Test
    fun `exlude directives with invalid locations`() {
        val objectDirectives = generateDirectives(basicGenerator, MyExampleObject::class, DirectiveLocation.OBJECT)
        assertEquals(expected = 1, actual = objectDirectives.size)
        assertEquals("directiveOnObjectOnly", objectDirectives.first().name)

        val inputObjectDirectives = generateDirectives(basicGenerator, MyExampleObject::class, DirectiveLocation.INPUT_OBJECT)
        assertEquals(expected = 1, actual = inputObjectDirectives.size)
        assertEquals("directiveOnInputObjectOnly", inputObjectDirectives.first().name)

        val fieldDirectives = generateDirectives(basicGenerator, MyClass::invalidDirectives, DirectiveLocation.FIELD_DEFINITION)
        assertEquals(expected = 1, actual = fieldDirectives.size)
        assertEquals("directiveOnFieldDefinitionOnly", fieldDirectives.first().name)
    }

    companion object {
        @AfterAll
        fun cleanUp(generateDirectiveTest: GenerateDirectiveTest) {
            generateDirectiveTest.basicGenerator.close()
        }
    }
}
