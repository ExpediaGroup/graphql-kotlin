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

package com.expediagroup.graphql.generator.federation.validation.integration

import com.expediagroup.graphql.generator.TopLevelObject
import com.expediagroup.graphql.generator.federation.data.integration.key.failure._2.KeyMissingFieldSelectionQuery
import com.expediagroup.graphql.generator.federation.data.integration.key.failure._3.InvalidKeyQuery
import com.expediagroup.graphql.generator.federation.data.integration.key.failure._4.BaseKeyReferencingExternalFieldsQuery
import com.expediagroup.graphql.generator.federation.data.integration.key.failure._6.KeyReferencingListQuery
import com.expediagroup.graphql.generator.federation.data.integration.key.failure._7.KeyReferencingInterfaceQuery
import com.expediagroup.graphql.generator.federation.data.integration.key.failure._8.KeyReferencingUnionQuery
import com.expediagroup.graphql.generator.federation.data.integration.key.failure._9.NestedKeyReferencingScalarQuery
import com.expediagroup.graphql.generator.federation.data.integration.key.success._1.SimpleKeyQuery
import com.expediagroup.graphql.generator.federation.data.integration.key.success._3.KeyWithMultipleFieldsQuery
import com.expediagroup.graphql.generator.federation.data.integration.key.success._5.KeyWithNestedFieldsQuery
import com.expediagroup.graphql.generator.federation.exception.InvalidFederatedSchema
import com.expediagroup.graphql.generator.federation.toFederatedSchema
import graphql.schema.GraphQLSchema
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull

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
        assertNotNull(validatedType.getDirective("key"))
    }

    @Test
    fun `@extended type should specify @key directive`() {
        val exception = assertFailsWith<InvalidFederatedSchema> {
            toFederatedSchema(config = federatedTestConfig("com.expediagroup.graphql.generator.federation.data.integration.key.failure._1"))
        }
        val expected = "Invalid federated schema:\n - @key directive is missing on federated FederatedMissingKey type"
        assertEquals(expected, exception.message)
    }

    @Test
    fun `@key directive field set cannot be empty`() {
        val exception = assertFailsWith<InvalidFederatedSchema> {
            toFederatedSchema(
                config = federatedTestConfig("com.expediagroup.graphql.generator.federation.data.integration.key.failure._2"),
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
                config = federatedTestConfig("com.expediagroup.graphql.generator.federation.data.integration.key.failure._3"),
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
                config = federatedTestConfig("com.expediagroup.graphql.generator.federation.data.integration.key.failure._4"),
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
            toFederatedSchema(config = federatedTestConfig("com.expediagroup.graphql.generator.federation.data.integration.key.failure._5"))
        }
        val expected = "Invalid federated schema:\n" +
            " - @key(fields = id) directive on ExternalKeyReferencingLocalField specifies invalid field set - extended type incorrectly references local field=id"
        assertEquals(expected, exception.message)
    }

    @Test
    fun `@key directive field set cannot reference list field`() {
        val exception = assertFailsWith<InvalidFederatedSchema> {
            toFederatedSchema(
                config = federatedTestConfig("com.expediagroup.graphql.generator.federation.data.integration.key.failure._6"),
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
                config = federatedTestConfig("com.expediagroup.graphql.generator.federation.data.integration.key.failure._7"),
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
                config = federatedTestConfig("com.expediagroup.graphql.generator.federation.data.integration.key.failure._8"),
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
                config = federatedTestConfig("com.expediagroup.graphql.generator.federation.data.integration.key.failure._9"),
                queries = listOf(TopLevelObject(NestedKeyReferencingScalarQuery()))
            )
        }
        val expected = "Invalid federated schema:\n" +
            " - @key(fields = id { uuid }) directive on NestedKeyReferencingScalar specifies invalid field set - field set defines nested selection set on unsupported type\n" +
            " - @key(fields = id { uuid }) directive on NestedKeyReferencingScalar specifies invalid field set - field set specifies field that does not exist, field=uuid"
        assertEquals(expected, exception.message)
    }
}
