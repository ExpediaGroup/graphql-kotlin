package com.expedia.graphql.generator.state

import com.expedia.graphql.exceptions.ConflictingTypesException
import com.expedia.graphql.exceptions.TypeNotSupportedException
import com.expedia.graphql.generator.extensions.getKClass
import com.expedia.graphql.generator.extensions.getSimpleName
import com.expedia.graphql.generator.extensions.getWrappedName
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
        val cacheKeyString = getCacheKeyString(cacheKey)
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
        typeUnderConstruction.remove(kGraphQLType.kClass)
        return cache.put(getCacheKeyString(key), kGraphQLType)
    }

    fun doesNotContainGraphQLType(graphQLType: GraphQLType) =
        cache.none { (_, v) -> v.graphQLType.name == graphQLType.name }

    fun doesNotContain(kClass: KClass<*>): Boolean = cache.none { (_, ktype) -> ktype.kClass == kClass }

    private fun getCacheKeyString(cacheKey: TypesCacheKey): String {
        val kClass = cacheKey.type.getKClass()

        if (kClass.isSubclassOf(Enum::class)) {
            return kClass.getSimpleName()
        }

        if (isTypeNotSupported(cacheKey.type)) {
            throw TypeNotSupportedException(cacheKey.type, supportedPackages)
        }

        return cacheKey.type.getWrappedName(cacheKey.inputType)
    }

    private fun isTypeNotSupported(type: KType): Boolean {

        if (type.getKClass().isListType()) {
            return false
        }

        return supportedPackages.none {
            type.qualifiedName.startsWith(it)
        }
    }

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
