package com.expedia.graphql.schema.generator.types

import com.expedia.graphql.annotations.GraphQLDescription
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class InputObjectTypeTest : TypeTestHelper() {

    private lateinit var builder: InputObjectTypeBuilder

    override fun beforeTest() {
        builder = InputObjectTypeBuilder(generator)
    }

    @Suppress("Detekt.UnusedPrivateClass")
    @GraphQLDescription("The truth")
    private class InputClass

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
}
