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
import com.expediagroup.graphql.generator.federation.data.integration.key.failure._02.KeyMissingFieldSelectionQuery
import com.expediagroup.graphql.generator.federation.data.integration.key.failure._03.InvalidKeyQuery
import com.expediagroup.graphql.generator.federation.data.integration.key.failure._04.BaseKeyReferencingExternalFieldsQuery
import com.expediagroup.graphql.generator.federation.data.integration.key.failure._06.KeyReferencingListQuery
import com.expediagroup.graphql.generator.federation.data.integration.key.failure._07.KeyReferencingInterfaceQuery
import com.expediagroup.graphql.generator.federation.data.integration.key.failure._08.KeyReferencingUnionQuery
import com.expediagroup.graphql.generator.federation.data.integration.key.failure._09.NestedKeyReferencingScalarQuery
import com.expediagroup.graphql.generator.federation.data.integration.key.failure._10.MultipleKeysOneInvalidQuery
import com.expediagroup.graphql.generator.federation.data.integration.key.success._1.SimpleKeyQuery
import com.expediagroup.graphql.generator.federation.data.integration.key.success._3.KeyWithMultipleFieldsQuery
import com.expediagroup.graphql.generator.federation.data.integration.key.success._5.KeyWithNestedFieldsQuery
import com.expediagroup.graphql.generator.federation.data.integration.key.success._7.MultipleKeyQuery
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
    fun `verifies valid simple @key directive on a local type`() {
        assertDoesNotThrow {
            val schema = toFederatedSchema(
                config = federatedTestConfig("com.expediagroup.graphql.generator.federation.data.integration.key.success._1"),
                queries = listOf(TopLevelObject(SimpleKeyQuery()))
            )
            validateTypeWasCreatedWithKeyDirective(schema, "SimpleKey")
        }
    }

    @Test
    fun `verifies valid simple @key directive on a federated type`() {
        assertDoesNotThrow {
            val schema = toFederatedSchema(config = federatedTestConfig("com.expediagroup.graphql.generator.federation.data.integration.key.success._2"))
            validateTypeWasCreatedWithKeyDirective(schema, "SimpleFederatedKey")
        }
    }

    @Test
    fun `verifies @key directive selecting multiple fields on a local type `() {
        assertDoesNotThrow {
            val schema = toFederatedSchema(
                config = federatedTestConfig("com.expediagroup.graphql.generator.federation.data.integration.key.success._3"),
                queries = listOf(TopLevelObject(KeyWithMultipleFieldsQuery()))
            )
            validateTypeWasCreatedWithKeyDirective(schema, "KeyWithMultipleFields")
        }
    }

    @Test
    fun `verifies @key directive selecting multiple fields on a federated type`() {
        assertDoesNotThrow {
            val schema = toFederatedSchema(config = federatedTestConfig("com.expediagroup.graphql.generator.federation.data.integration.key.success._4"))
            validateTypeWasCreatedWithKeyDirective(schema, "FederatedKeyWithMultipleFields")
        }
    }

    @Test
    fun `verifies @key directive with complex field selection on a local type`() {
        assertDoesNotThrow {
            val schema = toFederatedSchema(
                config = federatedTestConfig("com.expediagroup.graphql.generator.federation.data.integration.key.success._5"),
                queries = listOf(TopLevelObject(KeyWithNestedFieldsQuery()))
            )
            validateTypeWasCreatedWithKeyDirective(schema, "KeyWithNestedFields")
        }
    }

    @Test
    fun `verifies @key directive with complex field selection on a federated type`() {
        assertDoesNotThrow {
            val schema = toFederatedSchema(config = federatedTestConfig("com.expediagroup.graphql.generator.federation.data.integration.key.success._6"))
            validateTypeWasCreatedWithKeyDirective(schema, "FederatedKeyWithNestedFields")
        }
    }

    private fun validateTypeWasCreatedWithKeyDirective(schema: GraphQLSchema, typeName: String) {
        val validatedType = schema.getObjectType(typeName)
        assertNotNull(validatedType)
        assertTrue(validatedType.hasAppliedDirective("key"))
    }

    @Test
    fun `@extended type should specify @key directive`() {
        val exception = assertFailsWith<InvalidFederatedSchema> {
            toFederatedSchema(config = federatedTestConfig("com.expediagroup.graphql.generator.federation.data.integration.key.failure._01"))
        }
        val expected = "Invalid federated schema:\n - @key directive is missing on federated FederatedMissingKey type"
        assertEquals(expected, exception.message)
    }

    @Test
    fun `@key directive field set cannot be empty`() {
        val exception = assertFailsWith<InvalidFederatedSchema> {
            toFederatedSchema(
                config = federatedTestConfig("com.expediagroup.graphql.generator.federation.data.integration.key.failure._02"),
                queries = listOf(TopLevelObject(KeyMissingFieldSelectionQuery()))
            )
        }
        val expected = "Invalid federated schema:\n - @key directive on KeyMissingFieldSelection is missing field information"
        assertEquals(expected, exception.message)
    }

    @Test
    fun `@key directive needs to reference valid field`() {
        val exception = assertFailsWith<InvalidFederatedSchema> {
            toFederatedSchema(
                config = federatedTestConfig("com.expediagroup.graphql.generator.federation.data.integration.key.failure._03"),
                queries = listOf(TopLevelObject(InvalidKeyQuery()))
            )
        }
        val expected = "Invalid federated schema:\n - @key(fields = id) directive on InvalidKey specifies invalid field set - field set specifies field that does not exist, field=id"
        assertEquals(expected, exception.message)
    }

    @Test
    fun `@key directive on local type cannot reference external fields`() {
        val exception = assertFailsWith<InvalidFederatedSchema> {
            toFederatedSchema(
                config = federatedTestConfig("com.expediagroup.graphql.generator.federation.data.integration.key.failure._04"),
                queries = listOf(TopLevelObject(BaseKeyReferencingExternalFieldsQuery()))
            )
        }
        val expected = "Invalid federated schema:\n" +
            " - @key(fields = id) directive on BaseKeyReferencingExternalFields specifies invalid field set - type incorrectly references external field=id\n" +
            " - base BaseKeyReferencingExternalFields type has fields marked with @external directive, fields=[id]"
        assertEquals(expected, exception.message)
    }

    @Test
    fun `@extended type @key directive cannot reference local fields`() {
        val exception = assertFailsWith<InvalidFederatedSchema> {
            toFederatedSchema(config = federatedTestConfig("com.expediagroup.graphql.generator.federation.data.integration.key.failure._05"))
        }
        val expected = "Invalid federated schema:\n" +
            " - @key(fields = id) directive on ExternalKeyReferencingLocalField specifies invalid field set - extended type incorrectly references local field=id"
        assertEquals(expected, exception.message)
    }

    @Test
    fun `@key directive field set cannot reference list field`() {
        val exception = assertFailsWith<InvalidFederatedSchema> {
            toFederatedSchema(
                config = federatedTestConfig("com.expediagroup.graphql.generator.federation.data.integration.key.failure._06"),
                queries = listOf(TopLevelObject(KeyReferencingListQuery()))
            )
        }
        val expected = "Invalid federated schema:\n" +
            " - @key(fields = id) directive on KeyReferencingList specifies invalid field set - field set references GraphQLList, field=id"
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
            " - @key(fields = id) directive on KeyReferencingInterface specifies invalid field set - field set references GraphQLInterfaceType, field=id"
        assertEquals(expected, exception.message)
    }

    @Test
    fun `@key directive field set cannot reference union field`() {
        val exception = assertFailsWith<InvalidFederatedSchema> {
            toFederatedSchema(
                config = federatedTestConfig("com.expediagroup.graphql.generator.federation.data.integration.key.failure._08"),
                queries = listOf(TopLevelObject(KeyReferencingUnionQuery()))
            )
        }
        val expected = "Invalid federated schema:\n" +
            " - @key(fields = id) directive on KeyReferencingUnion specifies invalid field set - field set references GraphQLUnionType, field=id"
        assertEquals(expected, exception.message)
    }

    @Test
    fun `@key directive field set cannot reference scalar complex key`() {
        val exception = assertFailsWith<InvalidFederatedSchema> {
            toFederatedSchema(
                config = federatedTestConfig("com.expediagroup.graphql.generator.federation.data.integration.key.failure._09"),
                queries = listOf(TopLevelObject(NestedKeyReferencingScalarQuery()))
            )
        }
        val expected = "Invalid federated schema:\n" +
            " - @key(fields = id { uuid }) directive on NestedKeyReferencingScalar specifies invalid field set - field set defines nested selection set on unsupported type\n" +
            " - @key(fields = id { uuid }) directive on NestedKeyReferencingScalar specifies invalid field set - field set specifies field that does not exist, field=uuid"
        assertEquals(expected, exception.message)
    }

    @Test
    fun `verifies multiple @key directive on a local type`() {
        assertDoesNotThrow {
            val schema = toFederatedSchema(
                config = federatedTestConfig("com.expediagroup.graphql.generator.federation.data.integration.key.success._7"),
                queries = listOf(TopLevelObject(MultipleKeyQuery()))
            )
            val validatedType = schema.getObjectType("MultipleKeys")
            assertNotNull(validatedType)
            val keyDirectives = validatedType.getAppliedDirectives("key")
            assertTrue(keyDirectives.isNotEmpty())
            assertEquals(2, keyDirectives.size)
        }
    }

    @Test
    fun `verifies validation runs on multiple @key directives`() {
        val exception = assertFailsWith<InvalidFederatedSchema> {
            toFederatedSchema(
                config = federatedTestConfig("com.expediagroup.graphql.generator.federation.data.integration.key.failure._10"),
                queries = listOf(TopLevelObject(MultipleKeysOneInvalidQuery()))
            )
        }
        val expected = "Invalid federated schema:\n" +
            " - @key(fields = upc) directive on MultipleKeysOneInvalid specifies invalid field set - field set specifies field that does not exist, field=upc"
        assertEquals(expected, exception.message)
    }
}
