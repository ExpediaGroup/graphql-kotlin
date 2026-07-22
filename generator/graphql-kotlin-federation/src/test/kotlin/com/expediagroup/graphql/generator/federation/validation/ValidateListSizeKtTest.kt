/*
 * Copyright 2026 Expedia, Inc
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

import com.expediagroup.graphql.generator.federation.directives.LIST_SIZE_DIRECTIVE_NAME
import graphql.Scalars
import graphql.schema.GraphQLAppliedDirective
import graphql.schema.GraphQLArgument
import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLList
import graphql.schema.GraphQLNonNull
import graphql.schema.GraphQLObjectType
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class ValidateListSizeKtTest {

    private fun listSizeDirective(slicingArguments: List<String>? = null, sizedFields: List<String>? = null): GraphQLAppliedDirective =
        mockk {
            every { name } returns LIST_SIZE_DIRECTIVE_NAME
            every { getArgument("slicingArguments") } returns slicingArguments?.let { value -> mockk { every { argumentValue.value } returns value } }
            every { getArgument("sizedFields") } returns sizedFields?.let { value -> mockk { every { argumentValue.value } returns value } }
        }

    /**
     * foo: String
     */
    @Test
    fun `field without @listSize applied returns no errors`() {
        val field = GraphQLFieldDefinition.newFieldDefinition()
            .name("foo")
            .type(Scalars.GraphQLString)
            .build()

        assertTrue(validateListSize("MyType", field).isEmpty())
    }

    /**
     * items(first: Int): [String!] @listSize(slicingArguments: ["first"])
     */
    @Test
    fun `slicingArguments referencing an existing argument returns no errors`() {
        val field = GraphQLFieldDefinition.newFieldDefinition()
            .name("items")
            .type(GraphQLList.list(Scalars.GraphQLString))
            .argument(GraphQLArgument.newArgument().name("first").type(Scalars.GraphQLInt))
            .withAppliedDirective(listSizeDirective(slicingArguments = listOf("first")))
            .build()

        assertTrue(validateListSize("MyType", field).isEmpty())
    }

    /**
     * items(first: Int): [String!] @listSize(slicingArguments: ["last"])
     */
    @Test
    fun `slicingArguments referencing an unknown argument returns an error`() {
        val field = GraphQLFieldDefinition.newFieldDefinition()
            .name("items")
            .type(GraphQLList.list(Scalars.GraphQLString))
            .argument(GraphQLArgument.newArgument().name("first").type(Scalars.GraphQLInt))
            .withAppliedDirective(listSizeDirective(slicingArguments = listOf("last")))
            .build()

        val errors = validateListSize("MyType", field)
        assertEquals(expected = 1, actual = errors.size)
        assertEquals("@listSize directive on MyType.items references unknown argument: last", errors[0])
    }

    /**
     * items(first: Int): Cursor! @listSize(sizedFields: ["page"])
     * type Cursor { page: [String!]! }
     */
    @Test
    fun `sizedFields referencing an existing field on the return type returns no errors`() {
        val cursorType = GraphQLObjectType.newObject()
            .name("Cursor")
            .field(
                GraphQLFieldDefinition.newFieldDefinition().name("page").type(GraphQLList.list(Scalars.GraphQLString))
            )
            .build()
        val field = GraphQLFieldDefinition.newFieldDefinition()
            .name("items")
            .type(GraphQLNonNull.nonNull(cursorType))
            .withAppliedDirective(listSizeDirective(sizedFields = listOf("page")))
            .build()

        assertTrue(validateListSize("MyType", field).isEmpty())
    }

    /**
     * items: Cursor! @listSize(sizedFields: ["edges"])
     * type Cursor { page: [String!]! }
     */
    @Test
    fun `sizedFields referencing an unknown field on the return type returns an error`() {
        val cursorType = GraphQLObjectType.newObject()
            .name("Cursor")
            .field(
                GraphQLFieldDefinition.newFieldDefinition().name("page").type(GraphQLList.list(Scalars.GraphQLString))
            )
            .build()
        val field = GraphQLFieldDefinition.newFieldDefinition()
            .name("items")
            .type(GraphQLNonNull.nonNull(cursorType))
            .withAppliedDirective(listSizeDirective(sizedFields = listOf("edges")))
            .build()

        val errors = validateListSize("MyType", field)
        assertEquals(expected = 1, actual = errors.size)
        assertEquals("@listSize directive on MyType.items references unknown field: edges", errors[0])
    }

    /**
     * items: String! @listSize(sizedFields: ["page"])
     * A return type that has no fields at all (e.g. a scalar): every named sizedField is unknown.
     */
    @Test
    fun `sizedFields on a field returning a type without fields returns an error`() {
        val field = GraphQLFieldDefinition.newFieldDefinition()
            .name("items")
            .type(Scalars.GraphQLString)
            .withAppliedDirective(listSizeDirective(sizedFields = listOf("page")))
            .build()

        val errors = validateListSize("MyType", field)
        assertEquals(expected = 1, actual = errors.size)
        assertEquals("@listSize directive on MyType.items references unknown field: page", errors[0])
    }

    /**
     * items(first: Int): Cursor! @listSize(slicingArguments: ["last"], sizedFields: ["edges"])
     * type Cursor { page: [String!]! }
     * Verifies both slicingArguments and sizedFields are validated together.
     */
    @Test
    fun `both invalid slicingArguments and sizedFields are reported`() {
        val cursorType = GraphQLObjectType.newObject()
            .name("Cursor")
            .field(
                GraphQLFieldDefinition.newFieldDefinition().name("page").type(GraphQLList.list(Scalars.GraphQLString))
            )
            .build()
        val field = GraphQLFieldDefinition.newFieldDefinition()
            .name("items")
            .type(GraphQLNonNull.nonNull(cursorType))
            .argument(GraphQLArgument.newArgument().name("first").type(Scalars.GraphQLInt))
            .withAppliedDirective(listSizeDirective(slicingArguments = listOf("last"), sizedFields = listOf("edges")))
            .build()

        val errors = validateListSize("MyType", field)
        assertEquals(expected = 2, actual = errors.size)
        assertTrue(errors.any { it.contains("references unknown argument: last") })
        assertTrue(errors.any { it.contains("references unknown field: edges") })
    }
}
