package com.expedia.graphql.schema.generator.types

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class InputTypeTest {

    private class TestClass

    @Test
    fun getGraphQLInputClassName() {
        val actual = getInputClassName(TestClass::class)
        assertEquals(expected = "TestClassInput", actual = actual)
    }
}
