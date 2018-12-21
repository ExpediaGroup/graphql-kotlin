package com.expedia.graphql.generator.state

import graphql.schema.GraphQLType
import graphql.schema.GraphQLTypeVisitor
import graphql.util.TraversalControl
import graphql.util.TraverserContext
import org.junit.jupiter.api.Test
import kotlin.reflect.full.starProjectedType
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class TypesCacheTest {

    internal data class MyType(val id: Int = 0)

    private val graphQLType: GraphQLType = object : GraphQLType {
        override fun getName(): String = "MyType"

        override fun accept(context: TraverserContext<GraphQLType>, visitor: GraphQLTypeVisitor): TraversalControl = context.thisNode().accept(context, visitor)
    }

    private val secondGraphQLType: GraphQLType = object : GraphQLType {
        override fun getName(): String = "MySecondType"

        override fun accept(context: TraverserContext<GraphQLType>, visitor: GraphQLTypeVisitor): TraversalControl = context.thisNode().accept(context, visitor)
    }

    @Test
    fun `basic get and put with non input type`() {
        val cache = TypesCache(listOf("com.expedia.graphql"))
        val cacheKey = TypesCacheKey(MyType::class.starProjectedType, false)
        val cacheValue = KGraphQLType(MyType::class, graphQLType)

        assertNull(cache.get(cacheKey))

        cache.put(cacheKey, cacheValue)

        val cacheHit = cache.get(cacheKey)
        assertNotNull(cacheHit)
        assertEquals(expected = cacheValue.graphQLType, actual = cacheHit)
    }

    @Test
    fun `basic get and put with input type`() {
        val cache = TypesCache(listOf("com.expedia.graphql"))
        val cacheKey = TypesCacheKey(MyType::class.starProjectedType, true)
        val cacheValue = KGraphQLType(MyType::class, graphQLType)

        assertNull(cache.get(cacheKey))

        cache.put(cacheKey, cacheValue)

        assertNotNull(cache.get(cacheKey))
    }

    @Test
    fun `verify doesNotContainGraphQLType()`() {
        val cache = TypesCache(listOf("com.expedia.graphql"))
        val cacheKey = TypesCacheKey(MyType::class.starProjectedType, true)
        val cacheValue = KGraphQLType(MyType::class, graphQLType)

        cache.put(cacheKey, cacheValue)

        assertFalse(cache.doesNotContainGraphQLType(graphQLType))
        assertTrue(cache.doesNotContainGraphQLType(secondGraphQLType))
    }
}
