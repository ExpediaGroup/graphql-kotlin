package com.expedia.graphql.schema.generator.types

import com.expedia.graphql.annotations.GraphQLDescription
import com.expedia.graphql.annotations.GraphQLDirective
import com.expedia.graphql.schema.extensions.getValidFunctions
import graphql.Scalars
import graphql.introspection.Introspection
import org.junit.jupiter.api.Test
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@Suppress("Detekt.UnusedPrivateMember")
internal class FunctionTypeTest : TypeTestHelper() {

    private lateinit var builder: FunctionTypeBuilder

    override fun beforeTest() {
        builder = FunctionTypeBuilder(generator)
    }

    @GraphQLDirective(locations = [Introspection.DirectiveLocation.QUERY])
    private annotation class FunctionDirective(val arg: String)

    @GraphQLDirective
    private annotation class ArgumentDirective(val arg: String)

    private class Happy {

        @GraphQLDescription("By bob")
        @Deprecated("No more little trees >:|")
        @FunctionDirective("happy")
        fun littleTrees() = UUID.randomUUID().toString()

        fun paint(@ArgumentDirective("red") color: String) = UUID.randomUUID().toString()
    }

    @Test
    fun `Test description`() {
        val kFunction = Happy::class.getValidFunctions(hooks)[0]
        val result = builder.function(kFunction)
        assertEquals("By bob\n\nDirectives: @FunctionDirective, deprecated", result.description)
    }

    @Test
    fun `Test deprecation`() {
        val kFunction = Happy::class.getValidFunctions(hooks)[0]
        val result = builder.function(kFunction)
        assertTrue(result.isDeprecated)
        assertEquals("No more little trees >:|", result.deprecationReason)
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
