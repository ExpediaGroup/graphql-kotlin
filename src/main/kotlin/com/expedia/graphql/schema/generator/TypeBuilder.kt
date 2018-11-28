package com.expedia.graphql.schema.generator

import com.expedia.graphql.schema.extensions.canBeGraphQLInterface
import com.expedia.graphql.schema.extensions.canBeGraphQLUnion
import com.expedia.graphql.schema.extensions.wrapInNonNull
import com.expedia.graphql.schema.generator.models.KGraphQLType
import graphql.schema.GraphQLType
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.isSubclassOf

internal open class TypeBuilder constructor(val generator: SchemaGenerator) {
    val state = generator.state
    val config = generator.config
    val subTypeMapper = generator.subTypeMapper

    internal fun graphQLTypeOf(type: KType, inputType: Boolean = false, annotatedAsID: Boolean = false): GraphQLType {
        val hookGraphQLType = config.hooks.willGenerateGraphQLType(type)
        val graphQLType = hookGraphQLType ?: generator.scalarType(type, annotatedAsID)
        ?: objectFromReflection(type, inputType)
        val typeWithNullityTakenIntoAccount = graphQLType.wrapInNonNull(type)
        config.hooks.didGenerateGraphQLType(type, typeWithNullityTakenIntoAccount)
        return typeWithNullityTakenIntoAccount
    }

    internal fun objectFromReflection(type: KType, inputType: Boolean): GraphQLType {
        val cacheKey = TypesCacheKey(type, inputType)
        val cachedType = state.cache.get(cacheKey)

        if (cachedType != null) {
            return cachedType
        }

        val kClass = checkNotNull(type.classifier as? KClass<*>)
        val graphQLType = getGraphQLType(kClass, inputType, type)
        val kGraphQLType = KGraphQLType(kClass, graphQLType)

        state.cache.put(cacheKey, kGraphQLType)

        return graphQLType
    }

    private fun getGraphQLType(kClass: KClass<*>, inputType: Boolean, type: KType): GraphQLType = when {
        kClass.isSubclassOf(Enum::class) -> @Suppress("UNCHECKED_CAST") (generator.enumType(kClass as KClass<Enum<*>>))
        kClass.isSubclassOf(List::class) || kClass.java.isArray -> generator.listType(type, inputType)
        kClass.canBeGraphQLUnion() -> generator.unionType(kClass)
        kClass.canBeGraphQLInterface() -> generator.interfaceType(kClass)
        else -> if (inputType) generator.inputObjectType(kClass) else generator.objectType(kClass)
    }
}
