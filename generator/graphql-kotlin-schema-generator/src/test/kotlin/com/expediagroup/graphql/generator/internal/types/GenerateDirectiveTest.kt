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

import com.expediagroup.graphql.generator.SchemaGenerator
import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.generator.annotations.GraphQLDirective
import com.expediagroup.graphql.generator.annotations.GraphQLIgnore
import com.expediagroup.graphql.generator.getTestSchemaConfigWithMockedDirectives
import com.expediagroup.graphql.generator.internal.extensions.isTrue
import com.expediagroup.graphql.generator.test.utils.SimpleDirective
import graphql.introspection.Introspection.DirectiveLocation
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Test
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.test.assertEquals
import kotlin.test.assertTrue

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

    @Repeatable
    @GraphQLDirective
    annotation class RepeatableDirective(val value: String)

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

        @RepeatableDirective("foo")
        @RepeatableDirective("bar")
        @RepeatableDirective("baz")
        fun repeatedDirectives(string: String) = string
    }

    data class MyClassWithConstructorArgs(
        @SimpleDirective val noPrefix: String,
        @property:SimpleDirective val propertyPrefix: String,
        val noDirective: String
    )

    private val basicGenerator = SchemaGenerator(getTestSchemaConfigWithMockedDirectives())

    @Test
    fun `no annotation`() {
        val noAnnotation: KFunction<String> = MyClass::noAnnotation
        assertTrue(generateDirectives(basicGenerator, noAnnotation, DirectiveLocation.FIELD_DEFINITION).isEmpty().isTrue())
    }

    @Test
    fun `no directive`() {
        val noDirective: KFunction<String> = MyClass::noDirective
        assertTrue(generateDirectives(basicGenerator, noDirective, DirectiveLocation.FIELD_DEFINITION).isEmpty().isTrue())
    }

    @Test
    fun `has directive`() {
        val simpleDirective: KFunction<String> = MyClass::simpleDirective
        assertEquals(expected = 1, actual = generateDirectives(basicGenerator, simpleDirective, DirectiveLocation.FIELD_DEFINITION).size)
    }

    @Test
    fun `has directive with string`() {
        val directiveWithString: KFunction<String> = MyClass::directiveWithString
        assertEquals(expected = 1, actual = generateDirectives(basicGenerator, directiveWithString, DirectiveLocation.FIELD_DEFINITION).size)
    }

    @Test
    fun `has directive with enum`() {
        val directiveWithEnum: KFunction<String> = MyClass::directiveWithEnum
        assertEquals(expected = 1, actual = generateDirectives(basicGenerator, directiveWithEnum, DirectiveLocation.FIELD_DEFINITION).size)
    }

    @Test
    fun `has directive with class`() {
        val directiveWithClass: KFunction<String> = MyClass::directiveWithClass
        assertEquals(expected = 1, actual = generateDirectives(basicGenerator, directiveWithClass, DirectiveLocation.FIELD_DEFINITION).size)
    }

    @Test
    fun `directives are only added to the schema once`() {
        val initialCount = basicGenerator.directives.size
        val simpleDirective: KFunction<String> = MyClass::simpleDirective
        val firstInvocation = generateDirectives(basicGenerator, simpleDirective, DirectiveLocation.FIELD_DEFINITION)
        assertEquals(1, firstInvocation.size)
        val secondInvocation = generateDirectives(basicGenerator, simpleDirective, DirectiveLocation.FIELD_DEFINITION)
        assertEquals(1, secondInvocation.size)
        assertEquals(firstInvocation.first(), secondInvocation.first())
        assertEquals(initialCount + 1, basicGenerator.directives.size)
    }

    @Test
    fun `directives are valid on enum values`() {
        val field = Type::class.java.getField("ONE")

        val directives = generateEnumValueDirectives(basicGenerator, field)

        assertEquals(2, directives.size)
        assertEquals("directiveWithString", directives.first().name)
        assertEquals("directiveWithClass", directives.last().name)
    }

    @Test
    fun `directives are empty on an enum with no valid annotations`() {
        val field = Type::class.java.getField("TWO")

        val directives = generateEnumValueDirectives(basicGenerator, field)

        assertEquals(0, directives.size)
    }

    @Test
    fun `directives are created per each declaration`() {
        val initialCount = basicGenerator.directives.size
        val directiveWithString: KFunction<String> = MyClass::directiveWithString
        val directiveWithAnotherString: KFunction<String> = MyClass::directiveWithAnotherString
        val directivesOnFirstField = generateDirectives(basicGenerator, directiveWithString, DirectiveLocation.FIELD_DEFINITION)
        val directivesOnSecondField = generateDirectives(basicGenerator, directiveWithAnotherString, DirectiveLocation.FIELD_DEFINITION)
        assertEquals(expected = 1, actual = directivesOnFirstField.size)
        assertEquals(expected = 1, actual = directivesOnSecondField.size)

        val firstDirective = directivesOnFirstField.first()
        val secondDirective = directivesOnSecondField.first()
        assertEquals("directiveWithString", firstDirective.name)
        assertEquals("directiveWithString", secondDirective.name)
        assertEquals("foo", firstDirective.getArgument("string")?.argumentValue?.value)
        assertEquals("bar", secondDirective.getArgument("string")?.argumentValue?.value)

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
    fun `exclude directive arguments @GraphQLIgnore`() {
        val directiveWithIgnoredArgs: KFunction<String> = MyClass::directiveWithIgnoredArgs
        val directives = generateDirectives(basicGenerator, directiveWithIgnoredArgs, DirectiveLocation.FIELD_DEFINITION)
        assertEquals(expected = 1, actual = directives.size)
        assertEquals(expected = 1, actual = directives.first().arguments.size)
        assertEquals(expected = "string", actual = directives.first().arguments.first().name)
    }

    @Test
    fun `exclude directives with invalid locations`() {
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

    @Test
    fun `repeatable directives are supported`() {
        val repeatableDirectiveResult = generateDirectives(basicGenerator, MyClass::repeatedDirectives, DirectiveLocation.FIELD_DEFINITION)
        assertEquals(3, repeatableDirectiveResult.size)
        assertEquals("repeatableDirective", repeatableDirectiveResult[0].name)
        assertEquals("foo", repeatableDirectiveResult[0].getArgument("value")?.argumentValue?.value)
        assertEquals("repeatableDirective", repeatableDirectiveResult[1].name)
        assertEquals("bar", repeatableDirectiveResult[1].getArgument("value")?.argumentValue?.value)
        assertEquals("repeatableDirective", repeatableDirectiveResult[2].name)
        assertEquals("baz", repeatableDirectiveResult[2].getArgument("value")?.argumentValue?.value)
    }

    companion object {
        @AfterAll
        fun cleanUp(generateDirectiveTest: GenerateDirectiveTest) {
            generateDirectiveTest.basicGenerator.close()
        }
    }
}
