/*
 * Copyright 2019 Expedia Group
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

package com.expediagroup.graphql.federation.types

import graphql.schema.GraphQLTypeUtil
import graphql.schema.GraphQLUnionType
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotNull

internal class EntityTest {

    @Test
    fun `generateEntityFieldDefinition should fail on empty set`() {
        assertFailsWith(graphql.AssertException::class) {
            generateEntityFieldDefinition(emptySet())
        }
    }

    @Test
    fun `generateEntityFieldDefinition should return a valid type on a single set`() {
        val result = generateEntityFieldDefinition(setOf("MyType"))
        assertNotNull(result)
        assertEquals(expected = "_entities", actual = result.name)
        assertFalse(result.description.isNullOrEmpty())
        assertEquals(expected = 1, actual = result.arguments.size)

        val graphQLUnionType = GraphQLTypeUtil.unwrapType(result.type).last() as? GraphQLUnionType

        assertNotNull(graphQLUnionType)
        assertEquals(expected = "_Entity", actual = graphQLUnionType.name)
        assertEquals(expected = 1, actual = graphQLUnionType.types.size)
        assertEquals(expected = "MyType", actual = graphQLUnionType.types.first().name)
    }

    @Test
    fun `generateEntityFieldDefinition should return a valid type on a multiple values`() {
        val result = generateEntityFieldDefinition(setOf("MyType", "MySecondType"))
        val graphQLUnionType = GraphQLTypeUtil.unwrapType(result.type).last() as? GraphQLUnionType

        assertNotNull(graphQLUnionType)
        assertEquals(expected = 2, actual = graphQLUnionType.types.size)
        assertEquals(expected = "MyType", actual = graphQLUnionType.types.first().name)
        assertEquals(expected = "MySecondType", actual = graphQLUnionType.types[1].name)
    }
}
