package com.expedia.graphql.generator.types

import com.expedia.graphql.annotations.GraphQLDescription
import com.expedia.graphql.annotations.GraphQLDirective
import com.expedia.graphql.exceptions.GraphQLKotlinException
import com.expedia.graphql.generator.SchemaGenerator
import com.expedia.graphql.generator.extensions.isTrue
import com.expedia.graphql.testSchemaConfig
import org.junit.jupiter.api.Test
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.full.declaredMemberFunctions
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class DirectiveTypeBuilderTest {

    @GraphQLDirective
    internal annotation class SimpleDirective

    @GraphQLDirective
    internal annotation class DiretiveWithString(val string: String)

    internal enum class Type {
        ONE, TWO
    }

    @GraphQLDirective
    internal annotation class DiretiveWithEnum(val type: Type)

    @GraphQLDirective
    internal annotation class DiretiveWithClass(val kclass: KClass<*>)

    internal class MyClass {

        fun noAnnotation(string: String) = string

        @GraphQLDescription("test")
        fun noDirective(string: String) = string

        @SimpleDirective
        fun simpleDirective(string: String) = string

        @DiretiveWithString(string = "foo")
        fun directiveWithString(string: String) = string

        @DiretiveWithEnum(type = Type.TWO)
        fun directiveWithEnum(string: String) = string

        @DiretiveWithClass(kclass = Type::class)
        fun directiveWithClass(string: String) = string
    }

    private fun KClass<*>.getMemberFunction(name: String): KFunction<*> =
        this.declaredMemberFunctions.find { it.name == name } ?: throw GraphQLKotlinException("No functino of name $name")

    private val basicGenerator = SchemaGenerator(emptyList(), emptyList(), testSchemaConfig)

    @Test
    fun `no annotation`() {
        val function = MyClass::class.getMemberFunction("noAnnotation")
        assertTrue(basicGenerator.directives(function).isEmpty().isTrue())
    }

    @Test
    fun `no directive`() {
        val function = MyClass::class.getMemberFunction("noDirective")
        assertTrue(basicGenerator.directives(function).isEmpty().isTrue())
    }

    @Test
    fun `has directive`() {
        val function = MyClass::class.getMemberFunction("simpleDirective")
        assertEquals(expected = 1, actual = basicGenerator.directives(function).size)
    }

    @Test
    fun `has directive with string`() {
        val function = MyClass::class.getMemberFunction("directiveWithString")
        assertEquals(expected = 1, actual = basicGenerator.directives(function).size)
    }

    @Test
    fun `has directive with enum`() {
        val function = MyClass::class.getMemberFunction("directiveWithEnum")
        assertEquals(expected = 1, actual = basicGenerator.directives(function).size)
    }

    @Test
    fun `has directive with class`() {
        val function = MyClass::class.getMemberFunction("directiveWithClass")
        assertEquals(expected = 1, actual = basicGenerator.directives(function).size)
    }
}
