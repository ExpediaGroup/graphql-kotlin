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

package com.expediagroup.graphql.generator.federation.validation

import com.expediagroup.graphql.generator.federation.directives.EXTENDS_DIRECTIVE_TYPE
import com.expediagroup.graphql.generator.federation.directives.KEY_DIRECTIVE_NAME
import com.expediagroup.graphql.generator.federation.directives.KEY_DIRECTIVE_TYPE_V2
import com.expediagroup.graphql.generator.federation.directives.PROVIDES_DIRECTIVE_NAME
import com.expediagroup.graphql.generator.federation.directives.REQUIRES_DIRECTIVE_NAME
import com.expediagroup.graphql.generator.federation.exception.InvalidFederatedSchema
import com.expediagroup.graphql.generator.federation.externalDirective
import com.expediagroup.graphql.generator.federation.getKeyDirective
import com.expediagroup.graphql.generator.federation.types.FIELD_SET_ARGUMENT
import graphql.Scalars.GraphQLString
import graphql.schema.GraphQLAppliedDirective
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
            .withAppliedDirective(
                KEY_DIRECTIVE_TYPE_V2.toAppliedDirective()
                    .transform { directive ->
                        directive.argument(
                            FIELD_SET_ARGUMENT.toAppliedArgument()
                                .transform { argument ->
                                    argument.valueProgrammatic("foo")
                                }
                        )
                    }
            )
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
        val keyDirectiveType = GraphQLAppliedDirective.newDirective()
            .name(KEY_DIRECTIVE_NAME)

        val field = GraphQLFieldDefinition.newFieldDefinition()
            .name("bar")
            .type(GraphQLString)

        val typeToValidate = GraphQLObjectType.newObject()
            .name("Foo")
            .field(field)
            .withAppliedDirective(keyDirectiveType)
            .build()

        val result = kotlin.runCatching {
            FederatedSchemaValidator().validateGraphQLType(typeToValidate)
        }

        val expectedError =
            """
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
        val typeToValidate = GraphQLObjectType.newObject()
            .name("Foo")
            .field(
                GraphQLFieldDefinition.newFieldDefinition()
                    .name("id")
                    .type(GraphQLString)
                    .withAppliedDirective(externalDirective)
            )
            .field(
                GraphQLFieldDefinition.newFieldDefinition()
                    .name("bar")
                    .type(GraphQLString)
                    .withAppliedDirective(
                        GraphQLAppliedDirective.newDirective().name(PROVIDES_DIRECTIVE_NAME).build()
                    )
            )
            .withAppliedDirective(getKeyDirective("id"))
            .withAppliedDirective(EXTENDS_DIRECTIVE_TYPE.toAppliedDirective())
            .build()

        val result = kotlin.runCatching {
            FederatedSchemaValidator().validateGraphQLType(typeToValidate)
        }

        val expectedError =
            """
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
        val typeToValidate = GraphQLObjectType.newObject()
            .name("Foo")
            .field(
                GraphQLFieldDefinition.newFieldDefinition()
                    .name("id")
                    .type(GraphQLString)
                    .withAppliedDirective(externalDirective)
            )
            .field(
                GraphQLFieldDefinition.newFieldDefinition()
                    .name("bar")
                    .type(GraphQLString)
                    .withAppliedDirective(
                        GraphQLAppliedDirective.newDirective().name(REQUIRES_DIRECTIVE_NAME)
                    )
            )
            .withAppliedDirective(getKeyDirective("id"))
            .withAppliedDirective(EXTENDS_DIRECTIVE_TYPE.toAppliedDirective())
            .build()

        val result = kotlin.runCatching {
            FederatedSchemaValidator().validateGraphQLType(typeToValidate)
        }

        val expectedError =
            """
                Invalid federated schema:
                 - @requires directive on Foo.bar is missing field information
            """.trimIndent()

        assertEquals(expectedError, result.exceptionOrNull()?.message)
    }
}
