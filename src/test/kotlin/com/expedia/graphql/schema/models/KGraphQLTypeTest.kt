package com.expedia.graphql.schema.models

import graphql.schema.GraphQLType
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class KGraphQLTypeTest {

    private data class MyType(val id: Int = 0)
    private val graphQLType = GraphQLType { "MyType" }

    @Test
    fun `properties are set`() {
        val kGraphQLType = KGraphQLType(MyType::class, graphQLType)
        assertEquals(expected = MyType::class, actual = kGraphQLType.kClass)
        assertEquals(expected = graphQLType, actual = kGraphQLType.graphQLType)
    }
}
