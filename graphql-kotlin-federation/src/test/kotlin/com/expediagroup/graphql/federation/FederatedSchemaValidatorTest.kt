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

package com.expediagroup.graphql.federation

import com.expediagroup.graphql.federation.directives.FieldSet
import com.expediagroup.graphql.federation.directives.extendsDirectiveType
import com.expediagroup.graphql.federation.exception.InvalidFederatedSchema
import graphql.Scalars.GraphQLString
import graphql.schema.GraphQLDirective
import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLInterfaceType
import graphql.schema.GraphQLObjectType
import io.mockk.every
import io.mockk.mockk
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
            .name("key")

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
     * type Foo @key(fields: "id") {
     *   id: String
     *   bar: String @provides
     * }
     */
    @Test
    fun `validate federated GraphQLObjectType with valid key directive and scalar field with provides directive`() {
        val keyDirective: GraphQLDirective = mockk {
            every { name } returns "key"
            every { getArgument(eq("fields")) } returns mockk {
                every { value } returns mockk<FieldSet> {
                    every { value } returns "id"
                }
            }
        }
        val id = GraphQLFieldDefinition.newFieldDefinition()
            .name("id")
            .type(GraphQLString)
        val directive = GraphQLDirective.newDirective().name("provides")
        val field = GraphQLFieldDefinition.newFieldDefinition()
            .name("bar")
            .withDirective(directive)
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
             - @provides directive is specified on a Foo.bar field but it does not return an object type
        """.trimIndent()

        assertEquals(expectedError, result.exceptionOrNull()?.message)
    }

    /**
     * type Foo @key(fields: "id") {
     *   id: String
     *   bar: String @requires
     * }
     */
    @Test
    fun `validate federated GraphQLObjectType with valid key directive and field with requires directive`() {
        val keyDirective: GraphQLDirective = mockk {
            every { name } returns "key"
            every { getArgument(eq("fields")) } returns mockk {
                every { value } returns mockk<FieldSet> {
                    every { value } returns "id"
                }
            }
        }
        val id = GraphQLFieldDefinition.newFieldDefinition()
            .name("id")
            .type(GraphQLString)
        val directive = GraphQLDirective.newDirective().name("requires")
        val field = GraphQLFieldDefinition.newFieldDefinition()
            .name("bar")
            .withDirective(directive)
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
             - base Foo type has fields marked with @requires directive, validatedField=bar
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
    fun `validate federated GraphQLObjectType with valid key directive and field with external directive`() {
        val keyDirective: GraphQLDirective = mockk {
            every { name } returns "key"
            every { getArgument(eq("fields")) } returns mockk {
                every { value } returns mockk<FieldSet> {
                    every { value } returns "id"
                }
            }
        }
        val id = GraphQLFieldDefinition.newFieldDefinition()
            .name("id")
            .type(GraphQLString)
        val directive = GraphQLDirective.newDirective().name("external")
        val field = GraphQLFieldDefinition.newFieldDefinition()
            .name("bar")
            .withDirective(directive)
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
