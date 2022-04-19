/*
 * Copyright 2022 Expedia, Inc
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

package com.expediagroup.graphql.generator.internal.state

import com.expediagroup.graphql.generator.annotations.GraphQLUnion
import com.expediagroup.graphql.generator.exceptions.InvalidCustomUnionException
import com.expediagroup.graphql.generator.internal.extensions.getKClass
import com.expediagroup.graphql.generator.internal.types.GraphQLKTypeMetadata
import graphql.schema.GraphQLNamedType
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import kotlin.reflect.full.createType
import kotlin.reflect.full.findParameterByName
import kotlin.reflect.full.starProjectedType
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class TypesCacheTest {

    data class MyType(val id: Int = 0)

    private val graphQLType: GraphQLNamedType = mockk {
        every { name } returns "MyType"
    }

    private val secondGraphQLType: GraphQLNamedType = mockk {
        every { name } returns "MySecondType"
    }

    private val customUnionGraphQLType: GraphQLNamedType = mockk {
        every { name } returns "CustomUnion"
    }

    private val metaUnionGraphQLType: GraphQLNamedType = mockk {
        every { name } returns "MetaUnion"
    }

    class MyClass {
        fun listFun(list: List<String>) = list.joinToString(separator = ",") { it }

        fun objectListFun(list: List<MyType>) = list.map { it.id.toString() }.joinToString(separator = ",") { it }

        @GraphQLUnion(name = "CustomUnion", possibleTypes = [MyType::class, Int::class])
        fun customUnion(): Any = MyType(1)

        @MetaUnion
        fun metaUnion(): Any = MyType(1)

        @GraphQLUnion(name = "InvalidUnion", possibleTypes = [MyType::class, Int::class])
        fun invalidUnion(): String = "foobar"

        @MetaUnion
        fun invalidMetaUnion(): String = "foobar"
    }

    @GraphQLUnion(name = "MetaUnion", possibleTypes = [MyType::class, Int::class])
    annotation class MetaUnion

    @Test
    fun `basic get and put with non input type`() {
        val cache = TypesCache(listOf("com.expediagroup.graphql.generator"))
        val cacheKey = TypesCacheKey(MyType::class.starProjectedType)
        val cacheValue = KGraphQLType(MyType::class, graphQLType)

        assertNull(cache.get(cacheKey))

        cache.put(cacheKey, cacheValue)

        val cacheHit = cache.get(cacheKey)
        assertNotNull(cacheHit)
        assertEquals(expected = cacheValue.graphQLType, actual = cacheHit)
    }

    @Test
    fun `basic get and put with input type`() {
        val cache = TypesCache(listOf("com.expediagroup.graphql.generator"))
        val cacheKey = TypesCacheKey(MyType::class.starProjectedType)
        val cacheValue = KGraphQLType(MyType::class, graphQLType)

        assertNull(cache.get(cacheKey))

        cache.put(cacheKey, cacheValue)

        assertNotNull(cache.get(cacheKey))
    }

    @Test
    fun `list types are not cached`() {
        val cache = TypesCache(listOf("com.expediagroup.graphql.generator"))

        val type = MyClass::listFun.findParameterByName("list")?.type

        assertNotNull(type)

        val cacheKey = TypesCacheKey(type)
        val cacheValue = KGraphQLType(MyType::class, graphQLType)

        assertNull(cache.get(cacheKey))
        assertNull(cache.put(cacheKey, cacheValue))
    }

    @Test
    fun `list of objects are not cached`() {
        val cache = TypesCache(listOf("com.expediagroup.graphql.generator"))

        val type = MyClass::objectListFun.findParameterByName("list")?.type

        assertNotNull(type)

        val cacheKey = TypesCacheKey(type)
        val cacheValue = KGraphQLType(MyType::class, graphQLType)

        assertNull(cache.get(cacheKey))
        assertNull(cache.put(cacheKey, cacheValue))
    }

    @Test
    fun `custom unions are cached by special name`() {
        val cache = TypesCache(listOf("com.expediagroup.graphql.generator"))
        val type = MyClass::customUnion.returnType
        val annotations = MyClass::customUnion.annotations
        val typeInfo = GraphQLKTypeMetadata(inputType = false, fieldAnnotations = annotations)

        val cacheKey = TypesCacheKey(type = type, typeInfo.inputType, name = "CustomUnion[MyType,Int]")
        val cacheValue = KGraphQLType(type.getKClass(), customUnionGraphQLType)

        assertNull(cache.get(cacheKey))
        assertNull(cache.get(type = type, typeInfo))
        assertNotNull(cache.put(cacheKey, cacheValue))
        assertNotNull(cache.get(type = type, typeInfo))
    }

    @Test
    fun `invalid custom unions throw an exception`() {
        val cache = TypesCache(listOf("com.expediagroup.graphql.generator"))
        val type = MyClass::invalidUnion.returnType
        val annotations = MyClass::invalidUnion.annotations
        val typeInfo = GraphQLKTypeMetadata(fieldAnnotations = annotations)

        val cacheKey = TypesCacheKey(type = type, inputType = typeInfo.inputType, name = "InvalidUnion[MyType,Int]")

        assertNull(cache.get(cacheKey))
        assertFailsWith(InvalidCustomUnionException::class) {
            cache.get(type = type, typeInfo)
        }
    }

    @Test
    fun `meta unions are cached by special name`() {
        val cache = TypesCache(listOf("com.expediagroup.graphql.generator"))
        val type = MyClass::metaUnion.returnType
        val annotations = MyClass::metaUnion.annotations
        val typeInfo = GraphQLKTypeMetadata(inputType = false, fieldAnnotations = annotations)

        val cacheKey = TypesCacheKey(type = type, typeInfo.inputType, name = "MetaUnion[MyType,Int]")
        val cacheValue = KGraphQLType(type.getKClass(), metaUnionGraphQLType)

        assertNull(cache.get(cacheKey))
        assertNull(cache.get(type = type, typeInfo))
        assertNotNull(cache.put(cacheKey, cacheValue))
        assertNotNull(cache.get(type = type, typeInfo))
    }

    @Test
    fun `invalid meta unions throw an exception`() {
        val cache = TypesCache(listOf("com.expediagroup.graphql.generator"))
        val type = MyClass::invalidMetaUnion.returnType
        val annotations = MyClass::invalidMetaUnion.annotations
        val typeInfo = GraphQLKTypeMetadata(fieldAnnotations = annotations)

        val cacheKey = TypesCacheKey(type = type, inputType = typeInfo.inputType, name = "InvalidMetaUnion[MyType,Int]")

        assertNull(cache.get(cacheKey))
        assertFailsWith(InvalidCustomUnionException::class) {
            cache.get(type = type, typeInfo)
        }
    }

    @Test
    fun `verify doesNotContainGraphQLType()`() {
        val cache = TypesCache(listOf("com.expediagroup.graphql.generator"))
        val cacheKey = TypesCacheKey(MyType::class.starProjectedType, inputType = true)
        val cacheValue = KGraphQLType(MyType::class, graphQLType)

        cache.put(cacheKey, cacheValue)

        assertFalse(cache.doesNotContainGraphQLType(graphQLType))
        assertTrue(cache.doesNotContainGraphQLType(secondGraphQLType))
    }

    @Test
    fun `buildIfNotUnderConstruction returns the cache type if already set`() {
        val cache = TypesCache(listOf("com.expediagroup.graphql.generator"))
        val cacheKey = TypesCacheKey(MyType::class.starProjectedType)
        val cacheValue = KGraphQLType(MyType::class, graphQLType)

        assertNull(cache.get(cacheKey))
        cache.put(cacheKey, cacheValue)

        val cacheHit = cache.buildIfNotUnderConstruction(MyType::class, GraphQLKTypeMetadata()) {
            assertTrue(false, "Should never reach here")
            cacheValue.graphQLType
        }
        assertNotNull(cacheHit)
        assertEquals(expected = cacheValue.graphQLType, actual = cacheHit)
    }

    @Test
    fun `buildIfNotUnderConstruction only runs once`() {
        val cache = TypesCache(listOf("com.expediagroup.graphql.generator"))
        val cacheKey = TypesCacheKey(MyType::class.starProjectedType)
        val cacheValue = KGraphQLType(MyType::class, graphQLType)
        assertNull(cache.get(cacheKey))

        val cacheHit = cache.buildIfNotUnderConstruction(MyType::class, GraphQLKTypeMetadata()) {
            cache.buildIfNotUnderConstruction(MyType::class, GraphQLKTypeMetadata()) {
                assertTrue(false, "Should never reach here")
                cacheValue.graphQLType
            }
            cacheValue.graphQLType
        }

        assertNotNull(cacheHit)
        assertEquals(expected = cacheValue.graphQLType, actual = cacheHit)
    }

    @Test
    fun `buildIfNotUnderConstruction puts custom union into the cache`() {
        val cache = TypesCache(listOf("com.expediagroup.graphql.generator"))
        val annotations = MyClass::customUnion.annotations
        val typeInfo = GraphQLKTypeMetadata(inputType = false, fieldAnnotations = annotations)

        val cacheKey = TypesCacheKey(type = Any::class.createType(), typeInfo.inputType, name = "CustomUnion[MyType,Int]")

        cache.buildIfNotUnderConstruction(MyClass::customUnion.returnType.getKClass(), typeInfo) {
            customUnionGraphQLType
        }

        val cacheValue = cache.get(cacheKey)
        assertNotNull(cacheValue)
    }
}
