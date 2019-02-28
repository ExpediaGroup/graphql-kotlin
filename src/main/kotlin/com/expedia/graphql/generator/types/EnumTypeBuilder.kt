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

        generator.directives(kClass).forEach {
            enumBuilder.withDirective(it)
        }

        kClass.java.enumConstants.forEach {
            enumBuilder.value(getEnumValueDefinition(it, kClass))
        }

        return enumBuilder.build()
    }

    private fun getEnumValueDefinition(enum: Enum<*>, kClass: KClass<out Enum<*>>): GraphQLEnumValueDefinition {
        val valueBuilder = GraphQLEnumValueDefinition.newEnumValueDefinition()

        valueBuilder.name(enum.name)
        valueBuilder.value(enum.name)

        val valueField = kClass.java.getField(enum.name)

        generator.fieldDirectives(valueField).forEach {
            valueBuilder.withDirective(it)
        }

        valueBuilder.description(valueField.getGraphQLDescription())
        valueBuilder.deprecationReason(valueField.getDeprecationReason())

        return valueBuilder.build()
    }
}
