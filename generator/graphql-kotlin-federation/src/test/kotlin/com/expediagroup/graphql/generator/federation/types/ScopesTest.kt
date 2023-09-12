/*
 * Copyright 2023 Expedia, Inc
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

import com.expediagroup.graphql.generator.federation.directives.Scope
import graphql.GraphQLContext
import graphql.execution.CoercedVariables
import graphql.language.IntValue
import graphql.language.StringValue
import graphql.schema.Coercing
import graphql.schema.CoercingParseLiteralException
import graphql.schema.CoercingSerializeException
import org.junit.jupiter.api.Test
import java.math.BigInteger
import java.util.Locale
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class ScopesTest {
    private val coercing: Coercing<*, *> = SCOPE_SCALAR_TYPE.coercing

    @Test
    fun `serialize should throw exception when not a Scope`() {
        assertFailsWith<CoercingSerializeException> {
            coercing.serialize(StringValue("hello"), GraphQLContext.getDefault(), Locale.ENGLISH)
        }
    }

    @Test
    fun `serialize should return the value from Scope`() {
        val result = coercing.serialize(Scope("1"), GraphQLContext.getDefault(), Locale.ENGLISH)
        assertEquals(expected = "1", actual = result)
    }

    @Test
    fun `parseValue should parse StringValue`() {
        val result = coercing.parseValue(StringValue("scope"), GraphQLContext.getDefault(), Locale.ENGLISH)
        assertTrue(result is Scope)
    }

    @Test
    fun `parseValue should throw exception on non-StringValue`() {
        assertFailsWith<CoercingParseLiteralException> {
            coercing.parseValue(IntValue(BigInteger.ONE), GraphQLContext.getDefault(), Locale.ENGLISH)
        }
    }

    @Test
    fun `parseLiteral should map StringValue to a Scope`() {
        val result = coercing.parseLiteral(StringValue("scope"), CoercedVariables.emptyVariables(), GraphQLContext.getDefault(), Locale.ENGLISH)
        assertTrue(result is Scope)
    }

    @Test
    fun `parseLiteral should throw exception on non-StringValue`() {
        assertFailsWith<CoercingParseLiteralException> {
            coercing.parseLiteral(IntValue(BigInteger.ONE), CoercedVariables.emptyVariables(), GraphQLContext.getDefault(), Locale.ENGLISH)
        }
    }
}
