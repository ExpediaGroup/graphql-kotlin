package com.expedia.graphql.schema.generator

import com.expedia.graphql.exceptions.ConflictingTypesException
import com.expedia.graphql.exceptions.CouldNotGetNameOfEnumException
import com.expedia.graphql.exceptions.TypeNotSupportedException
import com.expedia.graphql.schema.extensions.getJvmErasureName
import com.expedia.graphql.schema.extensions.getKClass
import com.expedia.graphql.schema.extensions.getTypeOfFirstArgument
import com.expedia.graphql.schema.extensions.isArray
import com.expedia.graphql.schema.extensions.isList
import com.expedia.graphql.schema.generator.models.KGraphQLType
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

    @Throws(CouldNotGetNameOfEnumException::class)
    private fun getCacheKeyString(cacheKey: TypesCacheKey): String {
        val kClass = cacheKey.type.getKClass()

        if (kClass.isSubclassOf(Enum::class)) {
            return kClass.simpleName ?: throw CouldNotGetNameOfEnumException(kClass)
        }

        val cacheKeyFromTypeName = when {
            kClass.isList() -> "List<${getJvmErasureNameFromList(cacheKey.type)}>"
            kClass.isArray() -> getArrayTypeName(kClass, cacheKey.type)
            else -> getCacheTypeName(cacheKey.type)
        }

        return "$cacheKeyFromTypeName:${cacheKey.inputType}"
    }

    private fun getArrayTypeName(kClass: KClass<*>, kType: KType): String {
        val kClassName = getArrayTypeNameFromKClass(kClass)

        return when {
            kClassName != null -> kClassName
            else -> "Array<${getJvmErasureNameFromList(kType)}>"
        }
    }

    private fun getArrayTypeNameFromKClass(kClass: KClass<*>): String? = when {
        kClass.isSubclassOf(IntArray::class) -> IntArray::class.simpleName
        kClass.isSubclassOf(LongArray::class) -> LongArray::class.simpleName
        kClass.isSubclassOf(ShortArray::class) -> ShortArray::class.simpleName
        kClass.isSubclassOf(FloatArray::class) -> FloatArray::class.simpleName
        kClass.isSubclassOf(DoubleArray::class) -> DoubleArray::class.simpleName
        kClass.isSubclassOf(CharArray::class) -> CharArray::class.simpleName
        kClass.isSubclassOf(BooleanArray::class) -> BooleanArray::class.simpleName
        else -> null
    }

    private fun getCacheTypeName(kType: KType): String {
        throwIfTypeIsNotSupported(kType)
        return kType.getJvmErasureName()
    }

    private fun getJvmErasureNameFromList(type: KType): String =
        type.getTypeOfFirstArgument().getJvmErasureName()

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
