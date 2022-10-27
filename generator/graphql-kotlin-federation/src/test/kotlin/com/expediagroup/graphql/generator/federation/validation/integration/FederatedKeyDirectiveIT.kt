/*
 * Copyright 2022 Expedia, Inc
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

package com.expediagroup.graphql.generator.federation.validation.integration

import com.expediagroup.graphql.generator.TopLevelObject
import com.expediagroup.graphql.generator.federation.data.integration.key.failure._01.EmptySelectionSetQuery
import com.expediagroup.graphql.generator.federation.data.integration.key.failure._02.NonExistentKeyQuery
import com.expediagroup.graphql.generator.federation.data.integration.key.failure._03.IncompleteSelectionSetQuery
import com.expediagroup.graphql.generator.federation.data.integration.key.failure._04.MalformedSelectionSetQuery
import com.expediagroup.graphql.generator.federation.data.integration.key.failure._05.KeyReferencingListQuery
import com.expediagroup.graphql.generator.federation.data.integration.key.failure._06.KeyReferencingUnionQuery
import com.expediagroup.graphql.generator.federation.data.integration.key.failure._07.KeyReferencingInterfaceQuery
import com.expediagroup.graphql.generator.federation.data.integration.key.failure._08.SelectionSetOnScalarQuery
import com.expediagroup.graphql.generator.federation.data.integration.key.failure._09.MultipleKeysOneInvalidQuery
import com.expediagroup.graphql.generator.federation.data.integration.key.success._1.SimpleKeyQuery
import com.expediagroup.graphql.generator.federation.data.integration.key.success._2.KeyWithMultipleFieldsQuery
import com.expediagroup.graphql.generator.federation.data.integration.key.success._3.KeyWithNestedFieldsQuery
import com.expediagroup.graphql.generator.federation.data.integration.key.success._4.MultipleKeyQuery
import com.expediagroup.graphql.generator.federation.exception.InvalidFederatedSchema
import com.expediagroup.graphql.generator.federation.toFederatedSchema
import graphql.schema.GraphQLSchema
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class FederatedKeyDirectiveIT {

    @Test
    fun `verifies valid simple @key directive`() {
        assertDoesNotThrow {
            val schema = toFederatedSchema(
                config = federatedTestConfig("com.expediagroup.graphql.generator.federation.data.integration.key.success._1"),
                queries = listOf(TopLevelObject(SimpleKeyQuery()))
            )
            validateTypeWasCreatedWithKeyDirective(schema, "SimpleKey")
        }
    }

    @Test
    fun `verifies valid multiple fields @key directive`() {
        assertDoesNotThrow {
            val schema = toFederatedSchema(
                config = federatedTestConfig("com.expediagroup.graphql.generator.federation.data.integration.key.success._2"),
                queries = listOf(TopLevelObject(KeyWithMultipleFieldsQuery()))
            )
            validateTypeWasCreatedWithKeyDirective(schema, "KeyWithMultipleFields")
        }
    }

    @Test
    fun `verifies valid composite key @key directive`() {
        assertDoesNotThrow {
            val schema = toFederatedSchema(
                config = federatedTestConfig("com.expediagroup.graphql.generator.federation.data.integration.key.success._3"),
                queries = listOf(TopLevelObject(KeyWithNestedFieldsQuery()))
            )
            validateTypeWasCreatedWithKeyDirective(schema, "KeyWithNestedFields")
        }
    }

    @Test
    fun `verifies valid multiple @key directive`() {
        assertDoesNotThrow {
            val schema = toFederatedSchema(
                config = federatedTestConfig("com.expediagroup.graphql.generator.federation.data.integration.key.success._4"),
                queries = listOf(TopLevelObject(MultipleKeyQuery()))
            )
            validateTypeWasCreatedWithKeyDirective(schema, "MultipleKeys")
        }
    }

    @Test
    fun `verifies valid @key directive on entity type without query`() {
        assertDoesNotThrow {
            val schema = toFederatedSchema(
                config = federatedTestConfig("com.expediagroup.graphql.generator.federation.data.integration.key.success._5"),
            )
            validateTypeWasCreatedWithKeyDirective(schema, "FederatedKey")
        }
    }

    private fun validateTypeWasCreatedWithKeyDirective(schema: GraphQLSchema, typeName: String) {
        val validatedType = schema.getObjectType(typeName)
        assertNotNull(validatedType)
        assertTrue(validatedType.hasAppliedDirective("key"))
    }

    @Test
    fun `@key directive field set cannot be empty`() {
        val exception = assertFailsWith<InvalidFederatedSchema> {
            toFederatedSchema(
                config = federatedTestConfig("com.expediagroup.graphql.generator.federation.data.integration.key.failure._01"),
                queries = listOf(TopLevelObject(EmptySelectionSetQuery()))
            )
        }
        val expected = "Invalid federated schema:\n - @key directive on EmptySelectionSet is missing field information"
        assertEquals(expected, exception.message)
    }

    @Test
    fun `@key directive needs to reference valid field`() {
        val exception = assertFailsWith<InvalidFederatedSchema> {
            toFederatedSchema(
                config = federatedTestConfig("com.expediagroup.graphql.generator.federation.data.integration.key.failure._02"),
                queries = listOf(TopLevelObject(NonExistentKeyQuery()))
            )
        }
        val expected = "Invalid federated schema:\n - @key(fields = \"id\") directive on NonExistentKey specifies invalid field set - field set specifies field that does not exist, field=id"
        assertEquals(expected, exception.message)
    }

    @Test
    fun `@key directive field set has to specify selection set on an object`() {
        val exception = assertFailsWith<InvalidFederatedSchema> {
            toFederatedSchema(
                config = federatedTestConfig("com.expediagroup.graphql.generator.federation.data.integration.key.failure._03"),
                queries = listOf(TopLevelObject(IncompleteSelectionSetQuery()))
            )
        }
        val expected = "Invalid federated schema:\n" +
            " - @key(fields = \"product\") directive on IncompleteSelectionSet specifies invalid field set - product object does not specify selection set"
        assertEquals(expected, exception.message)
    }

    @Test
    fun `@key directive field set has to valid selection set`() {
        val exception = assertFailsWith<InvalidFederatedSchema> {
            toFederatedSchema(
                config = federatedTestConfig("com.expediagroup.graphql.generator.federation.data.integration.key.failure._04"),
                queries = listOf(TopLevelObject(MalformedSelectionSetQuery()))
            )
        }
        val expected = "Invalid federated schema:\n" +
            " - @key(fields = \"product { id\") directive on MalformedSelectionSet specifies malformed field set: product { id"
        assertEquals(expected, exception.message)
    }

    @Test
    fun `@key directive field set cannot reference list field`() {
        val exception = assertFailsWith<InvalidFederatedSchema> {
            toFederatedSchema(
                config = federatedTestConfig("com.expediagroup.graphql.generator.federation.data.integration.key.failure._05"),
                queries = listOf(TopLevelObject(KeyReferencingListQuery()))
            )
        }
        val expected = "Invalid federated schema:\n" +
            " - @key(fields = \"id\") directive on KeyReferencingList specifies invalid field set - field set references GraphQLList, field=id"
        assertEquals(expected, exception.message)
    }

    @Test
    fun `@key directive field set cannot reference union field`() {
        val exception = assertFailsWith<InvalidFederatedSchema> {
            toFederatedSchema(
                config = federatedTestConfig("com.expediagroup.graphql.generator.federation.data.integration.key.failure._06"),
                queries = listOf(TopLevelObject(KeyReferencingUnionQuery()))
            )
        }
        val expected = "Invalid federated schema:\n" +
            " - @key(fields = \"id\") directive on KeyReferencingUnion specifies invalid field set - field set references GraphQLUnionType, field=id"
        assertEquals(expected, exception.message)
    }

    @Test
    fun `@key directive field set cannot reference interface field`() {
        val exception = assertFailsWith<InvalidFederatedSchema> {
            toFederatedSchema(
                config = federatedTestConfig("com.expediagroup.graphql.generator.federation.data.integration.key.failure._07"),
                queries = listOf(TopLevelObject(KeyReferencingInterfaceQuery()))
            )
        }
        val expected = "Invalid federated schema:\n" +
            " - @key(fields = \"key { id }\") directive on KeyReferencingInterface specifies invalid field set - field set references GraphQLInterfaceType, field=key"
        assertEquals(expected, exception.message)
    }

    @Test
    fun `@key directive field set cannot reference field set on scalar`() {
        val exception = assertFailsWith<InvalidFederatedSchema> {
            toFederatedSchema(
                config = federatedTestConfig("com.expediagroup.graphql.generator.federation.data.integration.key.failure._08"),
                queries = listOf(TopLevelObject(SelectionSetOnScalarQuery()))
            )
        }
        val expected = "Invalid federated schema:\n" +
            " - @key(fields = \"id { uuid }\") directive on SelectionSetOnScalar specifies invalid field set - field set specifies selection set on a leaf node, field=id"
        assertEquals(expected, exception.message)
    }

    @Test
    fun `verifies validation runs on multiple @key directives`() {
        val exception = assertFailsWith<InvalidFederatedSchema> {
            toFederatedSchema(
                config = federatedTestConfig("com.expediagroup.graphql.generator.federation.data.integration.key.failure._09"),
                queries = listOf(TopLevelObject(MultipleKeysOneInvalidQuery()))
            )
        }
        val expected = "Invalid federated schema:\n" +
            " - @key(fields = \"upc\") directive on MultipleKeysOneInvalid specifies invalid field set - field set specifies field that does not exist, field=upc"
        assertEquals(expected, exception.message)
    }
}
