package com.expedia.graphql.generator.types

import com.expedia.graphql.generator.SchemaGenerator
import com.expedia.graphql.generator.TypeBuilder
import com.expedia.graphql.generator.extensions.getGraphQLDescription
import com.expedia.graphql.generator.extensions.getSimpleName
import com.expedia.graphql.generator.extensions.getValidFunctions
import com.expedia.graphql.generator.extensions.getValidProperties
import com.expedia.graphql.generator.extensions.safeCast
import graphql.TypeResolutionEnvironment
import graphql.schema.GraphQLInterfaceType
import graphql.schema.GraphQLType
import graphql.schema.GraphQLTypeReference
import kotlin.reflect.KClass
import kotlin.reflect.full.createType

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
                .forEach { builder.field(generator.function(it, kClass.getSimpleName(), abstract = true)) }

            val implementations = subTypeMapper.getSubTypesOf(kClass)
            implementations.forEach { implementation ->
                val kType = implementation.kotlin.createType()
                val objectType = graphQLTypeOf(kType)
                // skip objects currently under construction
                if (objectType !is GraphQLTypeReference) {
                    state.additionalTypes.add(objectType)
                }
            }

            val interfaceType = builder.build()
            codeRegistry.typeResolver(interfaceType) { env: TypeResolutionEnvironment -> env.schema.getObjectType(env.getObject<Any>().javaClass.kotlin.getSimpleName()) }
            config.hooks.onRewireGraphQLType(interfaceType).safeCast()
        }
    }
}
