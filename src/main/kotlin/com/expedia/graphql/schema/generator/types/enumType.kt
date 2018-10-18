package com.expedia.graphql.schema.generator.types

import graphql.schema.GraphQLEnumType
import kotlin.reflect.KClass

internal fun enumType(kClass: KClass<*>): GraphQLEnumType {
    val enumKClass = @Suppress("UNCHECKED_CAST") (kClass as KClass<Enum<*>>)
    val builder = GraphQLEnumType.newEnum()

    enumKClass.java.enumConstants.forEach {
        builder.value(it.name)
    }

    builder.name(enumKClass.simpleName)
    return builder.build()
}
