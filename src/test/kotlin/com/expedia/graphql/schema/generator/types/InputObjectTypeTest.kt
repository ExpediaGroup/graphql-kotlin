package com.expedia.graphql.schema.generator.types

import com.expedia.graphql.annotations.GraphQLDescription
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class InputObjectTypeTest : TypeTestHelper() {

    private lateinit var builder: InputObjectTypeBuilder

    override fun beforeTest() {
        builder = InputObjectTypeBuilder(generator)
    }

    @GraphQLDescription("The truth")
    private class HappyClass

    @Test
    fun `Test naming`() {
        val result = builder.inputObjectType(HappyClass::class)
        assertEquals("HappyClassInput", result.name)
    }

    @Test
    fun `Test description`() {
        val result = builder.inputObjectType(HappyClass::class)
        assertEquals("The truth", result.description)
    }
}
