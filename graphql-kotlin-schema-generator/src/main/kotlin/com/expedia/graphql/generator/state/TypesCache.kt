package com.expedia.graphql.generator.state

import com.expedia.graphql.exceptions.ConflictingTypesException
import com.expedia.graphql.exceptions.TypeNotSupportedException
import com.expedia.graphql.generator.extensions.getKClass
import com.expedia.graphql.generator.extensions.getSimpleName
import com.expedia.graphql.generator.extensions.isListType
import com.expedia.graphql.generator.extensions.qualifiedName
import graphql.schema.GraphQLType
import graphql.schema.GraphQLTypeReference
import kotlin.reflect.KClass
import kotlin.reflect.KType
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
            typeUnderConstruction.remove(kGraphQLType.kClass)
            return cache.put(cacheKey, kGraphQLType)
        }

        return null
    }

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

    private fun putTypeUnderConstruction(kClass: KClass<*>) = typeUnderConstruction.add(kClass)

    fun removeTypeUnderConstruction(kClass: KClass<*>) = typeUnderConstruction.remove(kClass)

    fun buildIfNotUnderConstruction(kClass: KClass<*>, build: (KClass<*>) -> GraphQLType): GraphQLType {
        return if (typeUnderConstruction.contains(kClass)) {
            GraphQLTypeReference.typeRef(kClass.getSimpleName())
        } else {
            putTypeUnderConstruction(kClass)
            build(kClass)
        }
    }
}
