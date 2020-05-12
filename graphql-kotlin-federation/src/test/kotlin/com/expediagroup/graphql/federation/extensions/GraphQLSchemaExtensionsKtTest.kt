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

package com.expediagroup.graphql.federation.extensions

import com.expediagroup.graphql.federation.directives.EXTENDS_DIRECTIVE_TYPE
import graphql.Scalars.GraphQLString
import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLObjectType
import graphql.schema.GraphQLSchema
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class GraphQLSchemaExtensionsKtTest {

    private val simplQuery = GraphQLObjectType.newObject()
        .name("Query")
        .field(GraphQLFieldDefinition.newFieldDefinition()
            .name("foo")
            .type(GraphQLString)
        )

    @Test
    fun `addDirectivesIfNotPresent does nothing on empty list`() {
        val oldSchema = GraphQLSchema.newSchema()
            .query(simplQuery)
            .build()
        val numberOfDirectives = oldSchema.directives.size
        val newSchema = oldSchema.addDirectivesIfNotPresent(emptyList()).build()
        assertEquals(expected = numberOfDirectives, actual = newSchema.directives.size)
    }

    @Test
    fun `addDirectivesIfNotPresent does nothing if directive is already present`() {
        val oldSchema = GraphQLSchema.newSchema()
            .query(simplQuery)
            .additionalDirective(EXTENDS_DIRECTIVE_TYPE)
            .build()
        val numberOfDirectives = oldSchema.directives.size
        val newSchema = oldSchema.addDirectivesIfNotPresent(listOf(EXTENDS_DIRECTIVE_TYPE)).build()
        assertNotNull(newSchema.getDirective(EXTENDS_DIRECTIVE_TYPE.name))
        assertEquals(expected = numberOfDirectives, actual = newSchema.directives.size)
    }

    @Test
    fun `addDirectivesIfNotPresent adds directive if not present`() {
        val oldSchema = GraphQLSchema.newSchema()
            .query(simplQuery)
            .build()
        val numberOfDirectives = oldSchema.directives.size
        val newSchema = oldSchema.addDirectivesIfNotPresent(listOf(EXTENDS_DIRECTIVE_TYPE)).build()
        assertNotNull(newSchema.getDirective(EXTENDS_DIRECTIVE_TYPE.name))
        assertEquals(expected = numberOfDirectives + 1, actual = newSchema.directives.size)
    }
}
