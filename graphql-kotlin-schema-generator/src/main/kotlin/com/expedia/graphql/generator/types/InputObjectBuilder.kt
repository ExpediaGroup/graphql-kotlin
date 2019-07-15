package com.expedia.graphql.generator.types

import com.expedia.graphql.generator.SchemaGenerator
import com.expedia.graphql.generator.TypeBuilder
import com.expedia.graphql.generator.extensions.getGraphQLDescription
import com.expedia.graphql.generator.extensions.getPropertyDescription
import com.expedia.graphql.generator.extensions.getSimpleName
import com.expedia.graphql.generator.extensions.getValidProperties
import com.expedia.graphql.generator.extensions.isPropertyGraphQLID
import com.expedia.graphql.generator.extensions.safeCast
import graphql.schema.GraphQLInputObjectField
import graphql.schema.GraphQLInputObjectType
import graphql.schema.GraphQLInputType
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

internal class InputObjectBuilder(generator: SchemaGenerator) : TypeBuilder(generator) {
    internal fun inputObjectType(kClass: KClass<*>): GraphQLInputObjectType {
        val builder = GraphQLInputObjectType.newInputObject()

        builder.name(kClass.getSimpleName(isInputClass = true))
        builder.description(kClass.getGraphQLDescription())

        generator.directives(kClass).forEach {
            builder.withDirective(it)
        }

        // It does not make sense to run functions against the input types so we only process the properties
        kClass.getValidProperties(config.hooks)
            .forEach { builder.field(inputProperty(it, kClass)) }

        return config.hooks.onRewireGraphQLType(builder.build()).safeCast()
    }

    private fun inputProperty(prop: KProperty<*>, parentClass: KClass<*>): GraphQLInputObjectField {
        val builder = GraphQLInputObjectField.newInputObjectField()

        builder.description(prop.getPropertyDescription(parentClass))
        builder.name(prop.name)
        builder.type(graphQLTypeOf(prop.returnType, true, prop.isPropertyGraphQLID(parentClass)).safeCast<GraphQLInputType>())

        generator.directives(prop).forEach {
            builder.withDirective(it)
        }

        return config.hooks.onRewireGraphQLType(builder.build()).safeCast()
    }
}
