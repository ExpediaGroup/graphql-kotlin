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

    @Test
    fun `deepname of basic type`() {
        val type = BasicType()
        assertEquals(expected = "BasicType", actual = type.deepName)
    }

    @Test
    fun `deepname of list`() {
        val type = BasicType()
        val list = GraphQLList(type)
        assertEquals(expected = "[BasicType]", actual = list.deepName)
    }

    @Test
    fun `deepname of non null`() {
        val type = BasicType()
        val nonNull = GraphQLNonNull(type)
        assertEquals(expected = "BasicType!", actual = nonNull.deepName)
    }

    @Test
    fun `deepname of non null list of non nulls`() {
        val type = BasicType()
        val complicated = GraphQLNonNull(GraphQLList(GraphQLNonNull(type)))
        assertEquals(expected = "[BasicType!]!", actual = complicated.deepName)
    }
}
