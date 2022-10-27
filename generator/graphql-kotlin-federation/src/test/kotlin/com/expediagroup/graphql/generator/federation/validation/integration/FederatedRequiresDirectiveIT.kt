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

import com.expediagroup.graphql.generator.federation.exception.InvalidFederatedSchema
import com.expediagroup.graphql.generator.federation.toFederatedSchema
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class FederatedRequiresDirectiveIT {

    @Test
    fun `verifies @requires directive`() {
        assertDoesNotThrow {
            val schema = toFederatedSchema(config = federatedTestConfig("com.expediagroup.graphql.generator.federation.data.integration.requires.success._1"))
            val validatedType = schema.getObjectType("SimpleRequires")
            assertTrue(validatedType.hasAppliedDirective("key"))
            val weightField = validatedType.getFieldDefinition("weight")
            assertNotNull(weightField)
            assertNotNull(weightField.hasAppliedDirective("external"))
            val requiresField = validatedType.getFieldDefinition("shippingCost")
            assertNotNull(requiresField)
            assertNotNull(requiresField.hasAppliedDirective("requires"))
        }
    }

    @Test
    fun `verifies @requires directive can be applied on a list field`() {
        assertDoesNotThrow {
            val schema = toFederatedSchema(config = federatedTestConfig("com.expediagroup.graphql.generator.federation.data.integration.requires.success._2"))
            val validatedType = schema.getObjectType("RequiresSelectionOnList")
            assertTrue(validatedType.hasAppliedDirective("key"))
            val externalField = validatedType.getFieldDefinition("email")
            assertNotNull(externalField)
            assertNotNull(externalField.hasAppliedDirective("external"))
            val requiresField = validatedType.getFieldDefinition("reviews")
            assertNotNull(requiresField)
            assertNotNull(requiresField.hasAppliedDirective("requires"))
        }
    }

    @Test
    fun `verifies @requires directive can be applied on an interface field`() {
        assertDoesNotThrow {
            val schema = toFederatedSchema(config = federatedTestConfig("com.expediagroup.graphql.generator.federation.data.integration.requires.success._3"))
            val validatedType = schema.getObjectType("RequiresSelectionOnInterface")
            assertTrue(validatedType.hasAppliedDirective("key"))
            val externalField = validatedType.getFieldDefinition("email")
            assertNotNull(externalField)
            assertNotNull(externalField.hasAppliedDirective("external"))
            val requiresField = validatedType.getFieldDefinition("review")
            assertNotNull(requiresField)
            assertNotNull(requiresField.hasAppliedDirective("requires"))
        }
    }

    @Test
    fun `verifies @requires directive can be applied on a union field`() {
        assertDoesNotThrow {
            val schema = toFederatedSchema(config = federatedTestConfig("com.expediagroup.graphql.generator.federation.data.integration.requires.success._4"))
            val validatedType = schema.getObjectType("RequiresSelectionOnUnion")
            assertTrue(validatedType.hasAppliedDirective("key"))
            val externalField = validatedType.getFieldDefinition("email")
            assertNotNull(externalField)
            assertNotNull(externalField.hasAppliedDirective("external"))
            val requiresField = validatedType.getFieldDefinition("review")
            assertNotNull(requiresField)
            assertNotNull(requiresField.hasAppliedDirective("requires"))
        }
    }

    @Test
    fun `@requires directive cannot reference local fields`() {
        val exception = assertFailsWith<InvalidFederatedSchema> {
            toFederatedSchema(config = federatedTestConfig("com.expediagroup.graphql.generator.federation.data.integration.requires.failure._1"))
        }
        val expected =
            """
                Invalid federated schema:
                 - @requires(fields = "weight") directive on RequiresLocalField.shippingCost specifies invalid field set - @requires should reference only @external fields, field=weight
            """.trimIndent()
        assertEquals(expected, exception.message)
    }

    @Test
    fun `@requires directive should reference valid fields`() {
        val exception = assertFailsWith<InvalidFederatedSchema> {
            toFederatedSchema(config = federatedTestConfig("com.expediagroup.graphql.generator.federation.data.integration.requires.failure._2"))
        }
        val expected =
            """
                Invalid federated schema:
                 - @requires(fields = "zipCode") directive on RequiresNonExistentField.shippingCost specifies invalid field set - field set specifies field that does not exist, field=zipCode
            """.trimIndent()
        assertEquals(expected, exception.message)
    }
}
