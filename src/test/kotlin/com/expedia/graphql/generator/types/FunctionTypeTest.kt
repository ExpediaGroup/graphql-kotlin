package com.expedia.graphql.generator.types

import com.expedia.graphql.annotations.GraphQLDescription
import com.expedia.graphql.annotations.GraphQLDirective
import com.expedia.graphql.generator.extensions.getValidFunctions
import graphql.Scalars
import graphql.introspection.Introspection
import org.junit.jupiter.api.Test
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@Suppress("Detekt.UnusedPrivateClass")
internal class FunctionTypeTest : TypeTestHelper() {

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
    }

    @Test
    fun `Test description`() {
        val kFunction = Happy::class.getValidFunctions(hooks)[0]
        val result = builder.function(kFunction)
        assertEquals("By bob", result.description)
    }

    @Test
    fun `Test description on argument`() {
        val kFunction = Happy::class.getValidFunctions(hooks)[1]
        val result = builder.function(kFunction).arguments[0]
        assertEquals("brush color", result.description)
    }

    @Test
    fun `Test deprecation`() {
        val kFunction = Happy::class.getValidFunctions(hooks)[0]
        val result = builder.function(kFunction)
        assertTrue(result.isDeprecated)
        assertEquals("No more little trees >:|", result.deprecationReason)
    }

    @Test
    fun `Test deprecation with replacement`() {
        val kFunction = Happy::class.getValidFunctions(hooks)[2]
        val result = builder.function(kFunction)
        assertTrue(result.isDeprecated)
        assertEquals("No saw, just paint, replace with littleTrees", result.deprecationReason)
    }

    @Test
    fun `Test custom directive on function`() {
        val kFunction = Happy::class.getValidFunctions(hooks)[0]
        val result = builder.function(kFunction)

        assertEquals(1, result.directives.size)
        val directive = result.directives[0]
        assertEquals("functionDirective", directive.name)
        assertEquals("happy", directive.arguments[0].value)
        assertEquals("arg", directive.arguments[0].name)
        assertEquals(Scalars.GraphQLString, directive.arguments[0].type)
        assertEquals(
            directive.validLocations()?.toSet(),
            setOf(Introspection.DirectiveLocation.QUERY)
        )
    }

    @Test
    fun `Test custom directive on function argument`() {
        val kFunction = Happy::class.getValidFunctions(hooks)[1]
        val result = builder.function(kFunction).arguments[0]

        assertEquals(1, result.directives.size)
        val directive = result.directives[0]
        assertEquals("argumentDirective", directive.name)
        assertEquals("red", directive.arguments[0].value)
        assertEquals("arg", directive.arguments[0].name)
        assertEquals(Scalars.GraphQLString, directive.arguments[0].type)
    }
}
