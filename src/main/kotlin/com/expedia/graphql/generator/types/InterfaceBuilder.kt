package com.expedia.graphql.generator.types

import com.expedia.graphql.generator.SchemaGenerator
import com.expedia.graphql.generator.TypeBuilder
import com.expedia.graphql.generator.extensions.getGraphQLDescription
import com.expedia.graphql.generator.extensions.getSimpleName
import com.expedia.graphql.generator.extensions.getValidFunctions
import com.expedia.graphql.generator.extensions.getValidProperties
import graphql.TypeResolutionEnvironment
import graphql.schema.GraphQLInterfaceType
import graphql.schema.GraphQLType
import graphql.schema.GraphQLTypeReference
import kotlin.reflect.KClass

internal class InterfaceBuilder(generator: SchemaGenerator) : TypeBuilder(generator) {
    internal fun interfaceType(kClass: KClass<*>): GraphQLType {
        return state.cache.buildIfNotUnderConstruction(kClass) {
            val builder = GraphQLInterfaceType.newInterface()

            builder.name(kClass.getSimpleName())
            builder.description(kClass.getGraphQLDescription())

            generator.directives(kClass).forEach {
                builder.withDirective(it)
            }

            kClass.getValidProperties(config.hooks)
                .forEach { builder.field(generator.property(it, kClass)) }

            kClass.getValidFunctions(config.hooks)
                .forEach { builder.field(generator.function(it, abstract = true)) }

            builder.typeResolver { env: TypeResolutionEnvironment -> env.schema.getObjectType(env.getObject<Any>().javaClass.simpleName) }
            val interfaceType = builder.build()

            val implementations = subTypeMapper.getSubTypesOf(kClass)
            implementations.forEach {
                val objectType = generator.objectType(it.kotlin, interfaceType)

                // Only update the state if the object is fully constructed and not a reference
                if (objectType !is GraphQLTypeReference) {
                    state.additionalTypes.add(objectType)
                    state.cache.removeTypeUnderConstruction(it.kotlin)
                }
            }

            interfaceType
        }
    }
}
