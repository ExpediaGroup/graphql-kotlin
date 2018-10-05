package com.expedia.graphql.schema.generator

import com.expedia.graphql.schema.models.KGraphQLType
import graphql.schema.GraphQLType
import org.junit.Test
import kotlin.reflect.full.starProjectedType
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class TypesCacheTest {

    data class MyType(val id: Int = 0)

    private val graphQLType: GraphQLType = GraphQLType { "MyType" }
    private val secondGraphQLType: GraphQLType = GraphQLType { "MySecondType" }

    @Test
    fun `basic get and put with non input type`() {
        val cache = TypesCache("com.expedia.graphql")
        val cacheKey = TypesCacheKey(MyType::class.starProjectedType, false)
        val cacheValue = KGraphQLType(MyType::class, graphQLType)

        assertNull(cache.get(cacheKey))

        cache.put(cacheKey, cacheValue)

        assertNotNull(cache.get(cacheKey))
    }

    @Test
    fun `basic get and put with input type`() {
        val cache = TypesCache("com.expedia.graphql")
        val cacheKey = TypesCacheKey(MyType::class.starProjectedType, true)
        val cacheValue = KGraphQLType(MyType::class, graphQLType)

        assertNull(cache.get(cacheKey))

        cache.put(cacheKey, cacheValue)

        assertNotNull(cache.get(cacheKey))
    }

    @Test
    fun `verify doesNotContainGraphQLType()`() {
        val cache = TypesCache("com.expedia.graphql")
        val cacheKey = TypesCacheKey(MyType::class.starProjectedType, true)
        val cacheValue = KGraphQLType(MyType::class, graphQLType)

        cache.put(cacheKey, cacheValue)

        assertFalse(cache.doesNotContainGraphQLType(graphQLType))
        assertTrue(cache.doesNotContainGraphQLType(secondGraphQLType))
    }
}
