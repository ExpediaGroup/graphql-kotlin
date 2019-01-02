package com.expedia.graphql.generator.types

import com.expedia.graphql.generator.SchemaGenerator
import com.expedia.graphql.generator.TypeBuilder
import com.expedia.graphql.generator.extensions.getGraphQLDescription
import com.expedia.graphql.generator.extensions.getPropertyDescription
import com.expedia.graphql.generator.extensions.getSimpleName
import com.expedia.graphql.generator.extensions.getValidProperties
import com.expedia.graphql.generator.extensions.isPropertyGraphQLID
import graphql.schema.GraphQLInputObjectField
import graphql.schema.GraphQLInputObjectType
import graphql.schema.GraphQLInputType
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

internal class InputObjectTypeBuilder(generator: SchemaGenerator) : TypeBuilder(generator) {
    internal fun inputObjectType(kClass: KClass<*>): GraphQLInputObjectType {
        val builder = GraphQLInputObjectType.newInputObject()

        builder.name(kClass.getSimpleName(isInputClass = true))
        builder.description(kClass.getGraphQLDescription())

        // It does not make sense to run functions against the input types so we only process the properties
        kClass.getValidProperties(config.hooks)
            .forEach { builder.field(inputProperty(it, kClass)) }

        return builder.build()
    }

    private fun inputProperty(prop: KProperty<*>, parentClass: KClass<*>): GraphQLInputObjectField {
        val builder = GraphQLInputObjectField.newInputObjectField()

        builder.description(prop.getPropertyDescription(parentClass))
        builder.name(prop.name)
        builder.type(graphQLTypeOf(prop.returnType, true, prop.isPropertyGraphQLID(parentClass)) as? GraphQLInputType)

        return builder.build()
    }
}
