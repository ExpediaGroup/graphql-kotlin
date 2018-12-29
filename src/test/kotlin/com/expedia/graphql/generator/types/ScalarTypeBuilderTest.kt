package com.expedia.graphql.generator.types

import com.expedia.graphql.exceptions.InvalidIdTypeException
import graphql.Scalars
import graphql.schema.GraphQLScalarType
import java.math.BigDecimal
import java.math.BigInteger
import java.util.UUID
import kotlin.reflect.KClass
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
        verifyKClass(Int::class, Scalars.GraphQLInt)
        verifyKClass(Long::class, Scalars.GraphQLLong)
        verifyKClass(Short::class, Scalars.GraphQLShort)
        verifyKClass(Float::class, Scalars.GraphQLFloat)
        verifyKClass(Double::class, Scalars.GraphQLFloat)
        verifyKClass(BigDecimal::class, Scalars.GraphQLBigDecimal)
        verifyKClass(BigInteger::class, Scalars.GraphQLBigInteger)
        verifyKClass(Char::class, Scalars.GraphQLChar)
        verifyKClass(String::class, Scalars.GraphQLString)
        verifyKClass(Boolean::class, Scalars.GraphQLBoolean)
        verifyKClass(IntArray::class, null)
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

    private fun verifyKClass(kClass: KClass<*>, expected: GraphQLScalarType?) = verify(kClass.createType(), expected)

    private fun verify(kType: KType, expected: GraphQLScalarType?, annotatedAsID: Boolean = false) {
        val actual = builder.scalarType(kType, annotatedAsID)
        assertEquals(expected = expected, actual = actual)
    }
}
