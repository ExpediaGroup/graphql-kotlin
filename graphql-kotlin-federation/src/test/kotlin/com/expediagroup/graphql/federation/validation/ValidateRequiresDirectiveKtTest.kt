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

import com.expediagroup.graphql.federation.directives.FieldSet
import graphql.Scalars.GraphQLFloat
import graphql.Scalars.GraphQLString
import graphql.schema.GraphQLDirective
import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLObjectType
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class ValidateRequiresDirectiveKtTest {

    private fun requiresDirective(directiveValue: String = "weight"): GraphQLDirective = mockk {
        every { name } returns "requires"
        every { getArgument(eq("fields")) } returns mockk {
            every { value } returns mockk<FieldSet> {
                every { value } returns directiveValue
            }
        }
    }

    private val weight = GraphQLFieldDefinition.newFieldDefinition()
        .name("weight")
        .type(GraphQLFloat)
        .build()

    /**
     * type Foo {
     *   shippingCost: String
     * }
     */
    @Test
    fun `Verify non extended types and non requries fields returns an error`() {
        val shippingCost = GraphQLFieldDefinition.newFieldDefinition()
            .name("shippingCost")
            .type(GraphQLString)
            .build()

        val errors = validateRequiresDirective(
            validatedType = "Foo",
            fieldMap = emptyMap(),
            validatedField = shippingCost,
            extendedType = false)

        assertEquals(1, errors.size)
        assertEquals("base Foo type has fields marked with @requires directive, validatedField=shippingCost", errors.first())
    }

    /**
     * type Foo {
     *   shippingCost: String @requires(fields: "weight")
     * }
     */
    @Test
    fun `Verify non extended types return an error`() {
        val shippingCost = GraphQLFieldDefinition.newFieldDefinition()
            .name("shippingCost")
            .type(GraphQLString)
            .withDirective(requiresDirective())
            .build()

        val errors = validateRequiresDirective(
            validatedType = "Foo",
            fieldMap = emptyMap(),
            validatedField = shippingCost,
            extendedType = false)

        assertEquals(1, errors.size)
        assertEquals("base Foo type has fields marked with @requires directive, validatedField=shippingCost", errors.first())
    }

    /**
     * type Foo @extends {
     *   shippingCost: String @requires(fields: "bar")
     *   weight: Float
     * }
     */
    @Test
    fun `Verify valid requires directive, but invalid field set selection`() {
        val shippingCost = GraphQLFieldDefinition.newFieldDefinition()
            .name("shippingCost")
            .type(GraphQLString)
            .withDirective(requiresDirective("bar"))
            .build()

        val validatedType = GraphQLObjectType.newObject()
            .name("Foo")
            .field(shippingCost)
            .field(weight)
            .build()

        val errors = validateRequiresDirective(
            validatedType = validatedType.name,
            fieldMap = validatedType.fieldDefinitions.map { it.name to it }.toMap(),
            validatedField = shippingCost,
            extendedType = true)

        assertEquals(1, errors.size)
        assertEquals("@requires(fields = bar) directive on Foo.shippingCost specifies invalid field set - field set specifies fields that do not exist", errors.first())
    }

    /**
     * type Foo @extends {
     *   shippingCost: String @requires(fields: "weight")
     *   weight: Float
     * }
     */
    @Test
    fun `Verify valid requires directive and but selected field is not external`() {
        val shippingCost = GraphQLFieldDefinition.newFieldDefinition()
            .name("shippingCost")
            .type(GraphQLString)
            .withDirective(requiresDirective())
            .build()

        val validatedType = GraphQLObjectType.newObject()
            .name("Foo")
            .field(shippingCost)
            .field(weight)
            .build()

        val errors = validateRequiresDirective(
            validatedType = validatedType.name,
            fieldMap = validatedType.fieldDefinitions.map { it.name to it }.toMap(),
            validatedField = shippingCost,
            extendedType = true)

        assertEquals(1, errors.size)
        assertEquals("@requires(fields = weight) directive on Foo.shippingCost specifies invalid field set - extended type incorrectly references local field=weight", errors.first())
    }

    /**
     * type Foo @extends {
     *   shippingCost: String @requires(fields: "weight")
     *   weight: Float @external
     * }
     */
    @Test
    fun `Verify valid requires directive and valid field selection`() {
        val shippingCost = GraphQLFieldDefinition.newFieldDefinition()
            .name("shippingCost")
            .type(GraphQLString)
            .withDirective(requiresDirective())
            .build()

        val modifiedWeight = GraphQLFieldDefinition.newFieldDefinition(weight)
            .withDirective(GraphQLDirective.newDirective().name("external"))

        val validatedType = GraphQLObjectType.newObject()
            .name("Foo")
            .field(shippingCost)
            .field(modifiedWeight)
            .build()

        val errors = validateRequiresDirective(
            validatedType = validatedType.name,
            fieldMap = validatedType.fieldDefinitions.map { it.name to it }.toMap(),
            validatedField = shippingCost,
            extendedType = true)

        assertEquals(0, errors.size)
    }
}
