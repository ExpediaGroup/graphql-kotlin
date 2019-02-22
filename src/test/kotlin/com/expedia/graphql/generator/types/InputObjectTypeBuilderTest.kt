package com.expedia.graphql.generator.types

import com.expedia.graphql.annotations.GraphQLDescription
import com.expedia.graphql.utils.SimpleDirective
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class InputObjectTypeBuilderTest : TypeTestHelper() {

    private lateinit var builder: InputObjectTypeBuilder

    override fun beforeTest() {
        builder = InputObjectTypeBuilder(generator)
    }

    @Suppress("Detekt.UnusedPrivateClass")
    @GraphQLDescription("The truth")
    @SimpleDirective
    private class InputClass {

        @SimpleDirective
        val myField: String = "car"
    }

    @Test
    fun `Test naming`() {
        val result = builder.inputObjectType(InputClass::class)
        assertEquals("InputClassInput", result.name)
    }

    @Test
    fun `Test description`() {
        val result = builder.inputObjectType(InputClass::class)
        assertEquals("The truth", result.description)
    }

    @Test
    fun `directives should be on input objects`() {
        val result = builder.inputObjectType(InputClass::class)
        assertEquals(1, result.directives.size)
        assertEquals("simpleDirective", result.directives.first().name)
    }

    @Test
    fun `directives should be on input object fields`() {
        val result = builder.inputObjectType(InputClass::class)
        assertEquals(1, result.fields.first().directives.size)
        assertEquals("simpleDirective", result.fields.first().directives.first().name)
    }
}
