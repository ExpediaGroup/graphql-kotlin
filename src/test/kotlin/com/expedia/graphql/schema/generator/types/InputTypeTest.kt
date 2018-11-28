package com.expedia.graphql.schema.generator.types

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class InputTypeTest : TypeTestHelper() {

    private class TestClass

    @Test
    fun `Test building`() {
        val builder = InputObjectTypeBuilder(generator)
        val result = builder.inputObjectType(TestClass::class)

        assertEquals(expected = "TestClassInput", actual = result.name)
    }
}
