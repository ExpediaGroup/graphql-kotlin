package com.expediagroup.graphql.generator.types

import com.expediagroup.graphql.directives.deprecatedDirectiveWithReason
import com.expediagroup.graphql.generator.SchemaGenerator
import com.expediagroup.graphql.generator.TypeBuilder
import com.expediagroup.graphql.generator.extensions.getDeprecationReason
import com.expediagroup.graphql.generator.extensions.getGraphQLDescription
import com.expediagroup.graphql.generator.extensions.getSimpleName
import com.expediagroup.graphql.generator.extensions.safeCast
import graphql.schema.GraphQLEnumType
import graphql.schema.GraphQLEnumValueDefinition
import kotlin.reflect.KClass

internal class EnumBuilder(generator: SchemaGenerator) : TypeBuilder(generator) {
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
        return config.hooks.onRewireGraphQLType(enumBuilder.build()).safeCast()
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

        valueField.getDeprecationReason()?.let {
            valueBuilder.deprecationReason(it)
            valueBuilder.withDirective(deprecatedDirectiveWithReason(it))
        }
        return config.hooks.onRewireGraphQLType(valueBuilder.build()).safeCast()
    }
}
