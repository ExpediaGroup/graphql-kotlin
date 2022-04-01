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

package com.expediagroup.graphql.generator.internal.extensions

import com.expediagroup.graphql.generator.exceptions.CouldNotCastGraphQLSchemaElement
import graphql.schema.GraphQLAppliedDirective
import graphql.schema.GraphQLArgument
import graphql.schema.GraphQLDirectiveContainer
import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLInterfaceType
import graphql.schema.GraphQLNamedType
import graphql.schema.GraphQLNonNull
import graphql.schema.GraphQLObjectType
import graphql.schema.GraphQLType
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import kotlin.reflect.KType
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@Suppress("Detekt.LargeClass")
internal class GraphQLExtensionsTest {

    private val basicType = mockk<GraphQLNamedType> {
        every { name } returns "BasicType"
    }
    private val mockDirective: GraphQLAppliedDirective = mockk()

    @Test
    fun `wrapInNonNull twice returns object`() {
        val nonNull = GraphQLNonNull(basicType)
        val mockKType: KType = mockk()
        assertEquals(nonNull, nonNull.wrapInNonNull(mockKType))
    }

    @Test
    fun `wrapInNonNull with null kotlin type does nothing`() {
        val mockKType: KType = mockk {
            every { isMarkedNullable } returns true
        }

        assertFalse(basicType.wrapInNonNull(mockKType) is GraphQLNonNull)
        assertEquals(expected = basicType, actual = basicType.wrapInNonNull(mockKType))
    }

    @Test
    fun `wrapInNonNull with non-nullable kotlin type wraps`() {
        val mockKType: KType = mockk {
            every { isMarkedNullable } returns false
        }

        assertTrue(basicType.wrapInNonNull(mockKType) is GraphQLNonNull)
    }

    @Test
    fun `GraphQLDirectiveContainer with no directives`() {
        val container: GraphQLDirectiveContainer = mockk {
            every { appliedDirectives } returns emptyList()
        }

        assertTrue(container.getAllAppliedDirectives().isEmpty())
    }

    @Test
    fun `GraphQLDirectiveContainer with one directive`() {
        val container: GraphQLDirectiveContainer = mockk {
            every { appliedDirectives } returns listOf(mockDirective)
        }

        assertEquals(expected = 1, actual = container.getAllAppliedDirectives().size)
    }

    @Test
    fun `GraphQLFieldDefinition with one directive and no arguments`() {
        val container: GraphQLFieldDefinition = mockk {
            every { appliedDirectives } returns listOf(mockDirective)
            every { arguments } returns emptyList()
        }

        assertEquals(expected = 1, actual = container.getAllAppliedDirectives().size)
    }

    @Test
    fun `GraphQLFieldDefinition with one directive and argument with no directives`() {
        val mockArgument: GraphQLArgument = mockk {
            every { appliedDirectives } returns emptyList()
        }

        val container: GraphQLFieldDefinition = mockk {
            every { appliedDirectives } returns listOf(mockDirective)
            every { arguments } returns listOf(mockArgument)
        }

        assertEquals(expected = 1, actual = container.getAllAppliedDirectives().size)
    }

    @Test
    fun `GraphQLFieldDefinition with one directive and argument with two directives`() {
        val mockArgument: GraphQLArgument = mockk {
            every { appliedDirectives } returns listOf(mockDirective, mockDirective)
        }

        val container: GraphQLFieldDefinition = mockk {
            every { appliedDirectives } returns listOf(mockDirective)
            every { arguments } returns listOf(mockArgument)
        }

        assertEquals(expected = 3, actual = container.getAllAppliedDirectives().size)
    }

    @Test
    fun `safeCast with GraphQLType passes`() {
        val type: GraphQLNamedType = mockk()
        every { type.name } returns "foo"

        val castedType = type.safeCast<GraphQLNamedType>()
        assertEquals("foo", castedType.name)
    }

    @Test
    fun `safeCast valid type passes`() {
        val type: GraphQLType = GraphQLInterfaceType.newInterface().name("name").description("description").build()

        val castedType = type.safeCast<GraphQLInterfaceType>()
        assertEquals("name", castedType.name)
    }

    @Test
    fun `safeCast valid type to the wrong type fails`() {
        val type: GraphQLType = GraphQLObjectType.newObject().name("name").description("description").build()

        assertFailsWith(CouldNotCastGraphQLSchemaElement::class) {
            type.safeCast<GraphQLInterfaceType>()
        }
    }
}
