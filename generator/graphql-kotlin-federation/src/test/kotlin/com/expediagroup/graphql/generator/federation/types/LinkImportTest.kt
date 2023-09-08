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

import com.expediagroup.graphql.generator.federation.directives.LinkImport
import com.expediagroup.graphql.generator.federation.exception.CoercingValueToLiteralException
import graphql.GraphQLContext
import graphql.execution.CoercedVariables
import graphql.language.IntValue
import graphql.language.ObjectField
import graphql.language.ObjectValue
import graphql.language.StringValue
import graphql.schema.Coercing
import graphql.schema.CoercingParseLiteralException
import graphql.schema.CoercingParseValueException
import graphql.schema.CoercingSerializeException
import org.junit.jupiter.api.Test
import java.math.BigInteger
import java.util.Locale
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class LinkImportTest {
    private val coercing: Coercing<*, *> = LINK_IMPORT_SCALAR_TYPE.coercing

    @Test
    fun `serialize should throw exception when not a LinkImport`() {
        assertFailsWith<CoercingSerializeException> {
            coercing.serialize(StringValue("hello"), GraphQLContext.getDefault(), Locale.ENGLISH)
        }
    }

    @Test
    fun `serialize should return the value when LinkImport`() {
        val result = coercing.serialize(LinkImport(name = "@foo"), GraphQLContext.getDefault(), Locale.ENGLISH)
        assertEquals(expected = "@foo", actual = result)
    }

    @Test
    fun `serialize should return the value when LinkImport with renames`() {
        val result = coercing.serialize(LinkImport(name = "@foo", `as` = "@bar"), GraphQLContext.getDefault(), Locale.ENGLISH)
        assertTrue(result is Map<*, *>)
        assertEquals(expected = "@foo", actual = result["name"])
        assertEquals(expected = "@bar", actual = result["as"])
    }

    @Test
    fun `parseValue should be able to parse StringValue`() {
        val result = coercing.parseValue(StringValue("@foo"), GraphQLContext.getDefault(), Locale.ENGLISH)
        assertTrue(result is LinkImport)
        assertEquals("@foo", result.name)
    }

    @Test
    fun `parseValue should be able to parse ObjectValue`() {
        val objectValue = ObjectValue(
            listOf(
                ObjectField("name", StringValue("@foo")),
                ObjectField("as", StringValue("@bar"))
            )
        )
        val result = coercing.parseValue(objectValue, GraphQLContext.getDefault(), Locale.ENGLISH)
        assertTrue(result is LinkImport)
        assertEquals("@foo", result.name)
        assertEquals("@bar", result.`as`)
    }

    @Test
    fun `parseValue should throw exception on unhandled value`() {
        assertFailsWith<CoercingParseValueException> {
            coercing.parseValue(IntValue(BigInteger.ONE), GraphQLContext.getDefault(), Locale.ENGLISH)
        }
    }

    @Test
    fun `parseValue should throw exception on unknown ObjectValue`() {
        assertFailsWith<CoercingParseValueException> {
            coercing.parseValue(ObjectValue(listOf(ObjectField("foo", StringValue("FOO")))), GraphQLContext.getDefault(), Locale.ENGLISH)
        }
    }

    @Test
    fun `parseLiteral should map StringValue to a LinkImport`() {
        val result = coercing.parseLiteral(StringValue("@foo"), CoercedVariables.emptyVariables(), GraphQLContext.getDefault(), Locale.ENGLISH)
        assertTrue(result is LinkImport)
        assertEquals("@foo", result.name)
    }

    @Test
    fun `parseLiteral should be able to parse ObjectValue`() {
        val objectValue = ObjectValue(
            listOf(
                ObjectField("name", StringValue("@foo")),
                ObjectField("as", StringValue("@bar"))
            )
        )
        val result = coercing.parseLiteral(objectValue, CoercedVariables.emptyVariables(), GraphQLContext.getDefault(), Locale.ENGLISH)
        assertTrue(result is LinkImport)
        assertEquals("@foo", result.name)
        assertEquals("@bar", result.`as`)
    }

    @Test
    fun `parseLiteral should throw exception on unhandled value`() {
        assertFailsWith<CoercingParseLiteralException> {
            coercing.parseLiteral(IntValue(BigInteger.ONE), CoercedVariables.emptyVariables(), GraphQLContext.getDefault(), Locale.ENGLISH)
        }
    }

    @Test
    fun `parseLiteral should throw exception on unknown ObjectValue`() {
        assertFailsWith<CoercingParseLiteralException> {
            coercing.parseLiteral(ObjectValue(listOf(ObjectField("foo", StringValue("FOO")))), CoercedVariables.emptyVariables(), GraphQLContext.getDefault(), Locale.ENGLISH)
        }
    }

    @Test
    fun `valueToLiteral should map simple string import to StringValue`() {
        val result = coercing.valueToLiteral("@foo", GraphQLContext.getDefault(), Locale.ENGLISH)
        assertTrue(result is StringValue)
        assertEquals("@foo", result.value)
    }

    @Test
    fun `valueToLiteral should map simple LinkImport to StringValue`() {
        val result = coercing.valueToLiteral(LinkImport(name = "@foo"), GraphQLContext.getDefault(), Locale.ENGLISH)
        assertTrue(result is StringValue)
        assertEquals("@foo", result.value)
    }

    @Test
    fun `valueToLiteral should map LinkImport to ObjectValue`() {
        val result = coercing.valueToLiteral(LinkImport(name = "@foo", `as` = "@bar"), GraphQLContext.getDefault(), Locale.ENGLISH)
        assertTrue(result is ObjectValue)
        val nameFieldValue = result.objectFields.find { it.name == "name" }?.value as? StringValue
        val namespaceFieldValue = result.objectFields.find { it.name == "as" }?.value as? StringValue
        assertEquals("@foo", nameFieldValue?.value)
        assertEquals("@bar", namespaceFieldValue?.value)
    }

    @Test
    fun `valueToLiteral should throw exception on unhandled value`() {
        assertFailsWith<CoercingValueToLiteralException> {
            coercing.valueToLiteral(123, GraphQLContext.getDefault(), Locale.ENGLISH)
        }
    }
}
