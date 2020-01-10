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

import com.expediagroup.graphql.federation.directives.extendsDirectiveType
import graphql.Scalars.GraphQLString
import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLObjectType
import graphql.schema.GraphQLTypeReference
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class ValidateProvidesDirectiveKtTest {

    /**
     * type MyType {
     *   foo: String @provides
     * }
     */
    @Test
    fun `returns an error when the type is not GraphQLObjectType or GraphQLTypeReference`() {
        val federatedType = GraphQLFieldDefinition.newFieldDefinition()
            .name("foo")
            .type(GraphQLString)
            .build()

        val errors = validateProvidesDirective("MyType", federatedType)

        assertEquals(1, errors.size)
        assertEquals("@provides directive is specified on a MyType.foo field but it does not return an object type", errors.first())
    }

    /**
     * type MyType {
     *   foo: MyOtherType @provides
     * }
     */
    @Test
    fun `returns no errors when the type is GraphQLTypeReference`() {
        val federatedType = GraphQLFieldDefinition.newFieldDefinition()
            .name("foo")
            .type(GraphQLTypeReference("MyOtherType"))
            .build()

        val errors = validateProvidesDirective("MyType", federatedType)

        assertEquals(0, errors.size)
    }

    /**
     * type MyObject {
     *   bar: String
     * }
     *
     * type MyType {
     *   foo: MyObject @provides
     * }
     */
    @Test
    fun `returns an error when the type is GraphQLObjectType but is not extended`() {
        val objectType = GraphQLObjectType.newObject()
            .name("MyObject")
            .field(GraphQLFieldDefinition.newFieldDefinition().name("bar").type(GraphQLString))
            .build()

        val federatedType = GraphQLFieldDefinition.newFieldDefinition()
            .name("foo")
            .type(objectType)
            .build()

        val errors = validateProvidesDirective("MyType", federatedType)

        assertEquals(1, errors.size)
        assertEquals("@provides directive is specified on a MyType.foo field references local object", errors.first())
    }

    /**
     * type MyObject @extends {
     *   bar: String
     * }
     *
     * type MyType {
     *   foo: MyObject @provides
     * }
     */
    @Test
    fun `validates the return type fields when the type is GraphQLObjectType and is extended`() {
        val objectType = GraphQLObjectType.newObject()
            .name("MyObject")
            .field(GraphQLFieldDefinition.newFieldDefinition().name("bar").type(GraphQLString))
            .withDirective(extendsDirectiveType)
            .build()

        val federatedType = GraphQLFieldDefinition.newFieldDefinition()
            .name("foo")
            .type(objectType)
            .build()

        val errors = validateProvidesDirective("MyType", federatedType)

        assertEquals(1, errors.size)
        assertEquals("@provides directive is missing on federated MyType.foo type", errors.first())
    }
}
