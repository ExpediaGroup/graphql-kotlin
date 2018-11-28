package com.expedia.graphql.schema.generator.types

import com.expedia.graphql.schema.generator.SchemaGenerator
import com.expedia.graphql.schema.generator.TypeBuilder
import graphql.Scalars
import graphql.schema.GraphQLScalarType
import java.math.BigDecimal
import java.math.BigInteger
import java.util.UUID
import kotlin.reflect.KClass
import kotlin.reflect.KType

internal class ScalarTypeBuilder(generator: SchemaGenerator) : TypeBuilder<GraphQLScalarType>(generator) {
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

    internal fun scalarType(type: KType, annotatedAsID: Boolean = false): GraphQLScalarType? {
        val kClass = type.classifier as? KClass<*>
        return if (annotatedAsID) {
            if (validIdTypes.contains(kClass)) {
                Scalars.GraphQLID
            } else {
                val types = validIdTypes.joinToString(prefix = "[", postfix = "]", separator = ", ") {
                    it.qualifiedName ?: ""
                }
                throw IllegalArgumentException("${kClass?.simpleName} is not a valid ID type, only $types are accepted")
            }
        } else {
            defaultScalarsMap[kClass]
        }
    }
}
