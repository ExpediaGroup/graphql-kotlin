package com.expedia.graphql.generator.extensions

import com.expedia.graphql.exceptions.NestingNonNullTypeException
import graphql.schema.GraphQLNonNull
import graphql.schema.GraphQLType
import graphql.schema.GraphQLTypeVisitor
import graphql.util.TraversalControl
import graphql.util.TraverserContext
import io.mockk.every
import io.mockk.mockk
import kotlin.reflect.KType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class GraphQLTypeExtensionsTest {

    private class BasicType : GraphQLType {
        override fun getName() = "BasicType"

        override fun accept(context: TraverserContext<GraphQLType>, visitor: GraphQLTypeVisitor): TraversalControl = context.thisNode().accept(context, visitor)
    }

    private val basicType = BasicType()

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
}
