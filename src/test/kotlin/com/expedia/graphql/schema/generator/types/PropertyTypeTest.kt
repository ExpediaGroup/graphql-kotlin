package com.expedia.graphql.schema.generator.types

import com.expedia.graphql.annotations.GraphQLDescription
import com.expedia.graphql.annotations.GraphQLDirective
import com.expedia.graphql.schema.extensions.getValidProperties
import graphql.Scalars
import graphql.introspection.Introspection
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class PropertyTypeTest : TypeTestHelper() {

    @GraphQLDirective(locations = [Introspection.DirectiveLocation.FIELD])
    private annotation class PropertyDirective(val arg: String)

    private class HappyClass {
        @GraphQLDescription("The truth")
        @Deprecated("It's not a lie")
        @PropertyDirective("trust me")
        lateinit var cake: String
    }

    private lateinit var builder: PropertyTypeBuilder

    override fun beforeTest() {
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
        assertEquals("The truth\n\nDirectives: @PropertyDirective, deprecated", result.description)
    }

    @Test
    fun `Test custom directive`() {
        val prop = HappyClass::class.getValidProperties(hooks)[0]

        val result = builder.property(prop)

        assertEquals(1, result.directives.size)
        val directive = result.directives[0]
        assertEquals("propertyDirective", directive.name)
        assertEquals("trust me", directive.arguments[0].value)
        assertEquals("arg", directive.arguments[0].name)
        assertEquals(Scalars.GraphQLString, directive.arguments[0].type)
        assertEquals(
            directive.validLocations()?.toSet(),
            setOf(Introspection.DirectiveLocation.FIELD)
        )
    }
}
