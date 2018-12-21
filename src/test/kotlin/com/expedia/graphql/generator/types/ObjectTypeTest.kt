package com.expedia.graphql.generator.types

import com.expedia.graphql.annotations.GraphQLDescription
import com.expedia.graphql.annotations.GraphQLDirective
import graphql.Scalars
import graphql.introspection.Introspection
import graphql.schema.GraphQLObjectType
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@Suppress("Detekt.UnusedPrivateClass")
internal class ObjectTypeTest : TypeTestHelper() {

    private lateinit var builder: ObjectTypeBuilder

    override fun beforeTest() {
        builder = ObjectTypeBuilder(generator)
    }

    @GraphQLDirective(locations = [Introspection.DirectiveLocation.OBJECT])
    internal annotation class ObjectDirective(val arg: String)

    @GraphQLDescription("The truth")
    @ObjectDirective("Don't worry")
    private class BeHappy

    @Test
    fun `Test naming`() {
        val result = builder.objectType(BeHappy::class) as? GraphQLObjectType
        assertNotNull(result)
        assertEquals("BeHappy", result.name)
    }

    @Test
    fun `Test description`() {
        val result = builder.objectType(BeHappy::class) as? GraphQLObjectType
        assertNotNull(result)
        assertEquals("The truth", result.description)
    }

    @Test
    fun `Test custom directive`() {
        val result = builder.objectType(BeHappy::class) as? GraphQLObjectType
        assertNotNull(result)
        assertEquals(1, result.directives.size)

        val directive = result.directives[0]
        assertEquals("objectDirective", directive.name)
        assertEquals("Don't worry", directive.arguments[0].value)
        assertEquals("arg", directive.arguments[0].name)
        assertEquals(Scalars.GraphQLString, directive.arguments[0].type)
        assertEquals(
            directive.validLocations()?.toSet(),
            setOf(Introspection.DirectiveLocation.OBJECT)
        )
    }
}
