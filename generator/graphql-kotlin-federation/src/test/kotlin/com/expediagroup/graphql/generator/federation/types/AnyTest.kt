/*
 * Copyright 2021 Expedia, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.expediagroup.graphql.generator.federation.types

import graphql.language.ArrayValue
import graphql.language.BooleanValue
import graphql.language.EnumValue
import graphql.language.FloatValue
import graphql.language.IntValue
import graphql.language.NullValue
import graphql.language.ObjectField
import graphql.language.ObjectValue
import graphql.language.StringValue
import graphql.language.Value
import graphql.schema.CoercingParseLiteralException
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal
import java.math.BigInteger
import kotlin.test.assertEquals

class AnyTest {

    @Test
    fun `_Any scalar should allow all types`() {
        val coercing = ANY_SCALAR_TYPE.coercing

        assertEquals(expected = BigDecimal.ONE, actual = coercing.parseLiteral(FloatValue(BigDecimal.ONE)))
        assertEquals(expected = "hello", actual = coercing.parseLiteral(StringValue("hello")))
        assertEquals(expected = BigInteger.ONE, actual = coercing.parseLiteral(IntValue(BigInteger.ONE)))
        assertEquals(expected = true, actual = coercing.parseLiteral(BooleanValue(true)))
        assertEquals(expected = "MyEnum", actual = coercing.parseLiteral(EnumValue("MyEnum")))

        assertThrows<CoercingParseLiteralException> {
            coercing.parseLiteral(NullValue.newNullValue().build())
        }

        val listValues = listOf<Value<IntValue>>(IntValue(BigInteger.TEN))
        assertEquals(expected = listOf(BigInteger.TEN), actual = coercing.parseLiteral(ArrayValue(listValues)))
    }

    @Test
    fun `_Any scalar should parse ListValues`() {
        val coercing = ANY_SCALAR_TYPE.coercing
        val one = IntValue(BigInteger.ONE)
        val two = IntValue(BigInteger.TEN)

        val singleItemList = listOf<Value<IntValue>>(one)
        assertEquals(expected = listOf(BigInteger.ONE), actual = coercing.parseLiteral(ArrayValue(singleItemList)))

        val multiItemList = listOf<Value<IntValue>>(one, two)
        assertEquals(expected = listOf(BigInteger.ONE, BigInteger.TEN), actual = coercing.parseLiteral(ArrayValue(multiItemList)))
    }

    @Test
    fun `_Any scalar should parse ObjectValues`() {
        val coercing = ANY_SCALAR_TYPE.coercing
        val fieldOne = ObjectField("one", IntValue(BigInteger.ONE))
        val fieldTwo = ObjectField("ten", IntValue(BigInteger.TEN))

        val singleField = ObjectValue(listOf(fieldOne))
        assertEquals(expected = mapOf("one" to BigInteger.ONE), actual = coercing.parseLiteral(singleField))

        val multipleFields = ObjectValue(listOf(fieldOne, fieldTwo))
        assertEquals(expected = mapOf("one" to BigInteger.ONE, "ten" to BigInteger.TEN), actual = coercing.parseLiteral(multipleFields))

        assertThrows<CoercingParseLiteralException> {
            val mockValue: Value<*> = mockk()
            val objectValue = ObjectValue(listOf(ObjectField("name", mockValue)))
            coercing.parseLiteral(objectValue)
        }
    }

    @Test
    fun `_Any scalar should throw exception on invalid graphql value`() {
        val coercing = ANY_SCALAR_TYPE.coercing

        assertThrows<CoercingParseLiteralException> {
            coercing.parseLiteral(1)
        }
    }

    @Test
    fun `_Any scalar serialize should just return`() {
        val coercing = ANY_SCALAR_TYPE.coercing

        assertEquals(expected = 1, actual = coercing.serialize(1))
    }

    @Test
    fun `_Any scalar parseValue should just return`() {
        val coercing = ANY_SCALAR_TYPE.coercing

        assertEquals(expected = 1, actual = coercing.parseValue(1))
    }
}
