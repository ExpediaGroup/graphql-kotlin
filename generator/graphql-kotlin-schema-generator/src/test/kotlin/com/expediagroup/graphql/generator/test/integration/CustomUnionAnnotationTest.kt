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

import com.expediagroup.graphql.generator.SchemaGenerator
import com.expediagroup.graphql.generator.SchemaGeneratorConfig
import com.expediagroup.graphql.generator.TopLevelObject
import com.expediagroup.graphql.generator.annotations.GraphQLDirective
import com.expediagroup.graphql.generator.annotations.GraphQLUnion
import com.expediagroup.graphql.generator.extensions.deepName
import com.expediagroup.graphql.generator.extensions.unwrapType
import com.expediagroup.graphql.generator.testSchemaConfig
import com.expediagroup.graphql.generator.toSchema
import graphql.schema.GraphQLObjectType
import graphql.schema.GraphQLUnionType
import org.junit.jupiter.api.Test
import kotlin.reflect.KClass
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertNotNull
import kotlin.test.assertSame

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
        assertNotNull(schema.getType("Prime"))
        assertEquals("Even!", schema.queryType.getFieldDefinition("even").type.deepName)
        assertEquals("Odd!", schema.queryType.getFieldDefinition("odd").type.deepName)
        assertEquals("Number!", schema.queryType.getFieldDefinition("number").type.deepName)
        assertEquals("Number", schema.queryType.getFieldDefinition("nullableNumber").type.deepName)
        assertEquals("[Number!]!", schema.queryType.getFieldDefinition("listNumbers").type.deepName)
        assertEquals("[Number]", schema.queryType.getFieldDefinition("nullableListNumbers").type.deepName)
        assertEquals("Prime!", schema.queryType.getFieldDefinition("prime").type.deepName)
        assertEquals("Prime", schema.queryType.getFieldDefinition("nullablePrime").type.deepName)
        assertEquals("[Prime!]!", schema.queryType.getFieldDefinition("listPrimes").type.deepName)
        assertEquals("[Prime]", schema.queryType.getFieldDefinition("nullableListPrimes").type.deepName)

        val unionWithDirective = schema.getType("Prime") as GraphQLUnionType
        assertNotNull(unionWithDirective.appliedDirectives)
        assertEquals(1, unionWithDirective.appliedDirectives.size)
        assertEquals("TestDirective", unionWithDirective.appliedDirectives[0].name)
    }

    @Test
    fun `verify exception is thrown when union returns different types`() {
        assertFails {
            toSchema(testSchemaConfig, listOf(TopLevelObject(InvalidQuery())))
        }
    }

    @Test
    fun `verify exception is thrown when custom union return type is not Any`() {
        assertFails {
            toSchema(testSchemaConfig, listOf(TopLevelObject(InvalidReturnTypeNumber())))
        }
        assertFails {
            toSchema(testSchemaConfig, listOf(TopLevelObject(InvalidReturnTypePrime())))
        }
    }

    @Test
    fun `verify Meta Union Annotation when adding as additional type`() {
        val generator = CustomSchemaGenerator(testSchemaConfig)
        generator.addTypes(MyAnnotation::class)
        val types = generator.generateCustomAdditionalTypes()

        assertEquals(2, types.size)
        val metaUnion = types.find { it.deepName == "Prime" } as GraphQLUnionType
        assertNotNull(metaUnion)
        assertNotNull(metaUnion.appliedDirectives)
        assertEquals(1, metaUnion.appliedDirectives.size)
        assertEquals("TestDirective", metaUnion.appliedDirectives[0].name)
        assertSame(metaUnion, (types.find { it.deepName != "Prime" } as GraphQLObjectType).getField("union").type.unwrapType())
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
        fun nullableNumber(isNull: Boolean): Any? = if (isNull) null else One("1")

        @GraphQLUnion(name = "Number", possibleTypes = [One::class, Two::class, Three::class, Four::class])
        fun listNumbers(): List<Any> = listOf(One("1"), Two("2"))

        @GraphQLUnion(name = "Number", possibleTypes = [One::class, Two::class, Three::class, Four::class])
        fun nullableListNumbers(): List<Any?>? = null

        @PrimeUnion
        fun prime(first: Boolean): Any = if (first) Two("2") else Three("3")

        @PrimeUnion
        fun nullablePrime(isNull: Boolean): Any? = if (isNull) null else Two("2")

        @PrimeUnion
        fun listPrimes(): List<Any> = listOf(Two("2"), Three("3"))

        @PrimeUnion
        fun nullableListPrimes(): List<Any?>? = null
    }

    /**
     * Each union here is valid, but using them together in the same schema means the union "Number"
     * is defined twice with different definitions and should fail
     */
    class InvalidQuery {
        @GraphQLUnion(name = "Number", possibleTypes = [One::class, Two::class])
        fun number1(): Any = One("1")

        @GraphQLUnion(name = "Number", possibleTypes = [Three::class, Four::class])
        fun number2(): Any = Three("1")
    }

    /**
     * While it is valid to compile, library users should return Any for the custom
     * union annotation
     */
    class InvalidReturnTypeNumber {
        @GraphQLUnion(name = "Number", possibleTypes = [One::class, Two::class])
        fun number1(): One = One("one")

        fun number2(): One = One("two")
    }

    /**
     * While it is valid to compile, library users should return Any for the annotation
     * with the meta union annotation
     */
    class InvalidReturnTypePrime {
        @PrimeUnion
        fun prime(): Two = Two("two")
    }

    @GraphQLDirective(name = "TestDirective")
    annotation class TestDirective

    annotation class MyAnnotation

    @TestDirective
    @MyAnnotation
    @GraphQLUnion(name = "Prime", possibleTypes = [Two::class, Three::class])
    annotation class PrimeUnion

    @MyAnnotation
    data class MyType(
        @PrimeUnion
        val union: Any
    )

    class CustomSchemaGenerator(config: SchemaGeneratorConfig) : SchemaGenerator(config) {
        internal fun addTypes(annotation: KClass<*>) = addAdditionalTypesWithAnnotation(annotation)

        internal fun generateCustomAdditionalTypes() = generateAdditionalTypes()
    }
}
