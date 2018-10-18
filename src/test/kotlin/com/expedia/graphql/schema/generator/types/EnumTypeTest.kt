package com.expedia.graphql.schema.generator.types

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class EnumTypeTest {

    private enum class MyTestEnum {
        ONE,
        TWO
    }

    @Test
    fun enumType() {
        val actual = enumType(MyTestEnum::class)
        assertEquals(expected = 2, actual = actual.values.size)
        assertEquals(expected = "ONE", actual = actual.values[0].value)
        assertEquals(expected = "TWO", actual = actual.values[1].value)
    }
}
