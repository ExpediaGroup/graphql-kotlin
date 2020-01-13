/*
 * Copyright 2020 Expedia, Inc
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
import com.expediagroup.graphql.exceptions.EmptyMutationTypeException
import com.expediagroup.graphql.exceptions.InvalidMutationTypeException
import com.expediagroup.graphql.generator.extensions.isTrue
import com.expediagroup.graphql.hooks.SchemaGeneratorHooks
import com.expediagroup.graphql.test.utils.SimpleDirective
import graphql.schema.GraphQLFieldDefinition
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.reflect.KClass
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
        override fun didGenerateMutationField(kClass: KClass<*>, function: KFunction<*>, fieldDefinition: GraphQLFieldDefinition): GraphQLFieldDefinition {
            calledHook = true
            return super.didGenerateMutationField(kClass, function, fieldDefinition)
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

    override fun beforeSetup() {
        hooks = SimpleHooks()
    }

    @Test
    fun `empty list`() {
        assertNull(generateMutations(generator, emptyList()))
    }

    @Test
    fun `verify builder fails if non public mutation is specified`() {
        assertFailsWith(exceptionClass = InvalidMutationTypeException::class) {
            generateMutations(generator, listOf(TopLevelObject(PrivateMutation())))
        }
    }

    @Test
    fun `mutation with no valid functions`() {
        val mutations = listOf(TopLevelObject(NoFunctions()))
        assertThrows<EmptyMutationTypeException> {
            generateMutations(generator, mutations)
        }
    }

    @Test
    fun `mutation with valid functions`() {
        val mutations = listOf(TopLevelObject(MutationObject()))
        val result = generateMutations(generator, mutations)
        assertEquals(expected = "TestTopLevelMutation", actual = result?.name)
        assertEquals(expected = 1, actual = result?.fieldDefinitions?.size)
        assertEquals(expected = "mutation", actual = result?.fieldDefinitions?.firstOrNull()?.name)
    }

    @Test
    fun `verify hooks are called`() {
        assertFalse((generator.config.hooks as? SimpleHooks)?.calledHook.isTrue())
        generateMutations(generator, listOf(TopLevelObject(MutationObject())))
        assertTrue((generator.config.hooks as? SimpleHooks)?.calledHook.isTrue())
    }

    @Test
    fun `mutation objects can have directives`() {
        val mutations = listOf(TopLevelObject(MutationObject()))
        val result = generateMutations(generator, mutations)
        assertEquals(expected = 1, actual = result?.directives?.size)
        assertEquals(expected = "simpleDirective", actual = result?.directives?.first()?.name)
    }
}
