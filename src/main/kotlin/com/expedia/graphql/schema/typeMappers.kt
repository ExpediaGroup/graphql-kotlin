package com.expedia.graphql.schema

import com.expedia.graphql.schema.exceptions.NestingNonNullTypeException
import graphql.Scalars
import graphql.schema.GraphQLNonNull
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

@Throws(NestingNonNullTypeException::class)
internal fun GraphQLType.wrapInNonNull(type: KType): GraphQLType = when {
    this is GraphQLNonNull -> throw NestingNonNullTypeException(this, type)
    type.isMarkedNullable -> this
    else -> GraphQLNonNull.nonNull(this)
}

internal fun getGraphQLClassName(klass: KClass<*>, inputClass: Boolean): String? {
    val simpleName = klass.simpleName
    return if (simpleName != null && inputClass) getInputClassName(simpleName) else simpleName
}

private fun getInputClassName(className: String?) = "${className}Input"
