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

import com.expediagroup.graphql.generator.federation.directives.FieldSet
import com.expediagroup.graphql.generator.federation.exception.InvalidFederatedSchema
import graphql.Scalars
import graphql.schema.GraphQLAppliedDirective
import graphql.schema.GraphQLFieldDefinition
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class ValidateDirectiveKtTest {

    @Test
    fun `if directive is not found in directive map, return an error`() {
        val validationErrors = validateDirective(
            validatedType = "",
            targetDirective = "",
            directiveMap = emptyMap(),
            fieldMap = emptyMap()
        )

        assertEquals(expected = 1, actual = validationErrors.size)
    }

    /**
     * @foo
     */
    @Test
    fun `if directive does not have field information, return an error`() {
        val directive: GraphQLAppliedDirective = mockk {
            every { name } returns "foo"
            every { getArgument(eq("fields")) } returns null
        }

        val validationErrors = validateDirective(
            validatedType = "",
            targetDirective = "foo",
            directiveMap = mapOf("foo" to listOf(directive)),
            fieldMap = emptyMap()
        )

        assertEquals(expected = 1, actual = validationErrors.size)
    }

    /**
     * @foo(fields: null)
     */
    @Test
    fun `if directive argument does not have a value, return an error`() {
        val directive: GraphQLAppliedDirective = mockk {
            every { name } returns "foo"
            every { getArgument(eq("fields")) } returns mockk {
                every { argumentValue.value } returns null
            }
        }

        val validationErrors = validateDirective(
            validatedType = "",
            targetDirective = "foo",
            directiveMap = mapOf("foo" to listOf(directive)),
            fieldMap = emptyMap()
        )

        assertEquals(expected = 1, actual = validationErrors.size)
    }

    /**
     * @foo(fields: "hello")
     */
    @Test
    fun `if directive argument value is not a FieldSet, return an error`() {
        val directive: GraphQLAppliedDirective = mockk {
            every { name } returns "foo"
            every { getArgument(eq("fields")) } returns mockk {
                every { argumentValue.value } returns "hello"
            }
        }

        val validationErrors = validateDirective(
            validatedType = "",
            targetDirective = "foo",
            directiveMap = mapOf("foo" to listOf(directive)),
            fieldMap = emptyMap()
        )

        assertEquals(expected = 1, actual = validationErrors.size)
    }

    /**
     * @foo(fields: "")
     */
    @Test
    fun `if directive argument value is FieldSet but with empty string, return an error`() {
        val directive: GraphQLAppliedDirective = mockk {
            every { name } returns "foo"
            every { getArgument(eq("fields")) } returns mockk {
                every { argumentValue.value } returns mockk<FieldSet> {
                    every { value } returns ""
                }
            }
        }

        val validationErrors = validateDirective(
            validatedType = "",
            targetDirective = "foo",
            directiveMap = mapOf("foo" to listOf(directive)),
            fieldMap = emptyMap()
        )

        assertEquals(expected = 1, actual = validationErrors.size)
    }

    /**
     * @foo(fields: " ")
     */
    @Test
    fun `if directive argument value is FieldSet with value that is just spaces, return an error`() {
        val directive: GraphQLAppliedDirective = mockk {
            every { name } returns "foo"
            every { getArgument(eq("fields")) } returns mockk {
                every { argumentValue.value } returns mockk<FieldSet> {
                    every { value } returns " "
                }
            }
        }

        val validationErrors = validateDirective(
            validatedType = "",
            targetDirective = "foo",
            directiveMap = mapOf("foo" to listOf(directive)),
            fieldMap = emptyMap()
        )

        assertEquals(expected = 1, actual = validationErrors.size)
    }

    /**
     * @foo(fields: "bar")
     */
    @Test
    fun `if directive argument value is FieldSet with valid value but validatedType in invalid, return an error`() {
        val directive: GraphQLAppliedDirective = mockk {
            every { name } returns "foo"
            every { getArgument(eq("fields")) } returns mockk {
                every { argumentValue.value } returns mockk<FieldSet> {
                    every { value } returns "bar"
                }
            }
        }

        val validationErrors = validateDirective(
            validatedType = "",
            targetDirective = "foo",
            directiveMap = mapOf("foo" to listOf(directive)),
            fieldMap = emptyMap()
        )

        assertEquals(expected = 1, actual = validationErrors.size)
    }

    /**
     * type Parent @foo(fields: "bar") {
     *   bar: String
     * }
     */
    @Test
    fun `if directive argument value is FieldSet with valid value, no errors are returned`() {
        val directive: GraphQLAppliedDirective = mockk {
            every { name } returns "foo"
            every { getArgument(eq("fields")) } returns mockk {
                every { argumentValue.value } returns mockk<FieldSet> {
                    every { value } returns "bar"
                }
            }
        }

        val graphqlField = GraphQLFieldDefinition.newFieldDefinition()
            .name("bar")
            .type(Scalars.GraphQLString)
            .build()

        val validationErrors = validateDirective(
            validatedType = "MyType",
            targetDirective = "foo",
            directiveMap = mapOf("foo" to listOf(directive)),
            fieldMap = mapOf("bar" to graphqlField)
        )

        assertTrue(validationErrors.isEmpty())
    }

    /**
     * type Parent @foo(fields: "bar baz") {
     *   bar: String
     *   baz: String
     * }
     */
    @Test
    fun `if directive argument value is FieldSet with valid multiple values, no errors are returned`() {
        val directive: GraphQLAppliedDirective = mockk {
            every { name } returns "foo"
            every { getArgument(eq("fields")) } returns mockk {
                every { argumentValue.value } returns mockk<FieldSet> {
                    every { value } returns "bar baz"
                }
            }
        }

        val graphqlField1 = GraphQLFieldDefinition.newFieldDefinition()
            .name("bar")
            .type(Scalars.GraphQLString)
            .build()

        val graphqlField2 = GraphQLFieldDefinition.newFieldDefinition()
            .name("baz")
            .type(Scalars.GraphQLString)
            .build()

        val validationErrors = validateDirective(
            validatedType = "MyType",
            targetDirective = "foo",
            directiveMap = mapOf("foo" to listOf(directive)),
            fieldMap = mapOf("bar" to graphqlField1, "baz" to graphqlField2)
        )

        assertTrue(validationErrors.isEmpty())
    }

    @Test
    fun `if directive specifies malformed field set, throws an error`() {
        val directive: GraphQLAppliedDirective = mockk {
            every { name } returns "foo"
            every { getArgument(eq("fields")) } returns mockk {
                every { argumentValue.value } returns mockk<FieldSet> {
                    every { value } returns "bar } foo {"
                }
            }
        }

        val exception = assertThrows<InvalidFederatedSchema> {
            validateDirective(
                validatedType = "",
                targetDirective = "foo",
                directiveMap = mapOf("foo" to listOf(directive)),
                fieldMap = emptyMap()
            )
        }
        assertEquals(
            expected = "Invalid federated schema:\n - @foo(fields = \"bar } foo {\") directive on  specifies malformed field set: bar } foo {",
            actual = exception.message
        )
    }
}
