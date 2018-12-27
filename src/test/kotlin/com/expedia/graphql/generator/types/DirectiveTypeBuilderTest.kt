package com.expedia.graphql.generator.types

import com.expedia.graphql.annotations.GraphQLDescription
import com.expedia.graphql.annotations.GraphQLDirective
import com.expedia.graphql.generator.SchemaGenerator
import com.expedia.graphql.generator.extensions.isTrue
import com.expedia.graphql.testSchemaConfig
import org.junit.jupiter.api.Test
import kotlin.reflect.KClass
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class DirectiveTypeBuilderTest {

    @GraphQLDirective
    internal annotation class SimpleDirective

    @GraphQLDirective
    internal annotation class DirectiveWithString(val string: String)

    internal enum class Type {
        ONE, TWO
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

    private val basicGenerator = SchemaGenerator(emptyList(), emptyList(), testSchemaConfig)

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
}
