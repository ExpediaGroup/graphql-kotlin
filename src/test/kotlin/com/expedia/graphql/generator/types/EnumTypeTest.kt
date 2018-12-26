package com.expedia.graphql.generator.types

import com.expedia.graphql.annotations.GraphQLDescription
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

internal class EnumTypeTest : TypeTestHelper() {

    @Suppress("Detekt.UnusedPrivateClass")
    @GraphQLDescription("MyTestEnum description")
    private enum class MyTestEnum {
        @GraphQLDescription("enum 'ONE' description")
        ONE,

        @GraphQLDescription("enum 'TWO' description")
        @Deprecated("Deprecated enum value")
        TWO,

        @Deprecated("THREE is out", replaceWith = ReplaceWith("TWO"))
        THREE
    }

    lateinit var builder: EnumTypeBuilder

    override fun beforeTest() {
        super.beforeTest()
        builder = EnumTypeBuilder(generator)
    }

    @Test
    fun enumType() {
        val actual = builder.enumType(MyTestEnum::class)
        assertEquals(expected = 3, actual = actual.values.size)
        assertEquals(expected = "MyTestEnum", actual = actual.name)
        assertEquals(expected = "ONE", actual = actual.values[0].value)
        assertEquals(expected = "TWO", actual = actual.values[1].value)
        assertEquals(expected = "THREE", actual = actual.values[2].value)
    }

    @Test
    fun `Description on enum class and values`() {
        val gqlEnum = assertNotNull(builder.enumType(MyTestEnum::class))
        assertEquals("MyTestEnum description", gqlEnum.description)

        assertEquals("enum 'ONE' description", assertNotNull(gqlEnum.getValue("ONE")).description)
        assertEquals("enum 'TWO' description", assertNotNull(gqlEnum.getValue("TWO")).description)
        assertNull(gqlEnum.getValue("THREE").description)
    }

    @Test
    fun `Deprecation on enum values`() {
        val gqlEnum = assertNotNull(builder.enumType(MyTestEnum::class))

        val one = assertNotNull(gqlEnum.getValue("ONE"))
        assertFalse(one.isDeprecated)
        assertNull(one.deprecationReason)

        val two = assertNotNull(gqlEnum.getValue("TWO"))
        assertTrue(two.isDeprecated)
        assertEquals("Deprecated enum value", two.deprecationReason)

        val three = assertNotNull(gqlEnum.getValue("THREE"))
        assertTrue(three.isDeprecated)
        assertEquals("THREE is out, replace with TWO", three.deprecationReason)
    }
}
