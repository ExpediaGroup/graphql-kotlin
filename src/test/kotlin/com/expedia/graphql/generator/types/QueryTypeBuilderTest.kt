package com.expedia.graphql.generator.types

import com.expedia.graphql.TopLevelObject
import com.expedia.graphql.annotations.GraphQLDescription
import com.expedia.graphql.annotations.GraphQLIgnore
import com.expedia.graphql.exceptions.InvalidSchemaException
import com.expedia.graphql.generator.extensions.isTrue
import com.expedia.graphql.hooks.SchemaGeneratorHooks
import graphql.schema.GraphQLFieldDefinition
import kotlin.reflect.KFunction
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class QueryTypeBuilderTest : TypeTestHelper() {

    internal class SimpleHooks : SchemaGeneratorHooks {
        var calledHook = false
        override fun didGenerateQueryType(function: KFunction<*>, fieldDefinition: GraphQLFieldDefinition): GraphQLFieldDefinition {
            calledHook = true
            return super.didGenerateQueryType(function, fieldDefinition)
        }
    }

    internal class QueryObject {
        @GraphQLDescription("A GraphQL query method")
        fun query(value: Int) = value
    }

    internal class NoFunctions {
        @GraphQLIgnore
        fun hidden(value: Int) = value
    }

    private lateinit var builder: QueryTypeBuilder

    override fun beforeSetup() {
        hooks = SimpleHooks()
    }

    override fun beforeTest() {
        builder = QueryTypeBuilder(generator)
    }

    @Test
    fun `empty list`() {
        assertFailsWith(InvalidSchemaException::class) {
            builder.getQueryObject(emptyList())
        }
    }

    @Test
    fun `query with no valid functions`() {
        val queries = listOf(TopLevelObject(NoFunctions()))
        val result = builder.getQueryObject(queries)
        assertEquals(expected = "TestTopLevelQuery", actual = result.name)
        assertTrue(result.fieldDefinitions.isEmpty())
    }

    @Test
    fun `query with valid functions`() {
        val queries = listOf(TopLevelObject(QueryObject()))
        val result = builder.getQueryObject(queries)
        assertEquals(expected = "TestTopLevelQuery", actual = result.name)
        assertEquals(expected = 1, actual = result.fieldDefinitions.size)
        assertEquals(expected = "query", actual = result.fieldDefinitions.first().name)
    }

    @Test
    fun `verify hooks are called`() {
        assertFalse((hooks as? SimpleHooks)?.calledHook.isTrue())
        builder.getQueryObject(listOf(TopLevelObject(QueryObject())))
        assertTrue((hooks as? SimpleHooks)?.calledHook.isTrue())
    }
}
