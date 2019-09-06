package com.expediagroup.graphql.federation.types

import com.expediagroup.graphql.federation.directives.FieldSet
import graphql.language.IntValue
import graphql.language.StringValue
import graphql.schema.Coercing
import graphql.schema.CoercingParseLiteralException
import graphql.schema.CoercingSerializeException
import org.junit.jupiter.api.Test
import java.math.BigInteger
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

internal class FieldSetTest {
    private val coercing: Coercing<Any, Any> = FIELD_SET_SCALAR_TYPE.coercing

    @Test
    fun `serialize should throw exception when not a FieldSet`() {
        assertFailsWith(CoercingSerializeException::class) {
            coercing.serialize(StringValue("hello"))
        }
    }

    @Test
    fun `serialize should return the value when a FieldSet`() {

        @FieldSet("1")
        class MyClass

        val result = coercing.serialize(MyClass::class.annotations.first())
        assertEquals(expected = "1", actual = result)
    }

    @Test
    fun `parseValue should run to parseLiteral`() {
        val result = coercing.parseValue(StringValue("hello"))

        assertTrue(result is FieldSet)
    }

    @Test
    fun `parseValue should throw exception on non-StringValue`() {
        assertFailsWith(CoercingParseLiteralException::class) {
            coercing.parseValue(IntValue(BigInteger.ONE))
        }
    }

    @Test
    fun `parseLiteral should map StringValue to a FieldSet`() {
        val result = coercing.parseLiteral(StringValue("hello"))

        assertTrue(result is FieldSet)
    }

    @Test
    fun `parseLiteral should throw exception on non-StringValue`() {
        assertFailsWith(CoercingParseLiteralException::class) {
            coercing.parseLiteral(IntValue(BigInteger.ONE))
        }
    }
}
