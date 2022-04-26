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
import com.expediagroup.graphql.generator.exceptions.InvalidCustomUnionException
import com.expediagroup.graphql.generator.exceptions.TypeNotSupportedException
import com.expediagroup.graphql.generator.internal.extensions.getCustomTypeAnnotation
import com.expediagroup.graphql.generator.internal.extensions.getKClass
import com.expediagroup.graphql.generator.internal.extensions.getSimpleName
import com.expediagroup.graphql.generator.internal.extensions.getUnionAnnotation
import com.expediagroup.graphql.generator.internal.extensions.isAnnotationUnion
import com.expediagroup.graphql.generator.internal.extensions.isListType
import com.expediagroup.graphql.generator.internal.extensions.qualifiedName
import com.expediagroup.graphql.generator.internal.types.GraphQLKTypeMetadata
import graphql.schema.GraphQLNamedType
import graphql.schema.GraphQLType
import graphql.schema.GraphQLTypeReference
import java.io.Closeable
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.createType
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.starProjectedType

internal class TypesCache(private val supportedPackages: List<String>) : Closeable {

    private val cache: MutableMap<String, KGraphQLType> = mutableMapOf()
    private val typesUnderConstruction: MutableSet<TypesCacheKey> = mutableSetOf()

    internal fun get(type: KType, typeInfo: GraphQLKTypeMetadata): GraphQLNamedType? {
        val cacheKey = generateCacheKey(type, typeInfo)
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

    private fun generateCacheKey(type: KType, typeInfo: GraphQLKTypeMetadata): TypesCacheKey {
        if (type.getKClass().isListType(typeInfo.isDirective)) {
            return TypesCacheKey(type, typeInfo.inputType, isDirective = typeInfo.isDirective)
        }

        val customTypeAnnotation = typeInfo.fieldAnnotations.getCustomTypeAnnotation()
        if (customTypeAnnotation != null) {
            return TypesCacheKey(type, typeInfo.inputType, customTypeAnnotation.typeName)
        }

        val unionAnnotation = typeInfo.fieldAnnotations.getUnionAnnotation()
        if (unionAnnotation != null) {
            if (type.getKClass().isAnnotationUnion(typeInfo.fieldAnnotations)) {
                return TypesCacheKey(Any::class.createType(), typeInfo.inputType, getCustomUnionNameKey(unionAnnotation))
            } else {
                throw InvalidCustomUnionException(type)
            }
        }

        return TypesCacheKey(type, typeInfo.inputType)
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
                kClass.isListType(cacheKey.isDirective) -> null
                kClass.isSubclassOf(Enum::class) -> kClass.getSimpleName()
                isTypeNotSupported(type) -> throw TypeNotSupportedException(type, supportedPackages)
                else -> type.getSimpleName(cacheKey.inputType)
            }
        }
    }

    private fun isTypeNotSupported(type: KType): Boolean = supportedPackages.none { type.qualifiedName.startsWith(it) }

    internal fun buildIfNotUnderConstruction(kClass: KClass<*>, typeInfo: GraphQLKTypeMetadata, build: (KClass<*>) -> GraphQLType): GraphQLType {
        if (kClass.isListType(typeInfo.isDirective)) {
            return build(kClass)
        }

        val cacheKey = generateCacheKey(kClass.starProjectedType, typeInfo)
        val cachedType = get(cacheKey)
        return when {
            cachedType != null -> cachedType
            typesUnderConstruction.contains(cacheKey) -> GraphQLTypeReference.typeRef(kClass.getSimpleName(typeInfo.inputType))
            else -> {
                typesUnderConstruction.add(cacheKey)
                val newType = build(kClass)
                if (newType !is GraphQLTypeReference && newType is GraphQLNamedType) {
                    val cacheKClass = if (kClass.isAnnotationUnion(typeInfo.fieldAnnotations)) Any::class else kClass
                    put(cacheKey, KGraphQLType(cacheKClass, newType))
                }
                typesUnderConstruction.remove(cacheKey)
                newType
            }
        }
    }
}
