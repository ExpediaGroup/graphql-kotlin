package com.expedia.graphql.schema.generator.types

import com.expedia.graphql.schema.generator.SchemaGenerator
import com.expedia.graphql.schema.generator.TypeBuilder
import graphql.schema.GraphQLEnumType
import kotlin.reflect.KClass

internal class EnumTypeBuilder(generator: SchemaGenerator) : TypeBuilder(generator) {
    internal fun enumType(kClass: KClass<out Enum<*>>): GraphQLEnumType {
        val builder = GraphQLEnumType.newEnum()

        kClass.java.enumConstants.forEach {
            builder.value(it.name)
        }

        builder.name(kClass.simpleName)
        return builder.build()
    }
}
