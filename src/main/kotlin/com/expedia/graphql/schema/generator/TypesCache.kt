@file:Suppress("TooManyFunctions")
package com.expedia.graphql.schema.generator

import com.expedia.graphql.schema.exceptions.ConflictingTypesException
import com.expedia.graphql.schema.exceptions.CouldNotGetJvmNameOfKTypeException
import com.expedia.graphql.schema.exceptions.CouldNotGetNameOfEnumException
import com.expedia.graphql.schema.exceptions.TypeNotSupportedException
import com.expedia.graphql.schema.models.KGraphQLType
import graphql.schema.GraphQLType
import graphql.schema.GraphQLTypeReference
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.jvm.jvmErasure

internal data class TypesCacheKey(val type: KType, val inputType: Boolean)

internal class TypesCache(private val supportedPackages: List<String>) {

    private val cache: MutableMap<String, KGraphQLType> = mutableMapOf()
    private val typeUnderConstruction: MutableSet<KClass<*>> = mutableSetOf()

    fun get(cacheKey: TypesCacheKey): GraphQLType? {
        val cacheKeyString = getCacheKeyString(cacheKey)
        val cachedType = cache[cacheKeyString]

        if (cachedType != null) {
            val kClass = getKClassFromKType(cacheKey.type)
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

    @Throws(CouldNotGetNameOfEnumException::class)
    private fun getCacheKeyString(cacheKey: TypesCacheKey): String {
        val kClass = getKClassFromKType(cacheKey.type)

        if (kClass.isSubclassOf(Enum::class)) {
            return kClass.simpleName ?: throw CouldNotGetNameOfEnumException(kClass)
        }

        val cacheKeyFromTypeName = when {
            kClass.isSubclassOf(List::class) -> "List<${getJvmErasureNameFromList(cacheKey.type)}>"
            kClass.java.isArray -> "Array<${getJvmErasureNameFromList(cacheKey.type)}>"
            else -> getCacheTypeName(cacheKey.type)
        }

        return "$cacheKeyFromTypeName:${cacheKey.inputType}"
    }

    @Suppress("Detekt.UnsafeCast")
    private fun getKClassFromKType(kType: KType) = kType.classifier as KClass<*>

    private fun getCacheTypeName(kType: KType): String {
        throwIfTypeIsNotSupported(kType)
        return getJvmErasureName(kType)
    }

    private fun getJvmErasureNameFromList(type: KType): String =
        getJvmErasureName(type.arguments.first().type)

    @Throws(CouldNotGetJvmNameOfKTypeException::class)
    private fun getJvmErasureName(kType: KType?): String =
        kType?.jvmErasure?.simpleName ?: throw CouldNotGetJvmNameOfKTypeException(kType)

    @Throws(TypeNotSupportedException::class)
    private fun throwIfTypeIsNotSupported(type: KType) {
        val qualifiedName = type.jvmErasure.qualifiedName ?: ""

        val comesFromSupportedPackageName = supportedPackages.any {
            qualifiedName.startsWith(it)
        }

        if (!comesFromSupportedPackageName) {
            throw TypeNotSupportedException(qualifiedName, supportedPackages)
        }
    }

    private fun putTypeUnderConstruction(kClass: KClass<*>) = typeUnderConstruction.add(kClass)

    fun removeTypeUnderConstruction(kClass: KClass<*>) = typeUnderConstruction.remove(kClass)

    fun buildIfNotUnderConstruction(kClass: KClass<*>, build: (KClass<*>) -> GraphQLType): GraphQLType {
        return if (typeUnderConstruction.contains(kClass)) {
            GraphQLTypeReference.typeRef(kClass.simpleName)
        } else {
            putTypeUnderConstruction(kClass)
            build(kClass)
        }
    }
}
