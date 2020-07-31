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

package com.expediagroup.graphql.generator

import com.expediagroup.graphql.SchemaGeneratorConfig
import com.expediagroup.graphql.exceptions.InvalidPackagesException
import com.expediagroup.graphql.extensions.deepName
import org.junit.jupiter.api.Test
import kotlin.reflect.KClass
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class SchemaGeneratorTest {

    @Test
    fun addAdditionalTypesWithAnnotation() {
        val config = SchemaGeneratorConfig(listOf("com.expediagroup.graphql.generator"))
        val generator = CustomSchemaGenerator(config)
        assertEquals(0, generator.additionalTypes.size)

        // Add a non-existant annotaiton
        generator.addTypes(MyOtherCustomAnnotation::class)
        assertEquals(0, generator.additionalTypes.size)

        // Add a valid annotation
        generator.addTypes(MyCustomAnnotation::class)
        assertEquals(1, generator.additionalTypes.size)

        generator.addInputTypes(MyInterfaceAnnotation::class)
        assertEquals(3, generator.additionalTypes.size)

        // Verify there are no duplicates
        val result = generator.generateCustomAdditionalTypes().map { it.deepName }.toSet()
        assertEquals(4, result.size)
    }

    @Test
    fun generateAdditionalTypes() {
        val config = SchemaGeneratorConfig(listOf("com.expediagroup.graphql.generator"))
        val generator = CustomSchemaGenerator(config)
        generator.addTypes(MyCustomAnnotation::class)

        val result = generator.generateCustomAdditionalTypes()

        assertEquals(1, result.size)
        assertEquals("SomeObjectWithAnnotation", result.first().deepName)
    }

    @Test
    fun generateAdditionalInputTypes() {
        val config = SchemaGeneratorConfig(listOf("com.expediagroup.graphql.generator"))
        val generator = CustomSchemaGenerator(config)
        generator.addInputTypes(MyCustomAnnotation::class)

        val result = generator.generateCustomAdditionalTypes()

        assertEquals(1, result.size)
        assertEquals("SomeObjectWithAnnotationInput", result.first().deepName)
    }

    @Test
    fun generateBothInputAndOutputTypesWithSameName() {
        val config = SchemaGeneratorConfig(listOf("com.expediagroup.graphql.generator"))
        val generator = CustomSchemaGenerator(config)
        generator.addTypes(AnnotationOnAllTypes::class)
        generator.addInputTypes(AnnotationOnAllTypes::class)

        val result = generator.generateCustomAdditionalTypes().map { it.deepName }.toSet()

        // Verify there are no duplicates
        assertEquals(8, result.size)
    }

    @Test
    fun invalidPackagesThrowsException() {
        assertFailsWith(InvalidPackagesException::class) {
            val config = SchemaGeneratorConfig(listOf("foo.bar"))
            SchemaGenerator(config)
        }
    }

    class CustomSchemaGenerator(config: SchemaGeneratorConfig) : SchemaGenerator(config) {
        internal fun addTypes(annotation: KClass<*>) = addAdditionalTypesWithAnnotation(annotation)

        internal fun addInputTypes(annotation: KClass<*>) = addAdditionalTypesWithAnnotation(annotation, true)

        internal fun generateCustomAdditionalTypes() = generateAdditionalTypes()
    }

    annotation class MyCustomAnnotation
    annotation class MyOtherCustomAnnotation
    annotation class MyInterfaceAnnotation
    annotation class AnnotationOnAllTypes

    @MyCustomAnnotation
    @AnnotationOnAllTypes
    data class SomeObjectWithAnnotation(val name: String)

    @MyInterfaceAnnotation
    @AnnotationOnAllTypes
    interface SomeUnion

    @AnnotationOnAllTypes
    data class UnionImpl(val id: String) : SomeUnion

    @MyInterfaceAnnotation
    @AnnotationOnAllTypes
    interface SomeInterface {
        val id: String
    }

    @AnnotationOnAllTypes
    data class InterfaceImpl(override val id: String) : SomeInterface
}
