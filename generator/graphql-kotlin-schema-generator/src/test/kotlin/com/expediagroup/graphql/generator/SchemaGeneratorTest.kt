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

package com.expediagroup.graphql.generator

import com.expediagroup.graphql.generator.exceptions.InvalidPackagesException
import com.expediagroup.graphql.generator.extensions.deepName
import com.expediagroup.graphql.generator.hooks.SchemaGeneratorHooks
import graphql.schema.GraphQLNamedType
import graphql.schema.GraphQLObjectType
import org.junit.jupiter.api.Test
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull

class SchemaGeneratorTest {
    @Test
    fun addAdditionalTypesWithAnnotation() {
        val config = SchemaGeneratorConfig(listOf("com.expediagroup.graphql.generator"))
        val generator = CustomSchemaGenerator(config)
        assertEquals(0, generator.additionalTypes.size)

        // Add a non-existent annotation
        generator.addTypes(MyOtherCustomAnnotation::class)
        assertEquals(0, generator.additionalTypes.size)

        // Add a valid annotation
        generator.addTypes(MyCustomAnnotation::class)
        assertEquals(1, generator.additionalTypes.size)

        // Verify interfaces and unions are not added when input types
        generator.addInputTypes(MyInterfaceAnnotation::class)
        assertEquals(1, generator.additionalTypes.size)

        // Interfaces and unions can be added when not input types
        generator.addTypes(MyInterfaceAnnotation::class)
        assertEquals(3, generator.additionalTypes.size)

        // Verify the interface implementations are picked up at generation time
        val result = generator.generateCustomAdditionalTypes()
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
        val customHooks = object : SchemaGeneratorHooks {
            override fun isValidAdditionalType(kClass: KClass<*>, inputType: Boolean): Boolean {
                val allowedForInput = kClass.findAnnotation<AnnotationOnAllTypes>()?.allowedForInput ?: true

                if (inputType && !allowedForInput) {
                    return false
                }

                return super.isValidAdditionalType(kClass, inputType)
            }
        }

        val config = SchemaGeneratorConfig(listOf("com.expediagroup.graphql.generator"), hooks = customHooks)
        val generator = CustomSchemaGenerator(config)
        generator.addTypes(AnnotationOnAllTypes::class)
        generator.addInputTypes(AnnotationOnAllTypes::class)

        val result = generator.generateCustomAdditionalTypes()

        // Verify there are no duplicates
        assertEquals(7, result.size)
        val interfaceImpl = assertNotNull(result.find { (it as? GraphQLNamedType)?.name == "InterfaceImpl" } as? GraphQLObjectType)
        assertEquals(6, interfaceImpl.fieldDefinitions.size)
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
    annotation class AnnotationOnAllTypes(val allowedForInput: Boolean = true)

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

    @AnnotationOnAllTypes(allowedForInput = false)
    data class InterfaceImpl(
        override val id: String,
        val listField: List<String>,
        val optionalListField: List<String>?,
        val optionalListOptionalField: List<String?>?,
        val requiredUnionField: SomeUnion,
        val optionalUnionField: SomeUnion?
    ) : SomeInterface
}
