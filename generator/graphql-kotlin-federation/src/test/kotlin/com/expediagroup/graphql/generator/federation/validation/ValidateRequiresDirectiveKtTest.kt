/*
 * Copyright 2021 Expedia, Inc
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

import com.expediagroup.graphql.generator.federation.externalDirective
import com.expediagroup.graphql.generator.federation.getKeyDirective
import com.expediagroup.graphql.generator.federation.getRequiresDirective
import graphql.Scalars.GraphQLFloat
import graphql.Scalars.GraphQLString
import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLObjectType
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class ValidateRequiresDirectiveKtTest {

    private val weight = GraphQLFieldDefinition.newFieldDefinition()
        .name("weight")
        .type(GraphQLFloat)
        .build()

    private val idExternalField = GraphQLFieldDefinition.newFieldDefinition()
        .name("id")
        .type(GraphQLString)
        .withDirective(externalDirective)
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
            extendedType = false
        )

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
            .withDirective(getRequiresDirective("weight"))
            .build()

        val errors = validateRequiresDirective(
            validatedType = "Foo",
            fieldMap = emptyMap(),
            validatedField = shippingCost,
            extendedType = false
        )

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
            .withDirective(getRequiresDirective("bar"))
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
            extendedType = true
        )

        assertEquals(1, errors.size)
        assertEquals("@requires(fields = bar) directive on Foo.shippingCost specifies invalid field set - field set specifies field that does not exist, field=bar", errors.first())
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
            .withDirective(getRequiresDirective("weight"))
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
            extendedType = true
        )

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
            .withDirective(getRequiresDirective("weight"))
            .build()

        val modifiedWeight = GraphQLFieldDefinition.newFieldDefinition(weight)
            .withDirective(externalDirective)

        val validatedType = GraphQLObjectType.newObject()
            .name("Foo")
            .field(shippingCost)
            .field(modifiedWeight)
            .build()

        val errors = validateRequiresDirective(
            validatedType = validatedType.name,
            fieldMap = validatedType.fieldDefinitions.map { it.name to it }.toMap(),
            validatedField = shippingCost,
            extendedType = true
        )

        assertEquals(0, errors.size)
    }

    /**
     * type Foo @extends {
     *   shippingCost: String @requires(fields: "bar { foo }")
     *   bar: Bar @external
     * }
     *
     * type Bar @extends @key(fields = "weight") {
     *   weight: Float
     * }
     */
    @Test
    fun `Verify valid requires directive, but invalid nested field set selection`() {
        val shippingCost = GraphQLFieldDefinition.newFieldDefinition()
            .name("shippingCost")
            .type(GraphQLString)
            .withDirective(getRequiresDirective("bar { foo }"))
            .build()

        val barObject = GraphQLObjectType.newObject()
            .name("Bar")
            .field(weight)
            .withDirective(getKeyDirective("weight"))
            .build()

        val barField = GraphQLFieldDefinition.newFieldDefinition()
            .name("bar")
            .type(barObject)
            .withDirective(externalDirective)
            .build()

        val validatedType = GraphQLObjectType.newObject()
            .name("Foo")
            .field(shippingCost)
            .field(barField)
            .build()

        val errors = validateRequiresDirective(
            validatedType = validatedType.name,
            fieldMap = validatedType.fieldDefinitions.map { it.name to it }.toMap(),
            validatedField = shippingCost,
            extendedType = true
        )

        assertEquals(1, errors.size)
        assertEquals("@requires(fields = bar { foo }) directive on Foo.shippingCost specifies invalid field set - field set specifies field that does not exist, field=foo", errors.first())
    }

    /**
     * type Foo @extends @key(fields = "id") {
     *   id: String @external
     *   bar: Bar @external
     *   shippingCost: String @requires(fields: "bar { weight }")
     * }
     *
     * type Bar @extends @key(fields = "weight") {
     *   weight: Float @external
     * }
     */
    @Test
    fun `Verify valid requires directive with valid nested field set selection`() {
        val shippingCostField = GraphQLFieldDefinition.newFieldDefinition()
            .name("shippingCost")
            .type(GraphQLString)
            .withDirective(getRequiresDirective("bar { weight }"))
            .build()

        val externalWeightField = GraphQLFieldDefinition.newFieldDefinition()
            .name("weight")
            .type(GraphQLFloat)
            .withDirective(externalDirective)
            .build()

        val barObject = GraphQLObjectType.newObject()
            .name("Bar")
            .field(externalWeightField)
            .withDirective(getKeyDirective("weight"))
            .build()

        val barField = GraphQLFieldDefinition.newFieldDefinition()
            .name("bar")
            .type(barObject)
            .withDirective(externalDirective)
            .build()

        val validatedType = GraphQLObjectType.newObject()
            .name("Foo")
            .field(idExternalField)
            .field(shippingCostField)
            .field(barField)
            .withDirective(getKeyDirective("id"))
            .build()

        val errors = validateRequiresDirective(
            validatedType = validatedType.name,
            fieldMap = validatedType.fieldDefinitions.map { it.name to it }.toMap(),
            validatedField = shippingCostField,
            extendedType = true
        )

        assertEquals(0, errors.size)
    }
}
