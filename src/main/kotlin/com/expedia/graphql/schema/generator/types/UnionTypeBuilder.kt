package com.expedia.graphql.schema.generator.types

import com.expedia.graphql.schema.extensions.getGraphQLDescription
import com.expedia.graphql.schema.generator.SchemaGenerator
import com.expedia.graphql.schema.generator.TypeBuilder
import com.expedia.graphql.schema.generator.TypesCacheKey
import com.expedia.graphql.schema.generator.models.KGraphQLType
import graphql.TypeResolutionEnvironment
import graphql.schema.GraphQLObjectType
import graphql.schema.GraphQLType
import graphql.schema.GraphQLTypeReference
import graphql.schema.GraphQLUnionType
import kotlin.reflect.KClass
import kotlin.reflect.full.createType

internal class UnionTypeBuilder(generator: SchemaGenerator) : TypeBuilder(generator) {
    internal fun unionType(kClass: KClass<*>): GraphQLType {
        return state.cache.buildIfNotUnderConstruction(kClass) { _ ->
            val builder = GraphQLUnionType.newUnionType()

            builder.name(kClass.simpleName)
            builder.description(kClass.getGraphQLDescription())
            builder.typeResolver { env: TypeResolutionEnvironment -> env.schema.getObjectType(env.getObject<Any>().javaClass.simpleName) }

            val implementations = subTypeMapper.getSubTypesOf(kClass)
            implementations.forEach {
                val objectType = state.cache.get(TypesCacheKey(it.kotlin.createType(), false))
                    ?: generator.objectType(it.kotlin)

                val key = TypesCacheKey(it.kotlin.createType(), false)

                if (objectType is GraphQLTypeReference) {
                    builder.possibleType(objectType)
                } else {
                    builder.possibleType(objectType as? GraphQLObjectType)
                }

                if (state.cache.doesNotContain(it.kotlin)) {
                    state.cache.put(key, KGraphQLType(it.kotlin, objectType))
                }
            }

            builder.build()
        }
    }
}
