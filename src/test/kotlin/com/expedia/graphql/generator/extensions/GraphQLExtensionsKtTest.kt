package com.expedia.graphql.generator.extensions

import com.expedia.graphql.exceptions.CouldNotCastGraphQLType
import graphql.schema.GraphQLInterfaceType
import graphql.schema.GraphQLObjectType
import graphql.schema.GraphQLType
import io.mockk.mockk
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

internal class GraphQLExtensionsKtTest {

    @Test
    fun `safeCast valid type passes`() {
        val type: GraphQLType = GraphQLInterfaceType("name", "description", emptyList(), mockk())

        val castedType = type.safeCast<GraphQLInterfaceType>()
        assertEquals("name", castedType.name)
    }

    @Test
    fun `safeCast valid type to the wrong type fails`() {
        val type: GraphQLType = GraphQLObjectType("name", "description", emptyList(), mockk())

        assertFailsWith(CouldNotCastGraphQLType::class) {
            val result = type.safeCast<GraphQLInterfaceType>()
            result.description
        }
    }
}
