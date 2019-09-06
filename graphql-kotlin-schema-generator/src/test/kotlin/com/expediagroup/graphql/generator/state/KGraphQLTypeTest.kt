package com.expediagroup.graphql.generator.state

import graphql.schema.GraphQLType
import graphql.schema.GraphQLTypeVisitor
import graphql.util.TraversalControl
import graphql.util.TraverserContext
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class KGraphQLTypeTest {

    @Suppress("Detekt.UnusedPrivateClass")
    private data class MyType(val id: Int = 0)
    private val graphQLType = object : GraphQLType {
        override fun getName(): String = "MyType"

        override fun accept(context: TraverserContext<GraphQLType>, visitor: GraphQLTypeVisitor): TraversalControl = context.thisNode().accept(context, visitor)
    }

    @Test
    fun `properties are set`() {
        val kGraphQLType = KGraphQLType(MyType::class, graphQLType)
        assertEquals(expected = MyType::class, actual = kGraphQLType.kClass)
        assertEquals(expected = graphQLType, actual = kGraphQLType.graphQLType)
    }
}
