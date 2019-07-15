package com.expedia.graphql.extensions

import graphql.schema.GraphQLList
import graphql.schema.GraphQLNonNull
import graphql.schema.GraphQLType
import graphql.schema.GraphQLTypeVisitor
import graphql.util.TraversalControl
import graphql.util.TraverserContext
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class DeepNameKtTest {

    private class BasicType : GraphQLType {
        override fun getName() = "BasicType"

        override fun accept(context: TraverserContext<GraphQLType>, visitor: GraphQLTypeVisitor): TraversalControl =
            context.thisNode().accept(context, visitor)
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
}
