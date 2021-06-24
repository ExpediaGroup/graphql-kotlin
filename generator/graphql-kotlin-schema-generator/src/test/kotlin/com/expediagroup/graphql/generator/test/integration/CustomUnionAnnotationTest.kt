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

package com.expediagroup.graphql.generator.test.integration

import com.expediagroup.graphql.generator.TopLevelObject
import com.expediagroup.graphql.generator.annotations.GraphQLUnion
import com.expediagroup.graphql.generator.extensions.deepName
import com.expediagroup.graphql.generator.testSchemaConfig
import com.expediagroup.graphql.generator.toSchema
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertNotNull

class CustomUnionAnnotationTest {

    @Test
    fun `custom unions can be defined with a variety of return types`() {
        val schema = toSchema(testSchemaConfig, listOf(TopLevelObject(Query())))
        assertNotNull(schema)
        assertNotNull(schema.getType("One"))
        assertNotNull(schema.getType("Two"))
        assertNotNull(schema.getType("Three"))
        assertNotNull(schema.getType("Four"))
        assertNotNull(schema.getType("Even"))
        assertNotNull(schema.getType("Odd"))
        assertNotNull(schema.getType("Number"))
        assertEquals("Even!", schema.queryType.getFieldDefinition("even").type.deepName)
        assertEquals("Odd!", schema.queryType.getFieldDefinition("odd").type.deepName)
        assertEquals("Number!", schema.queryType.getFieldDefinition("number").type.deepName)
        assertEquals("[Number!]!", schema.queryType.getFieldDefinition("listNumbers").type.deepName)
    }

    @Test
    fun `verify exception is thrown when union returns different types`() {
        assertFails {
            toSchema(testSchemaConfig, listOf(TopLevelObject(InvalidQuery())))
        }
    }

    class One(val value: String)
    class Two(val value: String)
    class Three(val value: String)
    class Four(val value: String)

    class Query {
        @GraphQLUnion(name = "Even", possibleTypes = [Two::class, Four::class])
        fun even(first: Boolean): Any = if (first) Two("2") else Four("4")

        @GraphQLUnion(name = "Odd", possibleTypes = [One::class, Three::class])
        fun odd(first: Boolean): Any = if (first) One("1") else Three("3")

        @GraphQLUnion(name = "Number", possibleTypes = [One::class, Two::class, Three::class, Four::class])
        fun number(): Any = One("1")

        @GraphQLUnion(name = "Number", possibleTypes = [One::class, Two::class, Three::class, Four::class])
        fun listNumbers(): List<Any> = listOf(One("1"), Two("2"))
    }

    class InvalidQuery {
        @GraphQLUnion(name = "Number", possibleTypes = [One::class, Two::class])
        fun number1(): Any = One("1")

        @GraphQLUnion(name = "Number", possibleTypes = [Three::class, Four::class])
        fun number2(): Any = Three("1")
    }
}
