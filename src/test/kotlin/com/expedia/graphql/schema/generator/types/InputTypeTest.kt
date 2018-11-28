package com.expedia.graphql.schema.generator.types

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class InputTypeTest : TypeTestHelper() {

    private class TestClass

    @Test
    fun `Test building`() {
        val builder = InputObjectTypeBuilder(generator)
        val inputObjectType = builder.inputObjectType(TestClass::class)

        assertEquals(expected = "TestClassInput", actual = inputObjectType.name)
    }
}
