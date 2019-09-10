/*
 * Copyright 2019 Expedia, Inc
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

package com.expediagroup.graphql.generator.extensions

import com.expediagroup.graphql.exceptions.CouldNotCastGraphQLType
import com.expediagroup.graphql.exceptions.NestingNonNullTypeException
import graphql.schema.GraphQLArgument
import graphql.schema.GraphQLDirective
import graphql.schema.GraphQLDirectiveContainer
import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLInterfaceType
import graphql.schema.GraphQLNonNull
import graphql.schema.GraphQLObjectType
import graphql.schema.GraphQLType
import graphql.schema.GraphQLTypeVisitor
import graphql.util.TraversalControl
import graphql.util.TraverserContext
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

    private class BasicType : GraphQLType {
        override fun getName() = "BasicType"

        override fun accept(context: TraverserContext<GraphQLType>, visitor: GraphQLTypeVisitor): TraversalControl =
            context.thisNode().accept(context, visitor)
    }

    private val basicType = BasicType()
    private val mockDirective: GraphQLDirective = mockk()

    @Test
    fun `wrapInNonNull twice throws exception`() {
        val nonNull = GraphQLNonNull(basicType)
        val mockKType: KType = mockk()

        assertFailsWith(NestingNonNullTypeException::class) {
            nonNull.wrapInNonNull(mockKType)
        }
    }

    @Test
    fun `wrapInNonNull with null kotlin type does nothing`() {
        val mockKType: KType = mockk()
        every { mockKType.isMarkedNullable } returns true

        assertFalse(basicType.wrapInNonNull(mockKType) is GraphQLNonNull)
        assertEquals(expected = basicType, actual = basicType.wrapInNonNull(mockKType))
    }

    @Test
    fun `wrapInNonNull with non-nullable kotlin type wraps`() {
        val mockKType: KType = mockk()
        every { mockKType.isMarkedNullable } returns false

        assertTrue(basicType.wrapInNonNull(mockKType) is GraphQLNonNull)
    }

    @Test
    fun `GraphQLDirectiveContainer with no directives`() {
        val container: GraphQLDirectiveContainer = mockk()
        every { container.directives } returns emptyList()

        assertTrue(container.getAllDirectives().isEmpty())
    }

    @Test
    fun `GraphQLDirectiveContainer with one directive`() {
        val container: GraphQLDirectiveContainer = mockk()
        every { container.directives } returns listOf(mockDirective)

        assertEquals(expected = 1, actual = container.getAllDirectives().size)
    }

    @Test
    fun `GraphQLFieldDefinition with one directive and no arguments`() {
        val container: GraphQLFieldDefinition = mockk()
        every { container.directives } returns listOf(mockDirective)
        every { container.arguments } returns emptyList()

        assertEquals(expected = 1, actual = container.getAllDirectives().size)
    }

    @Test
    fun `GraphQLFieldDefinition with one directive and argument with no directives`() {
        val mockArgument: GraphQLArgument = mockk()
        every { mockArgument.directives } returns emptyList()

        val container: GraphQLFieldDefinition = mockk()
        every { container.directives } returns listOf(mockDirective)
        every { container.arguments } returns listOf(mockArgument)

        assertEquals(expected = 1, actual = container.getAllDirectives().size)
    }

    @Test
    fun `GraphQLFieldDefinition with one directive and argument with two directives`() {
        val mockArgument: GraphQLArgument = mockk()
        every { mockArgument.directives } returns listOf(mockDirective, mockDirective)

        val container: GraphQLFieldDefinition = mockk()
        every { container.directives } returns listOf(mockDirective)
        every { container.arguments } returns listOf(mockArgument)

        assertEquals(expected = 3, actual = container.getAllDirectives().size)
    }

    @Test
    fun `safeCast with GraphQLType passes`() {
        val type: GraphQLType = mockk()
        every { type.name } returns "foo"

        val castedType = type.safeCast<GraphQLType>()
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

        assertFailsWith(CouldNotCastGraphQLType::class) {
            type.safeCast<GraphQLInterfaceType>()
        }
    }
}
