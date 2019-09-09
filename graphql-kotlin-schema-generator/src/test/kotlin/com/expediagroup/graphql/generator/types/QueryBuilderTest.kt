/*
 * Copyright 2019 Expedia Group, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.expediagroup.graphql.generator.types

import com.expediagroup.graphql.TopLevelObject
import com.expediagroup.graphql.annotations.GraphQLDescription
import com.expediagroup.graphql.annotations.GraphQLIgnore
import com.expediagroup.graphql.exceptions.InvalidQueryTypeException
import com.expediagroup.graphql.generator.extensions.isTrue
import com.expediagroup.graphql.hooks.SchemaGeneratorHooks
import com.expediagroup.graphql.test.utils.SimpleDirective
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
