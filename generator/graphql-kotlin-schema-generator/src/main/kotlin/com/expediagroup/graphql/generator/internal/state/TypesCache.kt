/*
 * Copyright 2021 Expedia, Inc
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
import com.expediagroup.graphql.generator.exceptions.ConflictingTypesException
import com.expediagroup.graphql.generator.exceptions.TypeNotSupportedException
import com.expediagroup.graphql.generator.internal.extensions.getKClass
import com.expediagroup.graphql.generator.internal.extensions.getSimpleName
import com.expediagroup.graphql.generator.internal.extensions.getUnionAnnotation
import com.expediagroup.graphql.generator.internal.extensions.isListType
import com.expediagroup.graphql.generator.internal.extensions.qualifiedName
import graphql.schema.GraphQLNamedType
import graphql.schema.GraphQLType
import graphql.schema.GraphQLTypeReference
import java.io.Closeable
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.starProjectedType

internal class TypesCache(private val supportedPackages: List<String>) : Closeable {

    private val cache: MutableMap<String, KGraphQLType> = mutableMapOf()
    private val typesUnderConstruction: MutableSet<TypesCacheKey> = mutableSetOf()

    internal fun get(type: KType, inputType: Boolean, annotations: List<Annotation>): GraphQLNamedType? {
        val cacheKey = getCacheKey(type, inputType, annotations)
        return get(cacheKey)
    }

    @Throws(ConflictingTypesException::class)
    internal fun get(cacheKey: TypesCacheKey): GraphQLNamedType? {
        val cacheKeyString = getCacheKeyString(cacheKey) ?: return null
        val cachedType = cache[cacheKeyString]

        if (cachedType != null) {
            val kClass = cacheKey.type.getKClass()
            val isSameNameButNotSameClass = cachedType.kClass != kClass
            when {
                isSameNameButNotSameClass -> throw ConflictingTypesException(cachedType.kClass, kClass)
                else -> return cachedType.graphQLType
            }
        }

        return null
    }

    internal fun put(key: TypesCacheKey, kGraphQLType: KGraphQLType): KGraphQLType? {
        val cacheKey = getCacheKeyString(key)

        if (cacheKey != null) {
            cache[cacheKey] = kGraphQLType
            return kGraphQLType
        }

        return null
    }

    private fun getCacheKey(type: KType, inputType: Boolean, annotations: List<Annotation> = emptyList()): TypesCacheKey {
        val unionAnnotation = annotations.getUnionAnnotation()

        return if (unionAnnotation != null) {
            TypesCacheKey(type = type, inputType = inputType, name = getCustomUnionNameKey(unionAnnotation))
        } else {
            TypesCacheKey(type = type, inputType = inputType)
        }
    }

    private fun getCustomUnionNameKey(union: GraphQLUnion): String {
        return union.name + union.possibleTypes.joinToString(prefix = "[", postfix = "]", separator = ",") { it.getSimpleName() }
    }

    /**
     * Clear the cache of all saved values
     */
    override fun close() {
        cache.clear()
        typesUnderConstruction.clear()
    }

    internal fun doesNotContainGraphQLType(graphQLType: GraphQLNamedType) =
        cache.none { (_, v) -> v.graphQLType.name == graphQLType.name }

    /**
     * We do not want to cache list types since it is just a simple wrapper.
     * Enums do not have a different name for input and output.
     */
    private fun getCacheKeyString(cacheKey: TypesCacheKey): String? {
        return if (cacheKey.name != null) {
            cacheKey.name
        } else {
            val type = cacheKey.type
            val kClass = type.getKClass()

            when {
                kClass.isListType() -> null
                kClass.isSubclassOf(Enum::class) -> kClass.getSimpleName()
                isTypeNotSupported(type) -> throw TypeNotSupportedException(type, supportedPackages)
                else -> type.getSimpleName(cacheKey.inputType)
            }
        }
    }

    private fun isTypeNotSupported(type: KType): Boolean = supportedPackages.none { type.qualifiedName.startsWith(it) }

    internal fun buildIfNotUnderConstruction(kClass: KClass<*>, inputType: Boolean, annotations: List<Annotation>, build: (KClass<*>) -> GraphQLType): GraphQLType {
        if (kClass.isListType()) {
            return build(kClass)
        }

        val cacheKey = getCacheKey(kClass.starProjectedType, inputType, annotations)
        val cachedType = get(cacheKey)
        return when {
            cachedType != null -> cachedType
            typesUnderConstruction.contains(cacheKey) -> GraphQLTypeReference.typeRef(kClass.getSimpleName(inputType))
            else -> {
                typesUnderConstruction.add(cacheKey)
                val newType = build(kClass)
                if (newType !is GraphQLTypeReference && newType is GraphQLNamedType) {
                    put(cacheKey, KGraphQLType(kClass, newType))
                }
                typesUnderConstruction.remove(cacheKey)
                newType
            }
        }
    }
}
