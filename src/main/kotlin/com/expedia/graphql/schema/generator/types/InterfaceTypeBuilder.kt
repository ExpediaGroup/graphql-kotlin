package com.expedia.graphql.schema.generator.types

import com.expedia.graphql.schema.extensions.getValidFunctions
import com.expedia.graphql.schema.extensions.getValidProperties
import com.expedia.graphql.schema.extensions.graphQLDescription
import com.expedia.graphql.schema.generator.SchemaGenerator
import com.expedia.graphql.schema.generator.TypeBuilder
import graphql.TypeResolutionEnvironment
import graphql.schema.GraphQLInterfaceType
import graphql.schema.GraphQLType
import graphql.schema.GraphQLTypeReference
import kotlin.reflect.KClass

internal class InterfaceTypeBuilder(generator: SchemaGenerator) : TypeBuilder<GraphQLInterfaceType>(generator) {
    internal fun interfaceType(kClass: KClass<*>): GraphQLType {
        return state.cache.buildIfNotUnderConstruction(kClass) { _ ->
            val builder = GraphQLInterfaceType.newInterface()

            builder.name(kClass.simpleName)
            builder.description(kClass.graphQLDescription())

            kClass.getValidProperties(config.hooks)
                .forEach { builder.field(generator.property(it)) }

            kClass.getValidFunctions(config.hooks)
                .forEach { builder.field(generator.function(it, abstract = true)) }

            builder.typeResolver { env: TypeResolutionEnvironment -> env.schema.getObjectType(env.getObject<Any>().javaClass.simpleName) }
            val interfaceType = builder.build()

            val implementations = subTypeMapper.getSubTypesOf(kClass)
            implementations.forEach {
                val objectType = generator.objectType(it.kotlin, interfaceType)

                if (objectType !is GraphQLTypeReference) {
                    state.additionalTypes.add(objectType)
                }
                state.cache.removeTypeUnderConstruction(it.kotlin)
            }

            interfaceType
        }
    }
}
