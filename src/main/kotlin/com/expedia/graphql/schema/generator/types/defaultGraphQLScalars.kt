package com.expedia.graphql.schema.generator.types

import graphql.Scalars
import graphql.schema.GraphQLType
import java.math.BigDecimal
import java.math.BigInteger
import java.util.UUID
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

private val validIdTypes = listOf(Int::class, String::class, Long::class, UUID::class)

internal fun defaultGraphQLScalars(type: KType, annotatedAsID: Boolean = false): GraphQLType? {
    val kclass = type.classifier as? KClass<*>
    return if (annotatedAsID) {
        if (validIdTypes.contains(kclass)) {
            Scalars.GraphQLID
        } else {
            val types = validIdTypes.joinToString(prefix = "[", postfix = "]", separator = ", ") { it.qualifiedName ?: "" }
            throw IllegalArgumentException("${kclass?.simpleName} is not a valid ID type, only $types are accepted")
        }
    } else {
        defaultScalarsMap[kclass]
    }
}
