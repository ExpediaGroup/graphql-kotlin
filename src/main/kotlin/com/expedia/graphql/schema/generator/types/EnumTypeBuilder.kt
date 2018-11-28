package com.expedia.graphql.schema.generator.types

import com.expedia.graphql.schema.extensions.getDeprecationReason
import com.expedia.graphql.schema.extensions.graphQLDescription
import com.expedia.graphql.schema.generator.SchemaGenerator
import com.expedia.graphql.schema.generator.TypeBuilder
import graphql.schema.GraphQLEnumType
import graphql.schema.GraphQLEnumValueDefinition
import kotlin.reflect.KClass

internal class EnumTypeBuilder(generator: SchemaGenerator) : TypeBuilder(generator) {
    internal fun enumType(kClass: KClass<out Enum<*>>): GraphQLEnumType {
        val enumBuilder = GraphQLEnumType.newEnum()

        enumBuilder.name(kClass.simpleName)
        enumBuilder.description(kClass.graphQLDescription())

        kClass.java.enumConstants.forEach {
            val valueBuilder = GraphQLEnumValueDefinition.newEnumValueDefinition()

            valueBuilder.name(it.name)
            valueBuilder.value(it.name)

            val valueField = kClass.java.getField(it.name)
            valueBuilder.description(valueField.graphQLDescription())
            valueBuilder.deprecationReason(valueField.getDeprecationReason())

            enumBuilder.value(valueBuilder.build())
        }
        return enumBuilder.build()
    }
}
