/*
 * Copyright 2019 Expedia, Inc
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

import com.expediagroup.graphql.federation.directives.FieldSet
import graphql.Scalars
import graphql.schema.GraphQLDirective
import graphql.schema.GraphQLFieldDefinition
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class ValidateDirectiveKtTest {

    @Test
    fun `if directive is not found in directive map, return an error`() {
        val validationErrors = validateDirective(
            validatedType = "",
            targetDirective = "",
            directives = emptyMap(),
            fieldMap = emptyMap(),
            extendedType = false
        )

        assertEquals(expected = 1, actual = validationErrors.size)
    }

    @Test
    fun `if directive does not have field information, return an error`() {
        val directive: GraphQLDirective = mockk {
            every { getArgument(eq("fields")) } returns null
        }

        val validationErrors = validateDirective(
            validatedType = "",
            targetDirective = "foo",
            directives = mapOf("foo" to directive),
            fieldMap = emptyMap(),
            extendedType = false
        )

        assertEquals(expected = 1, actual = validationErrors.size)
    }

    @Test
    fun `if directive argument does not have a value, return an error`() {
        val directive: GraphQLDirective = mockk {
            every { getArgument(eq("fields")) } returns mockk {
                every { value } returns null
            }
        }

        val validationErrors = validateDirective(
            validatedType = "",
            targetDirective = "foo",
            directives = mapOf("foo" to directive),
            fieldMap = emptyMap(),
            extendedType = false
        )

        assertEquals(expected = 1, actual = validationErrors.size)
    }

    @Test
    fun `if directive argument value is not a FieldSet, return an error`() {
        val directive: GraphQLDirective = mockk {
            every { getArgument(eq("fields")) } returns mockk {
                every { value } returns "hello"
            }
        }

        val validationErrors = validateDirective(
            validatedType = "",
            targetDirective = "foo",
            directives = mapOf("foo" to directive),
            fieldMap = emptyMap(),
            extendedType = false
        )

        assertEquals(expected = 1, actual = validationErrors.size)
    }

    @Test
    fun `if directive argument value is FieldSet but with empty string, return an error`() {
        val directive: GraphQLDirective = mockk {
            every { getArgument(eq("fields")) } returns mockk {
                every { value } returns mockk<FieldSet> {
                    every { value } returns ""
                }
            }
        }

        val validationErrors = validateDirective(
            validatedType = "",
            targetDirective = "foo",
            directives = mapOf("foo" to directive),
            fieldMap = emptyMap(),
            extendedType = false
        )

        assertEquals(expected = 1, actual = validationErrors.size)
    }

    @Test
    fun `if directive argument value is FieldSet with value that is just spaces, return an error`() {
        val directive: GraphQLDirective = mockk {
            every { getArgument(eq("fields")) } returns mockk {
                every { value } returns mockk<FieldSet> {
                    every { value } returns " "
                }
            }
        }

        val validationErrors = validateDirective(
            validatedType = "",
            targetDirective = "foo",
            directives = mapOf("foo" to directive),
            fieldMap = emptyMap(),
            extendedType = false
        )

        assertEquals(expected = 1, actual = validationErrors.size)
    }

    @Test
    fun `if directive argument value is FieldSet with valid value but validatedType in invalid, return an error`() {
        val directive: GraphQLDirective = mockk {
            every { getArgument(eq("fields")) } returns mockk {
                every { value } returns mockk<FieldSet> {
                    every { value } returns "bar"
                }
            }
        }

        val validationErrors = validateDirective(
            validatedType = "",
            targetDirective = "foo",
            directives = mapOf("foo" to directive),
            fieldMap = emptyMap(),
            extendedType = false
        )

        assertEquals(expected = 1, actual = validationErrors.size)
    }

    @Test
    fun `if directive argument value is FieldSet with valid value, no errors are returned`() {
        val directive: GraphQLDirective = mockk {
            every { getArgument(eq("fields")) } returns mockk {
                every { value } returns mockk<FieldSet> {
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
            directives = mapOf("foo" to directive),
            fieldMap = mapOf("bar" to graphqlField),
            extendedType = false
        )

        assertTrue(validationErrors.isEmpty())
    }
}
