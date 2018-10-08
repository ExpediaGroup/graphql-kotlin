package com.expedia.graphql.schema.generator

import graphql.Scalars
import graphql.schema.GraphQLType
import java.math.BigDecimal
import java.math.BigInteger
import kotlin.reflect.KClass
import kotlin.reflect.KType

internal fun graphQLScalar(type: KType): GraphQLType? = when (type.classifier as? KClass<*>) {
    Int::class -> Scalars.GraphQLInt
    Long::class -> Scalars.GraphQLLong
    Short::class -> Scalars.GraphQLShort
    Float::class, Double::class -> Scalars.GraphQLFloat
    BigDecimal::class -> Scalars.GraphQLBigDecimal
    BigInteger::class -> Scalars.GraphQLBigInteger
    Char::class -> Scalars.GraphQLChar
    String::class -> Scalars.GraphQLString
    Boolean::class -> Scalars.GraphQLBoolean
    else -> null
}

internal fun getGraphQLClassName(klass: KClass<*>, inputClass: Boolean): String? {
    val simpleName = klass.simpleName
    return if (simpleName != null && inputClass) getInputClassName(simpleName) else simpleName
}

private fun getInputClassName(className: String) = "${className}Input"
