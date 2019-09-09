/*
 * Copyright 2019 Expedia Group
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
import com.expediagroup.graphql.exceptions.InvalidMutationTypeException
import com.expediagroup.graphql.generator.extensions.isTrue
import com.expediagroup.graphql.hooks.SchemaGeneratorHooks
import com.expediagroup.graphql.test.utils.SimpleDirective
import graphql.schema.GraphQLFieldDefinition
import org.junit.jupiter.api.Test
import kotlin.reflect.KFunction
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

@Suppress("Detekt.NestedClassesVisibility")
internal class MutationBuilderTest : TypeTestHelper() {

    internal class SimpleHooks : SchemaGeneratorHooks {
        var calledHook = false
        override fun didGenerateMutationType(function: KFunction<*>, fieldDefinition: GraphQLFieldDefinition): GraphQLFieldDefinition {
            calledHook = true
            return super.didGenerateMutationType(function, fieldDefinition)
        }
    }

    @SimpleDirective
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

    private lateinit var builder: MutationBuilder

    override fun beforeSetup() {
        hooks = SimpleHooks()
    }

    override fun beforeTest() {
        builder = MutationBuilder(generator)
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
        val mutations = listOf(TopLevelObject(NoFunctions()))
        val result = builder.getMutationObject(mutations)
        assertEquals(expected = "TestTopLevelMutation", actual = result?.name)
        assertTrue(result?.fieldDefinitions?.isEmpty().isTrue())
    }

    @Test
    fun `mutation with valid functions`() {
        val mutations = listOf(TopLevelObject(MutationObject()))
        val result = builder.getMutationObject(mutations)
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

    @Test
    fun `mutation objects can have directives`() {
        val mutations = listOf(TopLevelObject(MutationObject()))
        val result = builder.getMutationObject(mutations)
        assertEquals(expected = 1, actual = result?.directives?.size)
        assertEquals(expected = "simpleDirective", actual = result?.directives?.first()?.name)
    }
}
