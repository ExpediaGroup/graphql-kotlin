package com.expedia.graphql.schema.generator.types

import com.expedia.graphql.annotations.GraphQLDescription
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

internal class EnumTypeTest {
    @GraphQLDescription("MyTestEnum description")
    private enum class MyTestEnum {
        @GraphQLDescription("enum 'ONE' description")
        ONE,

        @Deprecated("Deprecated enum value")
        TWO
    }

    @Test
    fun enumType() {
        val actual = enumType(MyTestEnum::class)
        assertEquals(expected = 2, actual = actual.values.size)
        assertEquals(expected = "MyTestEnum", actual = actual.name)
        assertEquals(expected = "ONE", actual = actual.values[0].value)
        assertEquals(expected = "TWO", actual = actual.values[1].value)
    }

    @Test
    fun `Description on enum class and values`() {
        val gqlEnum = assertNotNull(enumType(MyTestEnum::class))
        assertEquals("MyTestEnum description", gqlEnum.description)

        val one = assertNotNull(gqlEnum.getValue("ONE"))
        assertEquals("enum 'ONE' description", one.description)
    }

    @Test
    fun `Deprecation on enum values`() {
        val gqlEnum = assertNotNull(enumType(MyTestEnum::class))

        val one = assertNotNull(gqlEnum.getValue("ONE"))
        assertFalse(one.isDeprecated)
        assertNull(one.deprecationReason)

        val two = assertNotNull(gqlEnum.getValue("TWO"))
        assertTrue(two.isDeprecated)
        assertEquals("Deprecated enum value", two.deprecationReason)
    }
}
