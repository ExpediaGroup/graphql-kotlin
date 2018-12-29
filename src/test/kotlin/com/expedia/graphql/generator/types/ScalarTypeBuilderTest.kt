package com.expedia.graphql.generator.types

import com.expedia.graphql.exceptions.InvalidIdTypeException
import graphql.Scalars
import graphql.schema.GraphQLScalarType
import java.math.BigDecimal
import java.math.BigInteger
import java.util.UUID
import kotlin.reflect.KType
import kotlin.reflect.full.createType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

internal class ScalarTypeBuilderTest : TypeTestHelper() {

    private lateinit var builder: ScalarTypeBuilder

    override fun beforeTest() {
        builder = ScalarTypeBuilder(generator)
    }

    internal class Ids {
        internal val stringID: String = "abc"
        internal val intID: Int = 1
        internal val longID: Long = 2
        internal val uuid: UUID = UUID.randomUUID()
        internal val invalidID: Double = 3.0
    }

    @Test
    fun `test all types`() {
        verify(Int::class.createType(), Scalars.GraphQLInt)
        verify(Long::class.createType(), Scalars.GraphQLLong)
        verify(Short::class.createType(), Scalars.GraphQLShort)
        verify(Float::class.createType(), Scalars.GraphQLFloat)
        verify(Double::class.createType(), Scalars.GraphQLFloat)
        verify(BigDecimal::class.createType(), Scalars.GraphQLBigDecimal)
        verify(BigInteger::class.createType(), Scalars.GraphQLBigInteger)
        verify(Char::class.createType(), Scalars.GraphQLChar)
        verify(String::class.createType(), Scalars.GraphQLString)
        verify(Boolean::class.createType(), Scalars.GraphQLBoolean)
        verify(IntArray::class.createType(), null)
    }

    @Test
    fun id() {
        verify(Ids::stringID.returnType, Scalars.GraphQLID, true)
        verify(Ids::intID.returnType, Scalars.GraphQLID, true)
        verify(Ids::longID.returnType, Scalars.GraphQLID, true)
        verify(Ids::uuid.returnType, Scalars.GraphQLID, true)

        assertFailsWith(InvalidIdTypeException::class) {
            verify(Ids::invalidID.returnType, Scalars.GraphQLID, true)
        }
    }

    private fun verify(kType: KType, expected: GraphQLScalarType?, annotatedAsID: Boolean = false) {
        val actual = builder.scalarType(kType, annotatedAsID)
        assertEquals(expected = expected, actual = actual)
    }
}
