package com.expedia.graphql.generator.types

import com.expedia.graphql.annotations.GraphQLDescription
import com.expedia.graphql.annotations.GraphQLDirective
import com.expedia.graphql.directives.DeprecatedDirective
import com.expedia.graphql.generator.SchemaGenerator
import com.expedia.graphql.generator.extensions.isTrue
import com.expedia.graphql.getTestSchemaConfigWithMockedDirectives
import graphql.Directives
import org.junit.jupiter.api.Test
import kotlin.reflect.KClass
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class DirectiveBuilderTest {

    @GraphQLDirective
    internal annotation class SimpleDirective

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

        @DirectiveWithEnum(type = Type.TWO)
        fun directiveWithEnum(string: String) = string

        @DirectiveWithClass(kclass = Type::class)
        fun directiveWithClass(string: String) = string
    }

    private val basicGenerator = SchemaGenerator(getTestSchemaConfigWithMockedDirectives())

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
    fun `directives are not duplicated in the schema`() {
        val initialCount = basicGenerator.state.directives.size
        assertTrue(basicGenerator.state.directives.contains(Directives.IncludeDirective))
        assertTrue(basicGenerator.state.directives.contains(Directives.SkipDirective))
        assertTrue(basicGenerator.state.directives.contains(DeprecatedDirective))

        basicGenerator.directives(MyClass::simpleDirective)
        basicGenerator.directives(MyClass::simpleDirective)
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
}
