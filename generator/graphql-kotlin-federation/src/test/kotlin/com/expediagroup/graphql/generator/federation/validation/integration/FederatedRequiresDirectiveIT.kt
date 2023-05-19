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

package com.expediagroup.graphql.generator.federation.validation.integration

import com.expediagroup.graphql.generator.federation.directives.EXTERNAL_DIRECTIVE_NAME
import com.expediagroup.graphql.generator.federation.directives.KEY_DIRECTIVE_NAME
import com.expediagroup.graphql.generator.federation.directives.REQUIRES_DIRECTIVE_NAME
import com.expediagroup.graphql.generator.federation.exception.InvalidFederatedSchema
import com.expediagroup.graphql.generator.federation.toFederatedSchema
import graphql.schema.GraphQLObjectType
import graphql.schema.GraphQLTypeUtil
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class FederatedRequiresDirectiveIT {

    @Test
    fun `verifies @requires directive`() {
        assertDoesNotThrow {
            val schema = toFederatedSchema(config = federatedTestConfig("com.expediagroup.graphql.generator.federation.data.integration.requires.success._1"))
            val validatedType = schema.getObjectType("SimpleRequires")
            assertTrue(validatedType.hasAppliedDirective(KEY_DIRECTIVE_NAME))
            val weightField = validatedType.getFieldDefinition("weight")
            assertNotNull(weightField)
            assertTrue(weightField.hasAppliedDirective(EXTERNAL_DIRECTIVE_NAME))
            val requiresField = validatedType.getFieldDefinition("shippingCost")
            assertNotNull(requiresField)
            assertTrue(requiresField.hasAppliedDirective(REQUIRES_DIRECTIVE_NAME))
        }
    }

    @Test
    fun `verifies @requires directive can be applied on a list field`() {
        assertDoesNotThrow {
            val schema = toFederatedSchema(config = federatedTestConfig("com.expediagroup.graphql.generator.federation.data.integration.requires.success._2"))
            val validatedType = schema.getObjectType("RequiresSelectionOnList")
            assertTrue(validatedType.hasAppliedDirective(KEY_DIRECTIVE_NAME))
            val externalField = validatedType.getFieldDefinition("email")
            assertNotNull(externalField)
            assertTrue(externalField.hasAppliedDirective(EXTERNAL_DIRECTIVE_NAME))
            val requiresField = validatedType.getFieldDefinition("reviews")
            assertNotNull(requiresField)
            assertTrue(requiresField.hasAppliedDirective(REQUIRES_DIRECTIVE_NAME))
        }
    }

    @Test
    fun `verifies @requires directive can be applied on an interface field`() {
        assertDoesNotThrow {
            val schema = toFederatedSchema(config = federatedTestConfig("com.expediagroup.graphql.generator.federation.data.integration.requires.success._3"))
            val validatedType = schema.getObjectType("RequiresSelectionOnInterface")
            assertTrue(validatedType.hasAppliedDirective(KEY_DIRECTIVE_NAME))
            val externalField = validatedType.getFieldDefinition("email")
            assertNotNull(externalField)
            assertTrue(externalField.hasAppliedDirective(EXTERNAL_DIRECTIVE_NAME))
            val requiresField = validatedType.getFieldDefinition("review")
            assertNotNull(requiresField)
            assertTrue(requiresField.hasAppliedDirective(REQUIRES_DIRECTIVE_NAME))
        }
    }

    @Test
    fun `verifies @requires directive can be applied on a union field`() {
        assertDoesNotThrow {
            val schema = toFederatedSchema(config = federatedTestConfig("com.expediagroup.graphql.generator.federation.data.integration.requires.success._4"))
            val validatedType = schema.getObjectType("RequiresSelectionOnUnion")
            assertTrue(validatedType.hasAppliedDirective(KEY_DIRECTIVE_NAME))
            val externalField = validatedType.getFieldDefinition("email")
            assertNotNull(externalField)
            assertTrue(externalField.hasAppliedDirective(EXTERNAL_DIRECTIVE_NAME))
            val requiresField = validatedType.getFieldDefinition("review")
            assertNotNull(requiresField)
            assertTrue(requiresField.hasAppliedDirective(REQUIRES_DIRECTIVE_NAME))
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

    @Test
    fun `verifies @requires needs @external leaf fields only`() {
        assertDoesNotThrow {
            val schema = toFederatedSchema(config = federatedTestConfig("com.expediagroup.graphql.generator.federation.data.integration.requires.success._5"))
            val validatedType = schema.getObjectType("LeafRequires")
            assertTrue(validatedType.hasAppliedDirective(KEY_DIRECTIVE_NAME))
            val localType = validatedType.getFieldDefinition("complexType")?.type
            assertNotNull(localType)
            val unwrappedType = GraphQLTypeUtil.unwrapAll(localType) as GraphQLObjectType
            assertFalse(unwrappedType.hasAppliedDirective(EXTERNAL_DIRECTIVE_NAME))
            val externalField = unwrappedType.getField("weight")
            assertTrue(externalField.hasAppliedDirective(EXTERNAL_DIRECTIVE_NAME))

            val requiresField = validatedType.getFieldDefinition("shippingCost")
            assertNotNull(requiresField)
            assertTrue(requiresField.hasAppliedDirective(REQUIRES_DIRECTIVE_NAME))
        }
    }

    @Test
    fun `verifies @external is recursively applied for @requires selection set`() {
        assertDoesNotThrow {
            val schema = toFederatedSchema(config = federatedTestConfig("com.expediagroup.graphql.generator.federation.data.integration.requires.success._6"))
            val validatedType = schema.getObjectType("RecursiveExternalRequires")
            assertTrue(validatedType.hasAppliedDirective(KEY_DIRECTIVE_NAME))
            val externalField = validatedType.getFieldDefinition("complexType")
            assertNotNull(externalField)
            assertTrue(externalField.hasAppliedDirective(EXTERNAL_DIRECTIVE_NAME))

            val unwrappedType = GraphQLTypeUtil.unwrapAll(externalField.type) as GraphQLObjectType
            assertFalse(unwrappedType.hasAppliedDirective(EXTERNAL_DIRECTIVE_NAME))
            val leafField = unwrappedType.getField("weight")
            assertFalse(leafField.hasAppliedDirective(EXTERNAL_DIRECTIVE_NAME))

            val requiresField = validatedType.getFieldDefinition("shippingCost")
            assertNotNull(requiresField)
            assertTrue(requiresField.hasAppliedDirective(REQUIRES_DIRECTIVE_NAME))
        }
    }

    @Test
    fun `verifies @external is applied on all fields within external type`() {
        assertDoesNotThrow {
            val schema = toFederatedSchema(config = federatedTestConfig("com.expediagroup.graphql.generator.federation.data.integration.requires.success._7"))
            val validatedType = schema.getObjectType("ExternalRequiresType")
            assertTrue(validatedType.hasAppliedDirective(KEY_DIRECTIVE_NAME))
            val externalField = validatedType.getFieldDefinition("externalType")
            assertNotNull(externalField)
            assertFalse(externalField.hasAppliedDirective(EXTERNAL_DIRECTIVE_NAME))

            val unwrappedType = GraphQLTypeUtil.unwrapAll(externalField.type) as GraphQLObjectType
            assertTrue(unwrappedType.hasAppliedDirective(EXTERNAL_DIRECTIVE_NAME))
            val leafField = unwrappedType.getField("weight")
            assertFalse(leafField.hasAppliedDirective(EXTERNAL_DIRECTIVE_NAME))

            val requiresField = validatedType.getFieldDefinition("shippingCost")
            assertNotNull(requiresField)
            assertTrue(requiresField.hasAppliedDirective(REQUIRES_DIRECTIVE_NAME))
        }
    }
}
