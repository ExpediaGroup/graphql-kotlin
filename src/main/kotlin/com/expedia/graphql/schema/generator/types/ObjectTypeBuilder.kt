package com.expedia.graphql.schema.generator.types

import com.expedia.graphql.schema.extensions.isGraphQLInterface
import com.expedia.graphql.schema.extensions.isGraphQLUnion
import com.expedia.graphql.schema.extensions.directives
import com.expedia.graphql.schema.extensions.getValidFunctions
import com.expedia.graphql.schema.extensions.getValidProperties
import com.expedia.graphql.schema.extensions.graphQLDescription
import com.expedia.graphql.schema.generator.SchemaGenerator
import com.expedia.graphql.schema.generator.TypeBuilder
import graphql.schema.GraphQLInterfaceType
import graphql.schema.GraphQLObjectType
import graphql.schema.GraphQLType
import kotlin.reflect.KClass
import kotlin.reflect.full.createType
import kotlin.reflect.full.superclasses

internal class ObjectTypeBuilder(generator: SchemaGenerator) : TypeBuilder(generator) {

    internal fun objectType(kClass: KClass<*>, interfaceType: GraphQLInterfaceType? = null): GraphQLType {
        return state.cache.buildIfNotUnderConstruction(kClass) { _ ->
            val builder = GraphQLObjectType.newObject()

            builder.name(kClass.simpleName)
            builder.description(kClass.graphQLDescription())

            kClass.directives(generator).forEach {
                builder.withDirective(it)
                state.directives.add(it)
            }

            if (interfaceType != null) {
                builder.withInterface(interfaceType)
            } else {
                kClass.superclasses
                    .asSequence()
                    .filter { it.isGraphQLInterface() && !it.isGraphQLUnion() }
                    .map { objectFromReflection(it.createType(), false) as? GraphQLInterfaceType }
                    .forEach { builder.withInterface(it) }
            }

            kClass.getValidProperties(config.hooks)
                .forEach { builder.field(generator.property(it)) }

            kClass.getValidFunctions(config.hooks)
                .forEach { builder.field(generator.function(it)) }

            builder.build()
        }
    }
}
