package com.expedia.graphql.generator.types

import com.expedia.graphql.annotations.GraphQLContext
import com.expedia.graphql.annotations.GraphQLDescription
import com.expedia.graphql.annotations.GraphQLDirective
import graphql.Scalars
import graphql.introspection.Introspection
import graphql.schema.GraphQLNonNull
import org.junit.jupiter.api.Test
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@Suppress("Detekt.UnusedPrivateClass")
internal class FunctionTypeBuilderTest : TypeTestHelper() {

    private lateinit var builder: FunctionTypeBuilder

    override fun beforeTest() {
        builder = FunctionTypeBuilder(generator)
    }

    @GraphQLDirective(locations = [Introspection.DirectiveLocation.QUERY])
    internal annotation class FunctionDirective(val arg: String)

    @GraphQLDirective
    internal annotation class ArgumentDirective(val arg: String)

    private class Happy {

        @GraphQLDescription("By bob")
        @Deprecated("No more little trees >:|")
        @FunctionDirective("happy")
        fun littleTrees() = UUID.randomUUID().toString()

        fun paint(@GraphQLDescription("brush color") @ArgumentDirective("red") color: String) = color.reversed()

        @Deprecated("No saw, just paint", replaceWith = ReplaceWith("littleTrees"))
        fun saw(tree: String) = tree

        fun print(@GraphQLContext context: String, string: String) = string
    }

    @Test
    fun `Test description`() {
        val kFunction = Happy::littleTrees
        val result = builder.function(kFunction)
        assertEquals("By bob", result.description)
    }

    @Test
    fun `Test description on argument`() {
        val kFunction = Happy::paint
        val result = builder.function(kFunction).arguments[0]
        assertEquals("brush color", result.description)
    }

    @Test
    fun `Test deprecation`() {
        val kFunction = Happy::littleTrees
        val result = builder.function(kFunction)
        assertTrue(result.isDeprecated)
        assertEquals("No more little trees >:|", result.deprecationReason)
    }

    @Test
    fun `Test deprecation with replacement`() {
        val kFunction = Happy::saw
        val result = builder.function(kFunction)
        assertTrue(result.isDeprecated)
        assertEquals("No saw, just paint, replace with littleTrees", result.deprecationReason)
    }

    @Test
    fun `Test custom directive on function`() {
        val kFunction = Happy::littleTrees
        val result = builder.function(kFunction)

        assertEquals(1, result.directives.size)
        val directive = result.directives[0]
        assertEquals("functionDirective", directive.name)
        assertEquals("happy", directive.arguments[0].value)
        assertEquals("arg", directive.arguments[0].name)
        assertEquals(GraphQLNonNull(Scalars.GraphQLString), directive.arguments[0].type)
        assertEquals(
            directive.validLocations()?.toSet(),
            setOf(Introspection.DirectiveLocation.QUERY)
        )
    }

    @Test
    fun `Test custom directive on function argument`() {
        val kFunction = Happy::paint
        val result = builder.function(kFunction).arguments[0]

        assertEquals(1, result.directives.size)
        val directive = result.directives[0]
        assertEquals("argumentDirective", directive.name)
        assertEquals("red", directive.arguments[0].value)
        assertEquals("arg", directive.arguments[0].name)
        assertEquals(GraphQLNonNull(Scalars.GraphQLString), directive.arguments[0].type)
    }

    @Test
    fun `Test context on argument`() {
        val kFunction = Happy::print
        val result = builder.function(kFunction)

        assertTrue(result.directives.isEmpty())
        assertEquals(expected = 1, actual = result.arguments.size)
        val arg = result.arguments.firstOrNull()
        assertEquals(expected = "string", actual = arg?.name)
    }
}
