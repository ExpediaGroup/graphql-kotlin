/*
 * Copyright 2020 Expedia, Inc
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

package com.expediagroup.graphql.federation.validation.integration

import com.expediagroup.graphql.TopLevelObject
import com.expediagroup.graphql.federation.data.integration.requires.failure._3.RequiresOnLocalTypeQuery
import com.expediagroup.graphql.federation.exception.InvalidFederatedSchema
import com.expediagroup.graphql.federation.toFederatedSchema
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull

class FederatedRequiresDirectiveIT {

    @Test
    fun `verifies @requires directive`() {
        assertDoesNotThrow {
            val schema = toFederatedSchema(config = federatedTestConfig("com.expediagroup.graphql.federation.data.integration.requires.success._1"))
            val validatedType = schema.getObjectType("SimpleRequires")
            assertNotNull(validatedType.getDirective("key"))
            val weightField = validatedType.getFieldDefinition("weight")
            assertNotNull(weightField)
            assertNotNull(weightField.getDirective("external"))
            val requiresField = validatedType.getFieldDefinition("shippingCost")
            assertNotNull(requiresField)
            assertNotNull(requiresField.getDirective("requires"))
        }
    }

    @Test
    fun `@requires directive cannot reference local fields`() {
        val exception = assertFailsWith<InvalidFederatedSchema> {
            toFederatedSchema(config = federatedTestConfig("com.expediagroup.graphql.federation.data.integration.requires.failure._1"))
        }
        assertEquals("Invalid federated schema:\n" +
            " - @requires(fields = weight) directive on RequiresLocalField.shippingCost specifies invalid field set - extended type incorrectly references local field=weight", exception.message)
    }

    @Test
    fun `@requires directive should reference valid fields`() {
        val exception = assertFailsWith<InvalidFederatedSchema> {
            toFederatedSchema(config = federatedTestConfig("com.expediagroup.graphql.federation.data.integration.requires.failure._2"))
        }
        assertEquals("Invalid federated schema:\n" +
            " - @requires(fields = zipCode) directive on RequiresNonExistentField.shippingCost specifies invalid field set - field set specifies fields that do not exist", exception.message)
    }

    @Test
    fun `@requires directive cannot be applied on local type`() {
        val exception = assertFailsWith<InvalidFederatedSchema> {
            toFederatedSchema(
                config = federatedTestConfig("com.expediagroup.graphql.federation.data.integration.requires.failure._3"),
                queries = listOf(TopLevelObject(RequiresOnLocalTypeQuery())))
        }
        assertEquals("Invalid federated schema:\n" +
            " - base RequiresOnLocalType type has fields marked with @requires directive, validatedField=shippingCost", exception.message)
    }

    @Test
    fun `verifies @requires directive on list does not throw`() {
        assertDoesNotThrow {
            val schema = toFederatedSchema(config = federatedTestConfig("com.expediagroup.graphql.federation.data.integration.requires.success._2"))
            val validatedType = schema.getObjectType("User")
            assertNotNull(validatedType.getDirective("key"))
            val externalField = validatedType.getFieldDefinition("email")
            assertNotNull(externalField)
            assertNotNull(externalField.getDirective("external"))
            val requiresField = validatedType.getFieldDefinition("reviews")
            assertNotNull(requiresField)
            assertNotNull(requiresField.getDirective("requires"))
        }
    }

    @Test
    fun `verifies @requires directive on interface does not throw`() {
        assertDoesNotThrow {
            val schema = toFederatedSchema(config = federatedTestConfig("com.expediagroup.graphql.federation.data.integration.requires.success._3"))
            val validatedType = schema.getObjectType("User")
            assertNotNull(validatedType.getDirective("key"))
            val externalField = validatedType.getFieldDefinition("email")
            assertNotNull(externalField)
            assertNotNull(externalField.getDirective("external"))
            val requiresField = validatedType.getFieldDefinition("reviews")
            assertNotNull(requiresField)
            assertNotNull(requiresField.getDirective("requires"))
        }
    }

    @Test
    fun `verifies @requires directive on union does not throw`() {
        assertDoesNotThrow {
            val schema = toFederatedSchema(config = federatedTestConfig("com.expediagroup.graphql.federation.data.integration.requires.success._4"))
            val validatedType = schema.getObjectType("User")
            assertNotNull(validatedType.getDirective("key"))
            val externalField = validatedType.getFieldDefinition("email")
            assertNotNull(externalField)
            assertNotNull(externalField.getDirective("external"))
            val requiresField = validatedType.getFieldDefinition("reviews")
            assertNotNull(requiresField)
            assertNotNull(requiresField.getDirective("requires"))
        }
    }
}
