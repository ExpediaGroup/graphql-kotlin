package com.expedia.graphql.schema.generator.types

import graphql.Scalars
import graphql.schema.GraphQLScalarType
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.math.BigInteger
import kotlin.reflect.KClass
import kotlin.reflect.full.createType
import kotlin.test.assertEquals

internal class DefaultGraphQLScalarsTest {

    @Test
    fun `test all types`() {
        verify(Int::class, Scalars.GraphQLInt)
        verify(Long::class, Scalars.GraphQLLong)
        verify(Short::class, Scalars.GraphQLShort)
        verify(Float::class, Scalars.GraphQLFloat)
        verify(Double::class, Scalars.GraphQLFloat)
        verify(BigDecimal::class, Scalars.GraphQLBigDecimal)
        verify(BigInteger::class, Scalars.GraphQLBigInteger)
        verify(Char::class, Scalars.GraphQLChar)
        verify(String::class, Scalars.GraphQLString)
        verify(Boolean::class, Scalars.GraphQLBoolean)
        verify(IntArray::class, null)
    }

    private fun verify(kClass: KClass<*>, expected: GraphQLScalarType?) {
        val actual = defaultGraphQLScalars(kClass.createType())
        assertEquals(expected = expected, actual = actual)
    }
}
