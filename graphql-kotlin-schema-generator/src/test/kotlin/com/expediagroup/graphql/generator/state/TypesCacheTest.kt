/*
 * Copyright 2019 Expedia, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.expediagroup.graphql.generator.state

import graphql.schema.GraphQLType
import graphql.schema.GraphQLTypeVisitor
import graphql.util.TraversalControl
import graphql.util.TraverserContext
import org.junit.jupiter.api.Test
import kotlin.reflect.full.findParameterByName
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

    internal class MyClass {
        fun listFun(list: List<String>) = list.joinToString(separator = ",") { it }

        fun objectListFun(list: List<MyType>) = list.map { it.id.toString() }.joinToString(separator = ",") { it }

        fun arrayFun(array: Array<String>) = array.joinToString(separator = ",") { it }

        fun primitiveArrayFun(intArray: IntArray) = intArray.joinToString(separator = ",") { it.toString() }
    }

    @Test
    fun `basic get and put with non input type`() {
        val cache = TypesCache(listOf("com.expediagroup.graphql"))
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
        val cache = TypesCache(listOf("com.expediagroup.graphql"))
        val cacheKey = TypesCacheKey(MyType::class.starProjectedType, true)
        val cacheValue = KGraphQLType(MyType::class, graphQLType)

        assertNull(cache.get(cacheKey))

        cache.put(cacheKey, cacheValue)

        assertNotNull(cache.get(cacheKey))
    }

    @Test
    fun `list types are not cached`() {
        val cache = TypesCache(listOf("com.expediagroup.graphql"))

        val type = MyClass::listFun.findParameterByName("list")?.type

        assertNotNull(type)

        val cacheKey = TypesCacheKey(type, false)
        val cacheValue = KGraphQLType(MyType::class, graphQLType)

        assertNull(cache.get(cacheKey))
        assertNull(cache.put(cacheKey, cacheValue))
    }

    @Test
    fun `list of objects are not cached`() {
        val cache = TypesCache(listOf("com.expediagroup.graphql"))

        val type = MyClass::objectListFun.findParameterByName("list")?.type

        assertNotNull(type)

        val cacheKey = TypesCacheKey(type, false)
        val cacheValue = KGraphQLType(MyType::class, graphQLType)

        assertNull(cache.get(cacheKey))
        assertNull(cache.put(cacheKey, cacheValue))
    }

    @Test
    fun `primitive array types are not cached`() {
        val cache = TypesCache(listOf("com.expediagroup.graphql"))

        val type = MyClass::primitiveArrayFun.findParameterByName("intArray")?.type

        assertNotNull(type)

        val cacheKey = TypesCacheKey(type, false)
        val cacheValue = KGraphQLType(MyType::class, graphQLType)

        assertNull(cache.get(cacheKey))
        assertNull(cache.put(cacheKey, cacheValue))
    }

    @Test
    fun `array types are not cached`() {
        val cache = TypesCache(listOf("com.expediagroup.graphql"))

        val type = MyClass::arrayFun.findParameterByName("array")?.type

        assertNotNull(type)

        val cacheKey = TypesCacheKey(type, false)
        val cacheValue = KGraphQLType(MyType::class, graphQLType)

        assertNull(cache.get(cacheKey))
        assertNull(cache.put(cacheKey, cacheValue))
    }

    @Test
    fun `verify doesNotContainGraphQLType()`() {
        val cache = TypesCache(listOf("com.expediagroup.graphql"))
        val cacheKey = TypesCacheKey(MyType::class.starProjectedType, true)
        val cacheValue = KGraphQLType(MyType::class, graphQLType)

        cache.put(cacheKey, cacheValue)

        assertFalse(cache.doesNotContainGraphQLType(graphQLType))
        assertTrue(cache.doesNotContainGraphQLType(secondGraphQLType))
    }
}
