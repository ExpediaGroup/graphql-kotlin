package com.expedia.graphql.generator.types

import com.expedia.graphql.generator.SchemaGenerator
import com.expedia.graphql.generator.TypeBuilder
import com.expedia.graphql.generator.extensions.getGraphQLDescription
import com.expedia.graphql.generator.extensions.getSimpleName
import com.expedia.graphql.generator.extensions.getValidProperties
import com.expedia.graphql.generator.extensions.safeCast
import graphql.schema.GraphQLInputObjectType
import kotlin.reflect.KClass

internal class InputObjectBuilder(generator: SchemaGenerator) : TypeBuilder(generator) {
    internal fun inputObjectType(kClass: KClass<*>): GraphQLInputObjectType {
        val builder = GraphQLInputObjectType.newInputObject()

        builder.name(kClass.getSimpleName(isInputClass = true))
        builder.description(kClass.getGraphQLDescription())

        generator.directives(kClass).forEach {
            builder.withDirective(it)
        }

        // It does not make sense to run functions against the input types so we only process the properties
        kClass.getValidProperties(config.hooks).forEach {
            builder.field(generator.inputProperty(it, kClass))
        }

        return config.hooks.onRewireGraphQLType(builder.build()).safeCast()
    }
}
