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
import com.expediagroup.graphql.generator.federation.data.integration.provides.failure._1.ProvidesNonExistentFieldQuery
import com.expediagroup.graphql.generator.federation.data.integration.provides.failure._2.ProvidesNotObjectQuery
import com.expediagroup.graphql.generator.federation.data.integration.provides.success._1.ProvidesSingleFieldQuery
import com.expediagroup.graphql.generator.federation.data.integration.provides.success._2.ProvidesMultipleFieldsQuery
import com.expediagroup.graphql.generator.federation.data.integration.provides.success._3.ProvidesOnAListQuery
import com.expediagroup.graphql.generator.federation.data.integration.provides.success._4.ProvidesListQuery
import com.expediagroup.graphql.generator.federation.data.integration.provides.success._5.ProvidesInterfaceQuery
import com.expediagroup.graphql.generator.federation.exception.InvalidFederatedSchema
import com.expediagroup.graphql.generator.federation.toFederatedSchema
import graphql.schema.GraphQLObjectType
import graphql.schema.GraphQLSchema
import graphql.schema.GraphQLTypeUtil
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class FederatedProvidesDirectiveIT {

    @Test
    fun `verifies @provides directive`() {
        assertDoesNotThrow {
            val schema = toFederatedSchema(
                config = federatedTestConfig("com.expediagroup.graphql.generator.federation.data.integration.provides.success._1"),
                queries = listOf(TopLevelObject(ProvidesSingleFieldQuery()))
            )
            validateTypeWasCreatedWithProvidesDirective(schema, "ProvidesSingleField")
        }
    }

    @Test
    fun `verifies @provides directive can specify multiple fields`() {
        assertDoesNotThrow {
            val schema = toFederatedSchema(
                config = federatedTestConfig("com.expediagroup.graphql.generator.federation.data.integration.provides.success._2"),
                queries = listOf(TopLevelObject(ProvidesMultipleFieldsQuery()))
            )
            validateTypeWasCreatedWithProvidesDirective(schema, "ProvidesMultipleFields")
        }
    }

    @Test
    fun `verifies @provides directive can be applied on a list field`() {
        assertDoesNotThrow {
            val schema = toFederatedSchema(
                config = federatedTestConfig("com.expediagroup.graphql.generator.federation.data.integration.provides.success._3"),
                queries = listOf(TopLevelObject(ProvidesOnAListQuery()))
            )
            validateTypeWasCreatedWithProvidesDirective(schema, "ProvidesOnAList")
        }
    }

    @Test
    fun `verifies @provides directive can reference list field`() {
        assertDoesNotThrow {
            val schema = toFederatedSchema(
                config = federatedTestConfig("com.expediagroup.graphql.generator.federation.data.integration.provides.success._4"),
                queries = listOf(TopLevelObject(ProvidesListQuery()))
            )
            validateTypeWasCreatedWithProvidesDirective(schema, "ProvidesList")
        }
    }

    @Test
    fun `verifies @provides directive can reference interfaces`() {
        assertDoesNotThrow {
            val schema = toFederatedSchema(
                config = federatedTestConfig("com.expediagroup.graphql.generator.federation.data.integration.provides.success._5"),
                queries = listOf(TopLevelObject(ProvidesInterfaceQuery()))
            )
            validateTypeWasCreatedWithProvidesDirective(schema, "ProvidesInterface")
        }
    }

    private fun validateTypeWasCreatedWithProvidesDirective(schema: GraphQLSchema, typeName: String) {
        val validatedType = schema.getObjectType(typeName)
        assertNotNull(validatedType)
        assertTrue(validatedType.hasAppliedDirective("key"))
        val providedField = validatedType.getFieldDefinition("provided")
        assertNotNull(providedField)
        assertNotNull(providedField.getAppliedDirective("provides"))
        val providedType = GraphQLTypeUtil.unwrapAll(providedField.type) as? GraphQLObjectType
        assertNotNull(providedType)
    }

    @Test
    fun `@provides field set should reference valid fields`() {
        val exception = assertFailsWith<InvalidFederatedSchema> {
            toFederatedSchema(
                config = federatedTestConfig("com.expediagroup.graphql.generator.federation.data.integration.provides.failure._1"),
                queries = listOf(TopLevelObject(ProvidesNonExistentFieldQuery()))
            )
        }
        val expected = "Invalid federated schema:" +
            "\n - @provides(fields = \"description\") directive on ProvidesNonExistentField.provided specifies invalid field set - field set specifies field that does not exist, field=description"
        assertEquals(expected, exception.message)
    }

    @Test
    fun `@provides field set should reference objects`() {
        val exception = assertFailsWith<InvalidFederatedSchema> {
            toFederatedSchema(
                config = federatedTestConfig("com.expediagroup.graphql.generator.federation.data.integration.provides.failure._2"),
                queries = listOf(TopLevelObject(ProvidesNotObjectQuery()))
            )
        }
        val expected = "Invalid federated schema:\n - @provides directive is specified on a ProvidesNotObject.provided field but it does not return an object type"
        assertEquals(expected, exception.message)
    }
}
