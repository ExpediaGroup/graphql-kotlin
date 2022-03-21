/*
 * Copyright 2022 Expedia, Inc
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

package com.expediagroup.graphql.generator.internal.types

import com.expediagroup.graphql.generator.TopLevelObject
import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.generator.annotations.GraphQLIgnore
import com.expediagroup.graphql.generator.exceptions.ConflictingFieldsException
import com.expediagroup.graphql.generator.exceptions.EmptyQueryTypeException
import com.expediagroup.graphql.generator.exceptions.InvalidQueryTypeException
import com.expediagroup.graphql.generator.hooks.SchemaGeneratorHooks
import com.expediagroup.graphql.generator.internal.extensions.isTrue
import com.expediagroup.graphql.generator.test.utils.SimpleDirective
import graphql.schema.GraphQLFieldDefinition
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@Suppress("Detekt.NestedClassesVisibility")
internal class GenerateQueryTest : TypeTestHelper() {

    internal class SimpleHooks : SchemaGeneratorHooks {
        var calledHook = false
        override fun didGenerateQueryField(kClass: KClass<*>, function: KFunction<*>, fieldDefinition: GraphQLFieldDefinition): GraphQLFieldDefinition {
            calledHook = true
            return super.didGenerateQueryField(kClass, function, fieldDefinition)
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

    class QueryObjectWithSameFun {
        fun query(value: Int) = value
    }

    class NoFunctions {
        @GraphQLIgnore
        fun hidden(value: Int) = value
    }

    override fun beforeSetup() {
        hooks = SimpleHooks()
    }

    @Test
    fun `verify builder fails if non public query is specified`() {
        assertFailsWith(exceptionClass = InvalidQueryTypeException::class) {
            generateQueries(generator, listOf(TopLevelObject(PrivateQuery())))
        }
    }

    @Test
    fun `query with no valid functions`() {
        val queries = listOf(TopLevelObject(NoFunctions()))
        assertThrows<EmptyQueryTypeException> {
            generateQueries(generator, queries)
        }
    }

    @Test
    fun `verify builder fails if no queries are specified`() {
        assertThrows<EmptyQueryTypeException> {
            generateQueries(generator, emptyList())
        }
    }

    @Test
    fun `query with valid functions`() {
        val queries = listOf(TopLevelObject(QueryObject()))
        val result = generateQueries(generator, queries)
        assertEquals(expected = "TestTopLevelQuery", actual = result.name)
        assertEquals(expected = 1, actual = result.fieldDefinitions.size)
        assertEquals(expected = "query", actual = result.fieldDefinitions.first().name)
    }

    @Test
    fun `verify hooks are called`() {
        assertFalse((generator.config.hooks as? SimpleHooks)?.calledHook.isTrue())
        generateQueries(generator, listOf(TopLevelObject(QueryObject())))
        assertTrue((generator.config.hooks as? SimpleHooks)?.calledHook.isTrue())
    }

    @Test
    fun `query objects can have directives`() {
        val queries = listOf(TopLevelObject(QueryObject()))
        val result = generateQueries(generator, queries)
        assertEquals(expected = 1, actual = result.appliedDirectives.size)
        assertEquals(expected = "simpleDirective", actual = result.appliedDirectives.first().name)
    }

    @Test
    fun `verify builder fails if plural queries the have same field names`() {
        val queries = listOf(TopLevelObject(QueryObject()), TopLevelObject(QueryObjectWithSameFun()))
        assertThrows<ConflictingFieldsException> {
            generateQueries(generator, queries)
        }
    }
}
