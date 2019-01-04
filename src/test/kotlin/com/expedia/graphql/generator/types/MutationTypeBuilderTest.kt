package com.expedia.graphql.generator.types

import com.expedia.graphql.TopLevelObject
import com.expedia.graphql.annotations.GraphQLDescription
import com.expedia.graphql.annotations.GraphQLIgnore
import com.expedia.graphql.exceptions.InvalidMutationTypeException
import com.expedia.graphql.generator.extensions.isTrue
import com.expedia.graphql.hooks.SchemaGeneratorHooks
import graphql.schema.GraphQLFieldDefinition
import kotlin.reflect.KFunction
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

@Suppress("Detekt.NestedClassesVisibility")
internal class MutationTypeBuilderTest : TypeTestHelper() {

    internal class SimpleHooks : SchemaGeneratorHooks {
        var calledHook = false
        override fun didGenerateMutationType(function: KFunction<*>, fieldDefinition: GraphQLFieldDefinition): GraphQLFieldDefinition {
            calledHook = true
            return super.didGenerateMutationType(function, fieldDefinition)
        }
    }

    class MutationObject {
        @GraphQLDescription("A GraphQL mutation method")
        fun mutation(value: Int) = value
    }

    class NoFunctions {
        @GraphQLIgnore
        fun hidden(value: Int) = value
    }

    private class PrivateMutation {
        fun echo(msg: String) = msg
    }

    private lateinit var builder: MutationTypeBuilder

    override fun beforeSetup() {
        hooks = SimpleHooks()
    }

    override fun beforeTest() {
        builder = MutationTypeBuilder(generator)
    }

    @Test
    fun `empty list`() {
        assertNull(builder.getMutationObject(emptyList()))
    }

    @Test
    fun `verify builder fails if non public mutation is specified`() {
        assertFailsWith(exceptionClass = InvalidMutationTypeException::class) {
            builder.getMutationObject(listOf(TopLevelObject(PrivateMutation())))
        }
    }

    @Test
    fun `mutation with no valid functions`() {
        val queries = listOf(TopLevelObject(NoFunctions()))
        val result = builder.getMutationObject(queries)
        assertEquals(expected = "TestTopLevelMutation", actual = result?.name)
        assertTrue(result?.fieldDefinitions?.isEmpty().isTrue())
    }

    @Test
    fun `mutation with valid functions`() {
        val queries = listOf(TopLevelObject(MutationObject()))
        val result = builder.getMutationObject(queries)
        assertEquals(expected = "TestTopLevelMutation", actual = result?.name)
        assertEquals(expected = 1, actual = result?.fieldDefinitions?.size)
        assertEquals(expected = "mutation", actual = result?.fieldDefinitions?.firstOrNull()?.name)
    }

    @Test
    fun `verify hooks are called`() {
        assertFalse((hooks as? SimpleHooks)?.calledHook.isTrue())
        builder.getMutationObject(listOf(TopLevelObject(MutationObject())))
        assertTrue((hooks as? SimpleHooks)?.calledHook.isTrue())
    }
}
