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
import com.expediagroup.graphql.generator.federation.data.integration.provides.failure._1.ProvidesLocalTypeQuery
import com.expediagroup.graphql.generator.federation.data.integration.provides.failure._2.ProvidesLocalFieldQuery
import com.expediagroup.graphql.generator.federation.data.integration.provides.failure._3.ProvidesInterfaceQuery
import com.expediagroup.graphql.generator.federation.data.integration.provides.failure._4.ProvidesListFieldQuery
import com.expediagroup.graphql.generator.federation.data.integration.provides.failure._5.ProvidesInterfaceFieldQuery
import com.expediagroup.graphql.generator.federation.data.integration.provides.success._1.SimpleProvidesQuery
import com.expediagroup.graphql.generator.federation.data.integration.provides.success._2.ProvidesListQuery
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
                queries = listOf(TopLevelObject(SimpleProvidesQuery()))
            )
            validateTypeWasCreatedWithProvidesDirective(schema, "SimpleProvides")
        }
    }

    @Test
    fun `verifies @provides directive can specify list of fields`() {
        assertDoesNotThrow {
            val schema = toFederatedSchema(
                config = federatedTestConfig("com.expediagroup.graphql.generator.federation.data.integration.provides.success._2"),
                queries = listOf(TopLevelObject(ProvidesListQuery()))
            )
            validateTypeWasCreatedWithProvidesDirective(schema, "ProvidesList")
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
        assertNotNull(providedType.getAppliedDirective("extends"))
    }

    @Test
    fun `@provides field set cannot reference local objects`() {
        val exception = assertFailsWith<InvalidFederatedSchema> {
            toFederatedSchema(
                config = federatedTestConfig("com.expediagroup.graphql.generator.federation.data.integration.provides.failure._1"),
                queries = listOf(TopLevelObject(ProvidesLocalTypeQuery()))
            )
        }
        val expected = "Invalid federated schema:\n - @provides directive is specified on a ProvidesLocalType.providedLocal field references local object"
        assertEquals(expected, exception.message)
    }

    @Test
    fun `@provides field set cannot reference local fields on extended types`() {
        val exception = assertFailsWith<InvalidFederatedSchema> {
            toFederatedSchema(
                config = federatedTestConfig("com.expediagroup.graphql.generator.federation.data.integration.provides.failure._2"),
                queries = listOf(TopLevelObject(ProvidesLocalFieldQuery()))
            )
        }
        val expected = "Invalid federated schema:\n" +
            " - @provides(fields = text) directive on ProvidesLocalField.provided specifies invalid field set - extended type incorrectly references local field=text"
        assertEquals(expected, exception.message)
    }

    @Test
    fun `@provides field set cannot reference fields from interface`() {
        val exception = assertFailsWith<InvalidFederatedSchema> {
            toFederatedSchema(
                config = federatedTestConfig("com.expediagroup.graphql.generator.federation.data.integration.provides.failure._3"),
                queries = listOf(TopLevelObject(ProvidesInterfaceQuery()))
            )
        }
        val expected = "Invalid federated schema:\n" +
            " - @provides directive is specified on a ProvidesInterface.providedInterface field but it does not return an object type"
        assertEquals(expected, exception.message)
    }

    @Test
    fun `@provides field set cannot reference fields returning lists`() {
        val exception = assertFailsWith<InvalidFederatedSchema> {
            toFederatedSchema(
                config = federatedTestConfig("com.expediagroup.graphql.generator.federation.data.integration.provides.failure._4"),
                queries = listOf(TopLevelObject(ProvidesListFieldQuery()))
            )
        }
        val expected = "Invalid federated schema:\n" +
            " - @provides(fields = text) directive on ProvidesListField.provided specifies invalid field set - field set references GraphQLList, field=text"
        assertEquals(expected, exception.message)
    }

    @Test
    fun `@provides field set cannot reference fields returning interface`() {
        val exception = assertFailsWith<InvalidFederatedSchema> {
            toFederatedSchema(
                config = federatedTestConfig("com.expediagroup.graphql.generator.federation.data.integration.provides.failure._5"),
                queries = listOf(TopLevelObject(ProvidesInterfaceFieldQuery()))
            )
        }
        val expected = "Invalid federated schema:\n" +
            " - @provides(fields = data) directive on ProvidesInterfaceField.provided specifies invalid field set - field set references GraphQLInterfaceType, field=data"
        assertEquals(expected, exception.message)
    }
}
