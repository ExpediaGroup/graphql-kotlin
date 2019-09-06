package com.expediagroup.graphql.federation.types

import graphql.schema.GraphQLTypeUtil
import graphql.schema.GraphQLUnionType
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotNull

internal class EntityTest {

    @Test
    fun `generateEntityFieldDefinition should fail on empty set`() {
        assertFailsWith(graphql.AssertException::class) {
            generateEntityFieldDefinition(emptySet())
        }
    }

    @Test
    fun `generateEntityFieldDefinition should return a valid type on a single set`() {
        val result = generateEntityFieldDefinition(setOf("MyType"))
        assertNotNull(result)
        assertEquals(expected = "_entities", actual = result.name)
        assertFalse(result.description.isNullOrEmpty())
        assertEquals(expected = 1, actual = result.arguments.size)

        val graphQLUnionType = GraphQLTypeUtil.unwrapType(result.type).last() as? GraphQLUnionType

        assertNotNull(graphQLUnionType)
        assertEquals(expected = "_Entity", actual = graphQLUnionType.name)
        assertEquals(expected = 1, actual = graphQLUnionType.types.size)
        assertEquals(expected = "MyType", actual = graphQLUnionType.types.first().name)
    }

    @Test
    fun `generateEntityFieldDefinition should return a valid type on a multiple values`() {
        val result = generateEntityFieldDefinition(setOf("MyType", "MySecondType"))
        val graphQLUnionType = GraphQLTypeUtil.unwrapType(result.type).last() as? GraphQLUnionType

        assertNotNull(graphQLUnionType)
        assertEquals(expected = 2, actual = graphQLUnionType.types.size)
        assertEquals(expected = "MyType", actual = graphQLUnionType.types.first().name)
        assertEquals(expected = "MySecondType", actual = graphQLUnionType.types[1].name)
    }
}
