package com.expedia.graphql.generator.types

import com.expedia.graphql.TopLevelObject
import com.expedia.graphql.annotations.GraphQLDescription
import com.expedia.graphql.annotations.GraphQLIgnore
import com.expedia.graphql.exceptions.InvalidQueryTypeException
import com.expedia.graphql.generator.extensions.isTrue
import com.expedia.graphql.hooks.SchemaGeneratorHooks
import com.expedia.graphql.test.utils.SimpleDirective
import graphql.schema.GraphQLFieldDefinition
import org.junit.jupiter.api.Test
import kotlin.reflect.KFunction
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@Suppress("Detekt.NestedClassesVisibility")
internal class QueryBuilderTest : TypeTestHelper() {

    internal class SimpleHooks : SchemaGeneratorHooks {
        var calledHook = false
        override fun didGenerateQueryType(function: KFunction<*>, fieldDefinition: GraphQLFieldDefinition): GraphQLFieldDefinition {
            calledHook = true
            return super.didGenerateQueryType(function, fieldDefinition)
        }
    }

    private class PrivateQuery {
        fun echo(msg: String) = msg
    }

    @SimpleDirective
    class QueryObject {
        @GraphQLDescription("A GraphQL query method")
        fun query(value: Int) = value
    }

    class NoFunctions {
        @GraphQLIgnore
        fun hidden(value: Int) = value
    }

    private lateinit var builder: QueryBuilder

    override fun beforeSetup() {
        hooks = SimpleHooks()
    }

    override fun beforeTest() {
        builder = QueryBuilder(generator)
    }

    @Test
    fun `verify builder fails if non public query is specified`() {
        assertFailsWith(exceptionClass = InvalidQueryTypeException::class) {
            builder.getQueryObject(listOf(TopLevelObject(PrivateQuery())))
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

    @Test
    fun `query objects can have directives`() {
        val queries = listOf(TopLevelObject(QueryObject()))
        val result = builder.getQueryObject(queries)
        assertEquals(expected = 1, actual = result.directives.size)
        assertEquals(expected = "simpleDirective", actual = result.directives.first().name)
    }
}
