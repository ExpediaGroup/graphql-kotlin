package com.expedia.graphql.generator.types

import com.expedia.graphql.generator.SchemaGenerator
import com.expedia.graphql.generator.TypeBuilder
import com.expedia.graphql.generator.extensions.getPropertyDescription
import com.expedia.graphql.generator.extensions.getPropertyName
import com.expedia.graphql.generator.extensions.isPropertyGraphQLID
import com.expedia.graphql.generator.extensions.safeCast
import graphql.schema.GraphQLInputObjectField
import graphql.schema.GraphQLInputType
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

internal class InputPropertyBuilder(generator: SchemaGenerator) : TypeBuilder(generator) {

    internal fun inputProperty(prop: KProperty<*>, parentClass: KClass<*>): GraphQLInputObjectField {
        val builder = GraphQLInputObjectField.newInputObjectField()

        builder.description(prop.getPropertyDescription(parentClass))
        builder.name(prop.getPropertyName(parentClass))
        builder.type(graphQLTypeOf(prop.returnType, true, prop.isPropertyGraphQLID(parentClass)).safeCast<GraphQLInputType>())

        generator.directives(prop, parentClass).forEach {
            builder.withDirective(it)
        }

        return config.hooks.onRewireGraphQLType(builder.build()).safeCast()
    }
}
