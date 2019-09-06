package com.expediagroup.graphql.generator.types

import com.expediagroup.graphql.annotations.GraphQLDescription
import com.expediagroup.graphql.annotations.GraphQLName
import com.expediagroup.graphql.test.utils.SimpleDirective
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class InputPropertyBuilderTest : TypeTestHelper() {

    private lateinit var builder: InputPropertyBuilder

    override fun beforeTest() {
        builder = InputPropertyBuilder(generator)
    }

    private data class InputPropertyTestClass(
        @GraphQLDescription("Custom description")
        val description: String,

        @GraphQLName("newName")
        val changeMe: String,

        @SimpleDirective
        val directiveWithNoPrefix: String,

        @property:SimpleDirective
        val directiveWithPrefix: String
    )

    @Test
    fun `Input property can have a description`() {
        val result = builder.inputProperty(InputPropertyTestClass::description, InputPropertyTestClass::class)
        assertEquals("Custom description", result.description)
    }

    @Test
    fun `Input property names can change`() {
        val result = builder.inputProperty(InputPropertyTestClass::changeMe, InputPropertyTestClass::class)
        assertEquals("newName", result.name)
    }

    @Test
    fun `Input property can have directives`() {
        val resultWithNoPrefix = builder.inputProperty(InputPropertyTestClass::directiveWithNoPrefix, InputPropertyTestClass::class)
        assertEquals(1, resultWithNoPrefix.directives.size)
        assertEquals("simpleDirective", resultWithNoPrefix.directives.first().name)

        val resultWithPrefix = builder.inputProperty(InputPropertyTestClass::directiveWithPrefix, InputPropertyTestClass::class)
        assertEquals(1, resultWithPrefix.directives.size)
        assertEquals("simpleDirective", resultWithPrefix.directives.first().name)
    }
}
