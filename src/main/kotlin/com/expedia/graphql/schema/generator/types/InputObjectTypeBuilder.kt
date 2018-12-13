package com.expedia.graphql.schema.generator.types

import com.expedia.graphql.schema.extensions.getPropertyDescription
import com.expedia.graphql.schema.extensions.getValidProperties
import com.expedia.graphql.schema.extensions.getGraphQLDescription
import com.expedia.graphql.schema.extensions.isPropertyGraphQLID
import com.expedia.graphql.schema.generator.SchemaGenerator
import com.expedia.graphql.schema.generator.TypeBuilder
import graphql.schema.GraphQLInputObjectField
import graphql.schema.GraphQLInputObjectType
import graphql.schema.GraphQLInputType
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

internal class InputObjectTypeBuilder(generator: SchemaGenerator) : TypeBuilder(generator) {
    internal fun inputObjectType(kClass: KClass<*>): GraphQLInputObjectType {
        val builder = GraphQLInputObjectType.newInputObject()
        val name = getInputClassName(kClass)

        builder.name(name)
        builder.description(kClass.getGraphQLDescription())

        // It does not make sense to run functions against the input types so we only process data fields
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

    private fun getInputClassName(klass: KClass<*>): String? = klass.simpleName?.let { "${it}Input" }
}
