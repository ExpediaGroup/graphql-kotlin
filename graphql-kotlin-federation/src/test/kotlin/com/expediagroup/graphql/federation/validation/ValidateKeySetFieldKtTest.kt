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

import graphql.Scalars.GraphQLString
import graphql.schema.GraphQLDirective
import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLInterfaceType
import graphql.schema.GraphQLList
import graphql.schema.GraphQLTypeReference
import graphql.schema.GraphQLUnionType
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class ValidateKeySetFieldKtTest {

    private val externalDirective: GraphQLDirective = GraphQLDirective.newDirective()
        .name("external")
        .build()

    @Test
    fun `returns an error on null targetField`() {
        val errors = mutableListOf<String>()
        validateKeySetField(
            targetField = null,
            extendedType = false,
            errors = errors,
            validatedDirective = "foo")

        assertEquals(1, errors.size)
        assertEquals("foo specifies invalid field set - field set specifies fields that do not exist", errors.first())
    }

    /**
     * type Parent @extends {
     *   foo: String
     * }
     */
    @Test
    fun `returns an error on extended type without external directive`() {
        val errors = mutableListOf<String>()
        val target = GraphQLFieldDefinition.newFieldDefinition()
            .name("foo")
            .type(GraphQLString)
            .build()

        validateKeySetField(
            targetField = target,
            extendedType = true,
            errors = errors,
            validatedDirective = "foo")

        assertEquals(1, errors.size)
        assertEquals("foo specifies invalid field set - extended type incorrectly references local field=foo", errors.first())
    }

    /**
     * type Parent {
     *   foo: String @external
     * }
     */
    @Test
    fun `returns an error on a non extended type with external directive`() {
        val errors = mutableListOf<String>()
        val target = GraphQLFieldDefinition.newFieldDefinition()
            .name("foo")
            .type(GraphQLString)
            .withDirective(externalDirective)
            .build()

        validateKeySetField(
            targetField = target,
            extendedType = false,
            errors = errors,
            validatedDirective = "foo")

        assertEquals(1, errors.size)
        assertEquals("foo specifies invalid field set - type incorrectly references external field=foo", errors.first())
    }

    /**
     * type Parent @extends {
     *   foo: [String] @external
     * }
     */
    @Test
    fun `returns an error when the field type is a list`() {
        val errors = mutableListOf<String>()
        val target = GraphQLFieldDefinition.newFieldDefinition()
            .name("foo")
            .type(GraphQLList.list(GraphQLString))
            .withDirective(externalDirective)
            .build()

        validateKeySetField(
            targetField = target,
            extendedType = true,
            errors = errors,
            validatedDirective = "foo")

        assertEquals(1, errors.size)
        assertEquals("foo specifies invalid field set - field set references GraphQLList, field=foo", errors.first())
    }

    /**
     * interface MyInterface {
     *   bar: String
     * }
     * type Parent @extends {
     *   foo: MyInterface @external
     * }
     */
    @Test
    fun `returns an error when the field type is a interface`() {
        val errors = mutableListOf<String>()
        val interfaceType = GraphQLInterfaceType.newInterface()
            .name("MyInterface")
            .field(GraphQLFieldDefinition.newFieldDefinition().name("bar").type(GraphQLString))

        val target = GraphQLFieldDefinition.newFieldDefinition()
            .name("foo")
            .type(interfaceType)
            .withDirective(externalDirective)
            .build()

        validateKeySetField(
            targetField = target,
            extendedType = true,
            errors = errors,
            validatedDirective = "foo")

        assertEquals(1, errors.size)
        assertEquals("foo specifies invalid field set - field set references GraphQLInterfaceType, field=foo", errors.first())
    }

    /**
     * union MyUnion = MyType
     *
     * type Parent @extends {
     *   foo: MyUnion @external
     * }
     */
    @Test
    fun `returns an error when the field type is a union`() {
        val errors = mutableListOf<String>()
        val unionType = GraphQLUnionType.newUnionType()
            .name("MyUnion")
            .possibleType(GraphQLTypeReference("MyType"))

        val target = GraphQLFieldDefinition.newFieldDefinition()
            .name("foo")
            .type(unionType)
            .withDirective(externalDirective)
            .build()

        validateKeySetField(
            targetField = target,
            extendedType = true,
            errors = errors,
            validatedDirective = "foo")

        assertEquals(1, errors.size)
        assertEquals("foo specifies invalid field set - field set references GraphQLUnionType, field=foo", errors.first())
    }

    /**
     * type Parent @extends {
     *   foo: String @external
     * }
     */
    @Test
    fun `returns no errors when there is extended type with external directive`() {
        val errors = mutableListOf<String>()
        val target = GraphQLFieldDefinition.newFieldDefinition()
            .name("foo")
            .type(GraphQLString)
            .withDirective(externalDirective)
            .build()

        validateKeySetField(
            targetField = target,
            extendedType = true,
            errors = errors,
            validatedDirective = "foo")

        assertEquals(0, errors.size)
    }
}
