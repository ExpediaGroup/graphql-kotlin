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

package com.expediagroup.graphql.federation.validation

import com.expediagroup.graphql.federation.directives.KEY_DIRECTIVE_NAME
import com.expediagroup.graphql.federation.directives.PROVIDES_DIRECTIVE_NAME
import com.expediagroup.graphql.federation.directives.REQUIRES_DIRECTIVE_NAME
import com.expediagroup.graphql.federation.directives.extendsDirectiveType
import com.expediagroup.graphql.federation.exception.InvalidFederatedSchema
import com.expediagroup.graphql.federation.externalDirective
import com.expediagroup.graphql.federation.getKeyDirective
import graphql.Scalars.GraphQLString
import graphql.schema.GraphQLDirective
import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLInterfaceType
import graphql.schema.GraphQLObjectType
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class FederatedSchemaValidatorTest {

    /**
     * type Foo {}
     */
    @Test
    fun `validate GraphQLObjectType but not federated should do nothing`() {
        val typeToValidate = GraphQLObjectType.newObject()
            .name("Foo")
            .build()

        assertDoesNotThrow {
            FederatedSchemaValidator().validateGraphQLType(typeToValidate)
        }
    }

    /**
     * interface Foo {
     *   bar: String
     * }
     */
    @Test
    fun `validate GraphQLInterfaceType but not federated should do nothing`() {
        val typeToValidate = GraphQLInterfaceType.newInterface()
            .name("Foo")
            .field(GraphQLFieldDefinition.newFieldDefinition().name("bar").type(GraphQLString))
            .build()

        assertDoesNotThrow {
            FederatedSchemaValidator().validateGraphQLType(typeToValidate)
        }
    }

    /**
     * type Foo @extends {}
     */
    @Test
    fun `validate federated GraphQLObjectType with no fields`() {
        val typeToValidate = GraphQLObjectType.newObject()
            .name("Foo")
            .withDirective(extendsDirectiveType)
            .build()

        assertFailsWith<InvalidFederatedSchema> {
            FederatedSchemaValidator().validateGraphQLType(typeToValidate)
        }
    }

    /**
     * type Foo @extends {
     *   bar: String
     * }
     */
    @Test
    fun `validate federated GraphQLObjectType with no key directive`() {
        val field = GraphQLFieldDefinition.newFieldDefinition()
            .name("bar")
            .type(GraphQLString)

        val typeToValidate = GraphQLObjectType.newObject()
            .name("Foo")
            .field(field)
            .withDirective(extendsDirectiveType)
            .build()

        assertFailsWith<InvalidFederatedSchema> {
            FederatedSchemaValidator().validateGraphQLType(typeToValidate)
        }
    }

    /**
     * type Foo @key {
     *   bar: String
     * }
     */
    @Test
    fun `validate federated GraphQLObjectType with empty key directive`() {
        val keyDirectiveType = GraphQLDirective.newDirective()
            .name(KEY_DIRECTIVE_NAME)

        val field = GraphQLFieldDefinition.newFieldDefinition()
            .name("bar")
            .type(GraphQLString)

        val typeToValidate = GraphQLObjectType.newObject()
            .name("Foo")
            .field(field)
            .withDirective(keyDirectiveType)
            .build()

        val result = kotlin.runCatching {
            FederatedSchemaValidator().validateGraphQLType(typeToValidate)
        }

        val expectedError = """
            Invalid federated schema:
             - @key directive on Foo is missing field information
        """.trimIndent()

        assertEquals(expectedError, result.exceptionOrNull()?.message)
    }

    /**
     * type Foo @key(fields: "id") @extends {
     *   id: String @external
     *   bar: String @provides
     * }
     */
    @Test
    fun `validate federated GraphQLObjectType with valid key directive and scalar field with invalid provides directive`() {
        val keyDirective: GraphQLDirective = getKeyDirective("id")
        val id = GraphQLFieldDefinition.newFieldDefinition()
            .name("id")
            .type(GraphQLString)
            .withDirective(externalDirective)

        val directive = GraphQLDirective.newDirective().name(PROVIDES_DIRECTIVE_NAME)
        val field = GraphQLFieldDefinition.newFieldDefinition()
            .name("bar")
            .withDirective(directive)
            .type(GraphQLString)

        val typeToValidate = GraphQLObjectType.newObject()
            .name("Foo")
            .field(field)
            .field(id)
            .withDirective(keyDirective)
            .withDirective(extendsDirectiveType)
            .build()

        val result = kotlin.runCatching {
            FederatedSchemaValidator().validateGraphQLType(typeToValidate)
        }

        val expectedError = """
            Invalid federated schema:
             - @provides directive is specified on a Foo.bar field but it does not return an object type
        """.trimIndent()

        assertEquals(expectedError, result.exceptionOrNull()?.message)
    }

    /**
     * type Foo @key(fields: "id") @extends {
     *   id: String @external
     *   bar: String @requires
     * }
     */
    @Test
    fun `validate federated GraphQLObjectType with valid key directive and field with invalid requires directive`() {
        val keyDirective: GraphQLDirective = getKeyDirective("id")
        val id = GraphQLFieldDefinition.newFieldDefinition()
            .name("id")
            .type(GraphQLString)
            .withDirective(externalDirective)

        val directive = GraphQLDirective.newDirective().name(REQUIRES_DIRECTIVE_NAME)
        val field = GraphQLFieldDefinition.newFieldDefinition()
            .name("bar")
            .withDirective(directive)
            .type(GraphQLString)

        val typeToValidate = GraphQLObjectType.newObject()
            .name("Foo")
            .field(field)
            .field(id)
            .withDirective(keyDirective)
            .withDirective(extendsDirectiveType)
            .build()

        val result = kotlin.runCatching {
            FederatedSchemaValidator().validateGraphQLType(typeToValidate)
        }

        val expectedError = """
            Invalid federated schema:
             - @requires directive on Foo.bar is missing field information
        """.trimIndent()

        assertEquals(expectedError, result.exceptionOrNull()?.message)
    }

    /**
     * type Foo @key(fields: "id") {
     *   id: String
     *   bar: String @external
     * }
     */
    @Test
    fun `validate local GraphQLObjectType with valid key directive and field with invalid external directive`() {
        val keyDirective: GraphQLDirective = getKeyDirective("id")
        val id = GraphQLFieldDefinition.newFieldDefinition()
            .name("id")
            .type(GraphQLString)
        val field = GraphQLFieldDefinition.newFieldDefinition()
            .name("bar")
            .withDirective(externalDirective)
            .type(GraphQLString)

        val typeToValidate = GraphQLObjectType.newObject()
            .name("Foo")
            .field(field)
            .field(id)
            .withDirective(keyDirective)
            .build()

        val result = kotlin.runCatching {
            FederatedSchemaValidator().validateGraphQLType(typeToValidate)
        }

        val expectedError = """
            Invalid federated schema:
             - base Foo type has fields marked with @external directive, fields=[bar]
        """.trimIndent()

        assertEquals(expectedError, result.exceptionOrNull()?.message)
    }
}
