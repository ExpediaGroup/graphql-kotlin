package com.expedia.graphql.schema.generator.types

import com.expedia.graphql.annotations.GraphQLDescription
import com.expedia.graphql.schema.extensions.getValidProperties
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class PropertyTypeTest : TypeTestHelper() {

    private class HappyClass {
        @GraphQLDescription("The truth")
        @Deprecated("It's not a lie")
        lateinit var cake: String
    }

    private lateinit var builder: PropertyTypeBuilder

    override fun beforeTest() {
        mockScalarTypeBuilder()

        builder = PropertyTypeBuilder(generator)
    }

    @Test
    fun `Test deprecation`() {
        val prop = HappyClass::class.getValidProperties(hooks)[0]

        val result = builder.property(prop)
        assertTrue(result.isDeprecated)
        assertEquals("It's not a lie", result.deprecationReason)
    }

    @Test
    fun `Test description`() {
        val prop = HappyClass::class.getValidProperties(hooks)[0]

        val result = builder.property(prop)
        assertEquals("The truth\n\nDirectives: deprecated", result.description)
    }
}
