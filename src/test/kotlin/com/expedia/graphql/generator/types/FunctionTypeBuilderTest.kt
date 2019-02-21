package com.expedia.graphql.generator.types

import com.expedia.graphql.annotations.GraphQLContext
import com.expedia.graphql.annotations.GraphQLDescription
import com.expedia.graphql.annotations.GraphQLDirective
import com.expedia.graphql.execution.FunctionDataFetcher
import graphql.Scalars
import graphql.introspection.Introspection
import graphql.schema.DataFetchingEnvironment
import graphql.schema.GraphQLNonNull
import org.junit.jupiter.api.Test
import java.util.UUID
import java.util.concurrent.CompletableFuture
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@Suppress("Detekt.UnusedPrivateClass")
internal class FunctionTypeBuilderTest : TypeTestHelper() {

    private lateinit var builder: FunctionTypeBuilder

    override fun beforeTest() {
        builder = FunctionTypeBuilder(generator)
    }

    internal interface MyInterface {
        fun printMessage(message: String): String
    }

    internal class MyImplementation : MyInterface {
        override fun printMessage(message: String): String = "message=$message"
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

        @Deprecated("No saw, just paint", replaceWith = ReplaceWith("paint"))
        fun saw(tree: String) = tree

        fun context(@GraphQLContext context: String, string: String) = "$context and $string"

        fun completableFuture(num: Int): CompletableFuture<Int> = CompletableFuture.completedFuture(num)

        fun dataFetchingEnvironment(environment: DataFetchingEnvironment): String = environment.field.name
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
        assertEquals("No saw, just paint, replace with paint", result.deprecationReason)
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
        val kFunction = Happy::context
        val result = builder.function(kFunction)

        assertTrue(result.directives.isEmpty())
        assertEquals(expected = 1, actual = result.arguments.size)
        val arg = result.arguments.firstOrNull()
        assertEquals(expected = "string", actual = arg?.name)
    }

    @Test
    fun `Non-abstract function`() {
        val kFunction = MyInterface::printMessage
        val result = builder.function(fn = kFunction, target = null, abstract = false)

        assertEquals(expected = 1, actual = result.arguments.size)
        assertTrue(result.dataFetcher is FunctionDataFetcher)
    }

    @Test
    fun `Abstract function`() {
        val kFunction = MyInterface::printMessage
        val result = builder.function(fn = kFunction, target = null, abstract = true)

        assertEquals(expected = 1, actual = result.arguments.size)
        assertFalse(result.dataFetcher is FunctionDataFetcher)
    }

    @Test
    fun `Abstract function with target`() {
        val kFunction = MyInterface::printMessage
        val result = builder.function(fn = kFunction, target = MyImplementation(), abstract = true)

        assertEquals(expected = 1, actual = result.arguments.size)
        assertFalse(result.dataFetcher is FunctionDataFetcher)
    }

    @Test
    fun `completable future return type is valid`() {
        val kFunction = Happy::completableFuture
        val result = builder.function(fn = kFunction)

        assertEquals(expected = 1, actual = result.arguments.size)
        assertEquals("Int", (result.type as? GraphQLNonNull)?.wrappedType?.name)
    }

    @Test
    fun `DataFetchingEnvironment argument type is ignored`() {
        val kFunction = Happy::dataFetchingEnvironment
        val result = builder.function(fn = kFunction)

        assertEquals(expected = 0, actual = result.arguments.size)
        assertEquals("String", (result.type as? GraphQLNonNull)?.wrappedType?.name)
    }
}
