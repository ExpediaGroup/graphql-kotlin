package com.expedia.graphql.generator.types

import com.expedia.graphql.generator.SchemaGenerator
import com.expedia.graphql.generator.TypeBuilder
import com.expedia.graphql.generator.extensions.getDeprecationReason
import com.expedia.graphql.generator.extensions.getGraphQLDescription
import com.expedia.graphql.generator.extensions.getSimpleName
import graphql.schema.GraphQLEnumType
import graphql.schema.GraphQLEnumValueDefinition
import kotlin.reflect.KClass

internal class EnumTypeBuilder(generator: SchemaGenerator) : TypeBuilder(generator) {
    internal fun enumType(kClass: KClass<out Enum<*>>): GraphQLEnumType {
        val enumBuilder = GraphQLEnumType.newEnum()

        enumBuilder.name(kClass.getSimpleName())
        enumBuilder.description(kClass.getGraphQLDescription())

        kClass.java.enumConstants.forEach {
            val valueBuilder = GraphQLEnumValueDefinition.newEnumValueDefinition()

            valueBuilder.name(it.name)
            valueBuilder.value(it.name)

            val valueField = kClass.java.getField(it.name)
            valueBuilder.description(valueField.getGraphQLDescription())
            valueBuilder.deprecationReason(valueField.getDeprecationReason())

            enumBuilder.value(valueBuilder.build())
        }
        return enumBuilder.build()
    }
}
