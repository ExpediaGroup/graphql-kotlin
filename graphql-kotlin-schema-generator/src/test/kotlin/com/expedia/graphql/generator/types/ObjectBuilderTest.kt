package com.expedia.graphql.generator.types

import com.expedia.graphql.annotations.GraphQLDescription
import com.expedia.graphql.annotations.GraphQLDirective
import com.expedia.graphql.annotations.GraphQLName
import graphql.Scalars
import graphql.introspection.Introspection
import graphql.schema.GraphQLNonNull
import graphql.schema.GraphQLObjectType
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@Suppress("Detekt.UnusedPrivateClass")
internal class ObjectBuilderTest : TypeTestHelper() {

    private lateinit var builder: ObjectBuilder

    override fun beforeTest() {
        builder = ObjectBuilder(generator)
    }

    @GraphQLDirective(locations = [Introspection.DirectiveLocation.OBJECT])
    internal annotation class ObjectDirective(val arg: String)

    @GraphQLDescription("The truth")
    @ObjectDirective("Don't worry")
    private class BeHappy

    @GraphQLName("BeHappyRenamed")
    private class BeHappyCustomName

    @Test
    fun `Test naming`() {
        val result = builder.objectType(BeHappy::class) as? GraphQLObjectType
        assertNotNull(result)
        assertEquals("BeHappy", result.name)
    }

    @Test
    fun `Test custom naming`() {
        val result = builder.objectType(BeHappyCustomName::class) as? GraphQLObjectType
        assertNotNull(result)
        assertEquals("BeHappyRenamed", result.name)
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
        assertEquals(GraphQLNonNull(Scalars.GraphQLString), directive.arguments[0].type)
        assertEquals(
            directive.validLocations()?.toSet(),
            setOf(Introspection.DirectiveLocation.OBJECT)
        )
    }
}
