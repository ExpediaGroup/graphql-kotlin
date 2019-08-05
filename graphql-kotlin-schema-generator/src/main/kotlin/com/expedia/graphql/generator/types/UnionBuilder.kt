package com.expedia.graphql.generator.types

import com.expedia.graphql.generator.SchemaGenerator
import com.expedia.graphql.generator.TypeBuilder
import com.expedia.graphql.generator.extensions.getGraphQLDescription
import com.expedia.graphql.generator.extensions.getSimpleName
import com.expedia.graphql.generator.state.TypesCacheKey
import graphql.TypeResolutionEnvironment
import graphql.schema.GraphQLObjectType
import graphql.schema.GraphQLType
import graphql.schema.GraphQLTypeReference
import graphql.schema.GraphQLUnionType
import kotlin.reflect.KClass
import kotlin.reflect.full.createType

internal class UnionBuilder(generator: SchemaGenerator) : TypeBuilder(generator) {
    internal fun unionType(kClass: KClass<*>): GraphQLType {
        return state.cache.buildIfNotUnderConstruction(kClass) {
            val builder = GraphQLUnionType.newUnionType()
            builder.name(kClass.getSimpleName())
            builder.description(kClass.getGraphQLDescription())

            generator.directives(kClass).forEach {
                builder.withDirective(it)
            }

            val implementations = subTypeMapper.getSubTypesOf(kClass)
            implementations.forEach {
                val objectType = state.cache.get(TypesCacheKey(it.kotlin.createType(), false))
                    ?: generator.objectType(it.kotlin)

                when (objectType) {
                    is GraphQLTypeReference -> builder.possibleType(objectType)
                    is GraphQLObjectType -> builder.possibleType(objectType)
                }
            }
            val unionType = builder.build()
            codeRegistry.typeResolver(unionType) { env: TypeResolutionEnvironment -> env.schema.getObjectType(env.getObject<Any>().javaClass.kotlin.getSimpleName()) }
            config.hooks.onRewireGraphQLType(unionType)
        }
    }
}
