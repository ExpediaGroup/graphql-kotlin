package com.expedia.graphql.schema.extensions

import graphql.schema.GraphQLList
import graphql.schema.GraphQLNonNull
import graphql.schema.GraphQLType
import kotlin.test.Test
import kotlin.test.assertEquals

internal class GraphQLTypeExtensionsTest {

    private class BasicType : GraphQLType {
        override fun getName() = "BasicType"
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
