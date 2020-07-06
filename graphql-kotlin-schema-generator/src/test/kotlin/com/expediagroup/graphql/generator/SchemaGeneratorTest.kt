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
    }

    @Test
    fun generateAdditionalTypes() {
        val config = SchemaGeneratorConfig(listOf("com.expediagroup.graphql.generator"))
        val generator = CustomSchemaGenerator(config)
        generator.addTypes(MyCustomAnnotation::class)

        val result = generator.generateCustomAdditionalTypes()

        assertEquals(1, result.size)
        assertEquals("SomeObjectWithAnnotation!", result.first().deepName)
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

        internal fun generateCustomAdditionalTypes() = generateAdditionalTypes()
    }

    annotation class MyCustomAnnotation
    annotation class MyOtherCustomAnnotation

    @MyCustomAnnotation
    data class SomeObjectWithAnnotation(val name: String)
}
