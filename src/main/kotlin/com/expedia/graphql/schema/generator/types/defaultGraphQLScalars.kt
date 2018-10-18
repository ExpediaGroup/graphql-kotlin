package com.expedia.graphql.schema.generator.types

import graphql.Scalars
import graphql.schema.GraphQLType
import java.math.BigDecimal
import java.math.BigInteger
import kotlin.reflect.KClass
import kotlin.reflect.KType

private val defaultScalarsMap = mapOf(
    Int::class to Scalars.GraphQLInt,
    Long::class to Scalars.GraphQLLong,
    Short::class to Scalars.GraphQLShort,
    Float::class to Scalars.GraphQLFloat,
    Double::class to Scalars.GraphQLFloat,
    BigDecimal::class to Scalars.GraphQLBigDecimal,
    BigInteger::class to Scalars.GraphQLBigInteger,
    Char::class to Scalars.GraphQLChar,
    String::class to Scalars.GraphQLString,
    Boolean::class to Scalars.GraphQLBoolean
)

internal fun defaultGraphQLScalars(type: KType): GraphQLType? {
    val kclass = type.classifier as? KClass<*>

    return defaultScalarsMap[kclass]
}
