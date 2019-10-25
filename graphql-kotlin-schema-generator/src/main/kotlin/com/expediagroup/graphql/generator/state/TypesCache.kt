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

import com.expediagroup.graphql.exceptions.ConflictingTypesException
import com.expediagroup.graphql.exceptions.TypeNotSupportedException
import com.expediagroup.graphql.generator.extensions.getKClass
import com.expediagroup.graphql.generator.extensions.getSimpleName
import com.expediagroup.graphql.generator.extensions.isListType
import com.expediagroup.graphql.generator.extensions.qualifiedName
import graphql.schema.GraphQLType
import graphql.schema.GraphQLTypeReference
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.createType
import kotlin.reflect.full.isSubclassOf

internal class TypesCache(private val supportedPackages: List<String>) {

    private val cache: MutableMap<String, KGraphQLType> = mutableMapOf()
    private val typeUnderConstruction: MutableSet<KClass<*>> = mutableSetOf()

    @Throws(ConflictingTypesException::class)
    fun get(cacheKey: TypesCacheKey): GraphQLType? {
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

    fun put(key: TypesCacheKey, kGraphQLType: KGraphQLType): KGraphQLType? {
        val cacheKey = getCacheKeyString(key)

        if (cacheKey != null) {
            cache[cacheKey] = kGraphQLType
            return kGraphQLType
        }

        return null
    }

    /**
     * Clear the map of all saved values
     */
    internal fun clear() = cache.clear()

    fun doesNotContainGraphQLType(graphQLType: GraphQLType) =
        cache.none { (_, v) -> v.graphQLType.name == graphQLType.name }

    fun doesNotContain(kClass: KClass<*>): Boolean = cache.none { (_, ktype) -> ktype.kClass == kClass }

    /**
     * We do not want to cache list types since it is just a simple wrapper.
     * Enums do not have a different name for input and output.
     */
    private fun getCacheKeyString(cacheKey: TypesCacheKey): String? {
        val type = cacheKey.type
        val kClass = type.getKClass()

        return when {
            kClass.isListType() -> null
            kClass.isSubclassOf(Enum::class) -> kClass.getSimpleName()
            isTypeNotSupported(type) -> throw TypeNotSupportedException(type, supportedPackages)
            else -> type.getSimpleName(cacheKey.inputType)
        }
    }

    private fun isTypeNotSupported(type: KType): Boolean = supportedPackages.none { type.qualifiedName.startsWith(it) }

    fun buildIfNotUnderConstruction(kClass: KClass<*>, build: (KClass<*>) -> GraphQLType): GraphQLType {
        val cachedType = cache[kClass.getSimpleName()]
        return when {
            cachedType != null -> cachedType.graphQLType
            typeUnderConstruction.contains(kClass) -> GraphQLTypeReference.typeRef(kClass.getSimpleName())
            else -> {
                typeUnderConstruction.add(kClass)
                val newType = build(kClass)
                val key = TypesCacheKey(kClass.createType(), false)
                put(key, KGraphQLType(kClass, newType))
                typeUnderConstruction.remove(kClass)
                newType
            }
        }
    }
}
