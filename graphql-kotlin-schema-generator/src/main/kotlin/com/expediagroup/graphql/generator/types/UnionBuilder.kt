package com.expediagroup.graphql.generator.types

import com.expediagroup.graphql.generator.SchemaGenerator
import com.expediagroup.graphql.generator.TypeBuilder
import com.expediagroup.graphql.generator.extensions.getGraphQLDescription
import com.expediagroup.graphql.generator.extensions.getSimpleName
import graphql.TypeResolutionEnvironment
import graphql.schema.GraphQLObjectType
import graphql.schema.GraphQLType
import graphql.schema.GraphQLTypeReference
import graphql.schema.GraphQLTypeUtil
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

            subTypeMapper.getSubTypesOf(kClass)
                .map { graphQLTypeOf(it.createType()) }
                .forEach {
                    when (val unwrappedType = GraphQLTypeUtil.unwrapNonNull(it)) {
                        is GraphQLTypeReference -> builder.possibleType(unwrappedType)
                        is GraphQLObjectType -> builder.possibleType(unwrappedType)
                    }
                }

            val unionType = builder.build()
            codeRegistry.typeResolver(unionType) { env: TypeResolutionEnvironment -> env.schema.getObjectType(env.getObject<Any>().javaClass.kotlin.getSimpleName()) }
            config.hooks.onRewireGraphQLType(unionType)
        }
    }
}
