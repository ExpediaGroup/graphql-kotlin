package com.expediagroup.graphql.generator.types

import com.expediagroup.graphql.exceptions.InvalidIdTypeException
import com.expediagroup.graphql.generator.SchemaGenerator
import com.expediagroup.graphql.generator.TypeBuilder
import com.expediagroup.graphql.generator.extensions.getKClass
import com.expediagroup.graphql.generator.extensions.getQualifiedName
import com.expediagroup.graphql.generator.extensions.safeCast
import graphql.Scalars
import graphql.schema.GraphQLScalarType
import java.math.BigDecimal
import java.math.BigInteger
import java.util.UUID
import kotlin.reflect.KClass
import kotlin.reflect.KType

internal class ScalarBuilder(generator: SchemaGenerator) : TypeBuilder(generator) {

    internal fun scalarType(type: KType, annotatedAsID: Boolean): GraphQLScalarType? {
        val kClass = type.getKClass()

        val scalar = when {
            annotatedAsID -> getId(kClass)
            else -> defaultScalarsMap[kClass]
        }
        return scalar?.let {
            config.hooks.onRewireGraphQLType(it).safeCast()
        }
    }

    @Throws(InvalidIdTypeException::class)
    private fun getId(kClass: KClass<*>): GraphQLScalarType? {
        return if (validIdTypes.contains(kClass)) {
            Scalars.GraphQLID
        } else {
            val types = validIdTypes.joinToString(prefix = "[", postfix = "]", separator = ", ") {
                it.getQualifiedName()
            }
            throw InvalidIdTypeException(kClass, types)
        }
    }

    private companion object {
        private val validIdTypes = listOf(Int::class, String::class, Long::class, UUID::class)
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
    }
}
