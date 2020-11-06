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

import com.expediagroup.graphql.federation.externalDirective
import graphql.Scalars.GraphQLString
import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLInterfaceType
import graphql.schema.GraphQLList
import graphql.schema.GraphQLNonNull
import graphql.schema.GraphQLObjectType
import graphql.schema.GraphQLTypeReference
import graphql.schema.GraphQLUnionType
import kotlin.test.assertEquals
import org.junit.jupiter.api.Test

class ValidateFieldSetKtTest {

    private val mockKeyDirectiveInfo = DirectiveInfo(
        directiveName = "key",
        fieldSet = "foo",
        typeName = "Parent"
    )

    private val mockProvidesDirectiveInfo = DirectiveInfo(
        directiveName = "provides",
        fieldSet = "foo",
        typeName = "Any"
    )

    private val mockRequiresDirectiveInfo = DirectiveInfo(
        directiveName = "requires",
        fieldSet = "foo",
        typeName = "Any"
    )

    @Test
    fun `@key returns an error on null targetField`() {
        val errors = mutableListOf<String>()
        validateFieldSet(
            fieldName = "foo",
            targetField = null,
            extendedType = false,
            errors = errors,
            validatedDirective = mockKeyDirectiveInfo
        )

        assertEquals(1, errors.size)
        assertEquals("@key(fields = foo) directive on Parent specifies invalid field set - field set specifies field that does not exist, field=foo", errors.first())
    }

    /**
     * type Parent @key(fields: "foo") @extends {
     *   foo: String
     * }
     */
    @Test
    fun `@key returns an error on extended type without external directive`() {
        val errors = mutableListOf<String>()
        val target = GraphQLFieldDefinition.newFieldDefinition()
            .name("foo")
            .type(GraphQLString)
            .build()

        validateFieldSet(
            fieldName = "foo",
            targetField = target,
            extendedType = true,
            errors = errors,
            validatedDirective = mockKeyDirectiveInfo
        )

        assertEquals(1, errors.size)
        assertEquals("@key(fields = foo) directive on Parent specifies invalid field set - extended type incorrectly references local field=foo", errors.first())
    }

    /**
     * type Parent @key(fields: "foo") {
     *   foo: String @external
     * }
     */
    @Test
    fun `@key returns an error on a local type with external directive`() {
        val errors = mutableListOf<String>()
        val target = GraphQLFieldDefinition.newFieldDefinition()
            .name("foo")
            .type(GraphQLString)
            .withDirective(externalDirective)
            .build()

        validateFieldSet(
            fieldName = "foo",
            targetField = target,
            extendedType = false,
            errors = errors,
            validatedDirective = mockKeyDirectiveInfo
        )

        assertEquals(1, errors.size)
        assertEquals("@key(fields = foo) directive on Parent specifies invalid field set - type incorrectly references external field=foo", errors.first())
    }

    /**
     * type Parent @key(fields: "foo") @extends {
     *   foo: ID @external
     *   bar: [String] @requires("foo")
     * }
     */
    @Test
    fun `@requries returns no errors when the field type is a list`() {
        val errors = mutableListOf<String>()
        val target = GraphQLFieldDefinition.newFieldDefinition()
            .name("foo")
            .type(GraphQLList.list(GraphQLString))
            .withDirective(externalDirective)
            .build()

        validateFieldSet(
            fieldName = "foo",
            targetField = target,
            extendedType = true,
            errors = errors,
            validatedDirective = mockRequiresDirectiveInfo
        )

        assertEquals(0, errors.size)
    }

