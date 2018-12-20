package com.expedia.graphql.schema.extensions

import com.expedia.graphql.exceptions.NestingNonNullTypeException
import com.expedia.graphql.extensions.deepName
import graphql.schema.GraphQLList
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
    fun `deepname of basic type`() {
        assertEquals(expected = "BasicType", actual = basicType.deepName)
    }

    @Test
    fun `deepname of list`() {
        val list = GraphQLList(basicType)
        assertEquals(expected = "[BasicType]", actual = list.deepName)
    }

    @Test
    fun `deepname of non null`() {
        val nonNull = GraphQLNonNull(basicType)
        assertEquals(expected = "BasicType!", actual = nonNull.deepName)
    }

    @Test
    fun `deepname of non null list of non nulls`() {
        val complicated = GraphQLNonNull(GraphQLList(GraphQLNonNull(basicType)))
        assertEquals(expected = "[BasicType!]!", actual = complicated.deepName)
    }

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
