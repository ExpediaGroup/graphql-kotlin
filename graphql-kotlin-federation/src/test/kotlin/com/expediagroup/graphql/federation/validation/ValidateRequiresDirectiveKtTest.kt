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
import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLObjectType
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class ValidateRequiresDirectiveKtTest {

    private val descriptionField: GraphQLFieldDefinition = GraphQLFieldDefinition.newFieldDefinition()
        .name("description")
        .type(GraphQLString)
        .build()

    private val validatedType: GraphQLObjectType = GraphQLObjectType.newObject()
        .name("Foo")
        .field(descriptionField)
        .build()

    @Test
    fun `non extended types return an error`() {
        val errors = validateRequiresDirective(
            validatedType = validatedType.name,
            fieldMap = emptyMap(),
            validatedField = descriptionField,
            extendedType = false)

        assertEquals(1, errors.size)
        assertEquals("base Foo type has fields marked with @requires directive, validatedField=description", errors.first())
    }

    @Test
    fun `extended types run validation on the directive`() {
        val errors = validateRequiresDirective(
            validatedType = validatedType.name,
            fieldMap = emptyMap(),
            validatedField = descriptionField,
            extendedType = true)

        assertEquals(1, errors.size)
        assertEquals("@requires directive is missing on federated Foo.description type", errors.first())
    }
}
