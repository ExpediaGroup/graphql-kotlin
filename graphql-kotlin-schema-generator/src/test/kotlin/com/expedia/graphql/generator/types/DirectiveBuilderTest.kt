package com.expedia.graphql.generator.types

import com.expedia.graphql.annotations.GraphQLDescription
import com.expedia.graphql.annotations.GraphQLDirective
import com.expedia.graphql.directives.DeprecatedDirective
import com.expedia.graphql.generator.SchemaGenerator
import com.expedia.graphql.generator.extensions.isTrue
import com.expedia.graphql.getTestSchemaConfigWithMockedDirectives
import com.expedia.graphql.test.utils.SimpleDirective
import graphql.Directives
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.reflect.KClass
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class DirectiveBuilderTest {

    @GraphQLDirective
    internal annotation class DirectiveWithString(val string: String)

    internal enum class Type {
        @DirectiveWithString("my string")
        @DirectiveWithClass(SimpleDirective::class)
        ONE,

        @GraphQLDescription("my description")
        TWO
    }

    @GraphQLDirective
    internal annotation class DirectiveWithEnum(val type: Type)

    @GraphQLDirective
    internal annotation class DirectiveWithClass(val kclass: KClass<*>)

    internal class MyClass {

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
    }

    internal data class MyClassWithConstructorArgs(
        @SimpleDirective val noPrefix: String,
        @property:SimpleDirective val propertyPrefix: String,
        val noDirective: String
    )

    private lateinit var basicGenerator: SchemaGenerator

    @BeforeEach
    fun setUp() {
        basicGenerator = SchemaGenerator(getTestSchemaConfigWithMockedDirectives())
    }

    @Test
    fun `no annotation`() {
        assertTrue(basicGenerator.directives(MyClass::noAnnotation).isEmpty().isTrue())
    }

    @Test
    fun `no directive`() {
        assertTrue(basicGenerator.directives(MyClass::noDirective).isEmpty().isTrue())
    }

    @Test
    fun `has directive`() {
        assertEquals(expected = 1, actual = basicGenerator.directives(MyClass::simpleDirective).size)
    }

    @Test
    fun `has directive with string`() {
        assertEquals(expected = 1, actual = basicGenerator.directives(MyClass::directiveWithString).size)
    }

    @Test
    fun `has directive with enum`() {
        assertEquals(expected = 1, actual = basicGenerator.directives(MyClass::directiveWithEnum).size)
    }

    @Test
    fun `has directive with class`() {
        assertEquals(expected = 1, actual = basicGenerator.directives(MyClass::directiveWithClass).size)
    }

    @Test
    fun `directives are only added to the schema once`() {
        val initialCount = basicGenerator.state.directives.size
        assertTrue(basicGenerator.state.directives.containsKey(Directives.IncludeDirective.name))
        assertTrue(basicGenerator.state.directives.containsKey(Directives.SkipDirective.name))
        assertTrue(basicGenerator.state.directives.containsKey(DeprecatedDirective.name))

        val firstInvocation = basicGenerator.directives(MyClass::simpleDirective)
        assertEquals(1, firstInvocation.size)
        val secondInvocation = basicGenerator.directives(MyClass::simpleDirective)
        assertEquals(1, secondInvocation.size)
        assertEquals(firstInvocation.first(), secondInvocation.first())
        assertEquals(initialCount + 1, basicGenerator.state.directives.size)
    }

    @Test
    fun `directives are valid on fields (enum values)`() {
        val field = Type::class.java.getField("ONE")

        val directives = basicGenerator.fieldDirectives(field)

        assertEquals(2, directives.size)
        assertEquals("directiveWithString", directives.first().name)
        assertEquals("directiveWithClass", directives.last().name)
    }

    @Test
    fun `directives are empty on an enum with no valid annotations`() {
        val field = Type::class.java.getField("TWO")

        val directives = basicGenerator.fieldDirectives(field)

        assertEquals(0, directives.size)
    }

    @Test
    fun `directives are created per each declaration`() {
        val initialCount = basicGenerator.state.directives.size
        val directivesOnFirstField = basicGenerator.directives(MyClass::directiveWithString)
        val directivesOnSecondField = basicGenerator.directives(MyClass::directiveWithAnotherString)
        assertEquals(expected = 1, actual = directivesOnFirstField.size)
        assertEquals(expected = 1, actual = directivesOnSecondField.size)

        val firstDirective = directivesOnFirstField.first()
        val seconDirective = directivesOnSecondField.first()
        assertEquals("directiveWithString", firstDirective.name)
        assertEquals("directiveWithString", seconDirective.name)
        assertEquals("foo", firstDirective.getArgument("string")?.value)
        assertEquals("bar", seconDirective.getArgument("string")?.value)

        assertEquals(initialCount + 1, basicGenerator.state.directives.size)
    }

    @Test
    fun `directives on constructor arguments can be used with or without annotation prefix`() {
        val noDirectiveResult = basicGenerator.directives(MyClassWithConstructorArgs::noDirective)
        assertEquals(expected = 0, actual = noDirectiveResult.size)

        val propertyPrefixResult = basicGenerator.directives(MyClassWithConstructorArgs::propertyPrefix)
        assertEquals(expected = 1, actual = propertyPrefixResult.size)
        assertEquals(expected = "simpleDirective", actual = propertyPrefixResult.first().name)

        val noPrefixResult = basicGenerator.directives(MyClassWithConstructorArgs::noPrefix, MyClassWithConstructorArgs::class)
        assertEquals(expected = 1, actual = noPrefixResult.size)
        assertEquals(expected = "simpleDirective", actual = noPrefixResult.first().name)
    }

    @Test
    fun `directives on constructor arguments only works with parent class`() {
        val noPrefixResult = basicGenerator.directives(MyClassWithConstructorArgs::noPrefix, null)
        assertEquals(expected = 0, actual = noPrefixResult.size)
    }
}