    /**
     * type Parent @key(fields: "foo") @extends {
     *   foo: FooObject @external
     * }
     *
     * type FooObject {
     *   bar: String
     * }
     */
    @Test
    fun `@key returns no errors when the field type is an object`() {
        val errors = mutableListOf<String>()
        val stringField = GraphQLFieldDefinition.newFieldDefinition()
            .name("bar")
            .type(GraphQLString)

        val target = GraphQLFieldDefinition.newFieldDefinition()
            .name("foo")
            .type(
                GraphQLObjectType.newObject()
                    .name("FooObject")
                    .field(stringField)
            )
            .withDirective(externalDirective)
            .build()

        validateFieldSet(
            fieldName = "foo",
            targetField = target,
            extendedType = true,
            errors = errors,
            validatedDirective = mockKeyDirectiveInfo
        )

        assertEquals(0, errors.size)
    }

    /**
     * type Parent @key(fields: "foo") @extends {
     *   foo: ID @external
     *   bar: [String!]! @requires("foo")
     * }
     */
    @Test
    fun `@requries returns no errors when the field type is a non-nullable list of non-nulls`() {
        val errors = mutableListOf<String>()
        val target = GraphQLFieldDefinition.newFieldDefinition()
            .name("bar")
            .type(GraphQLNonNull.nonNull(GraphQLList.list(GraphQLNonNull.nonNull(GraphQLString))))
            .withDirective(externalDirective)
            .build()

        validateFieldSet(
            fieldName = "foo",
            targetField = target,
            extendedType = true,
            errors = errors,
            validatedDirective = mockRequiresDirectiveInfo
        )

        assertEquals(0, errors.size)
    }

    /**
     * interface MyInterface {
     *   bar: String
     * }
     * type Parent @key(fields: "foo") @extends {
     *   foo: MyInterface @external
     * }
     */
    @Test
    fun `@key returns an error when the field type is a interface`() {
        val errors = mutableListOf<String>()
        val interfaceType = GraphQLInterfaceType.newInterface()
            .name("MyInterface")
            .field(GraphQLFieldDefinition.newFieldDefinition().name("bar").type(GraphQLString))

        val target = GraphQLFieldDefinition.newFieldDefinition()
            .name("foo")
            .type(interfaceType)
            .withDirective(externalDirective)
            .build()

        validateFieldSet(
            fieldName = "foo",
            targetField = target,
            extendedType = true,
            errors = errors,
            validatedDirective = mockKeyDirectiveInfo
        )

        assertEquals(1, errors.size)
        assertEquals("@key(fields = foo) directive on Parent specifies invalid field set - field set references GraphQLInterfaceType, field=foo", errors.first())
    }

    /**
     * union MyUnion = MyType
     *
     * type Parent @key(fields: "foo") @extends {
     *   foo: MyUnion @external
     * }
     */
    @Test
    fun `@key returns an error when the field type is a union`() {
        val errors = mutableListOf<String>()
        val unionType = GraphQLUnionType.newUnionType()
            .name("MyUnion")
            .possibleType(GraphQLTypeReference("MyType"))

        val target = GraphQLFieldDefinition.newFieldDefinition()
            .name("foo")
            .type(unionType)
            .withDirective(externalDirective)
            .build()

        validateFieldSet(
            fieldName = "foo",
            targetField = target,
            extendedType = true,
            errors = errors,
            validatedDirective = mockKeyDirectiveInfo
        )

        assertEquals(1, errors.size)
        assertEquals("@key(fields = foo) directive on Parent specifies invalid field set - field set references GraphQLUnionType, field=foo", errors.first())
    }

    /**
     * type Parent @key(fields: "foo") @extends {
     *   foo: String @external
     * }
     */
    @Test
    fun `@key returns no errors when there is extended type with external directive`() {
        val errors = mutableListOf<String>()
        val target = GraphQLFieldDefinition.newFieldDefinition()
            .name("foo")
            .type(GraphQLString)
            .withDirective(externalDirective)
            .build()

        validateFieldSet(
            fieldName = "foo",
            targetField = target,
            extendedType = true,
            errors = errors,
            validatedDirective = mockKeyDirectiveInfo
        )

        assertEquals(0, errors.size)
    }
}
