/*
 * Copyright 2019 Expedia, Inc
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

package com.expediagroup.graphql.generator.types

import com.expediagroup.graphql.annotations.GraphQLDescription
import com.expediagroup.graphql.annotations.GraphQLName
import com.expediagroup.graphql.generator.extensions.getSimpleName
import com.expediagroup.graphql.test.utils.SimpleDirective
import graphql.schema.GraphQLInterfaceType
import org.junit.jupiter.api.Test
import kotlin.math.PI
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

internal class GenerateInterfaceTest : TypeTestHelper() {

    @Test
    fun `Test naming`() {
        val result = generateInterface(generator, HappyInterface::class) as? GraphQLInterfaceType
        assertEquals("HappyInterface", result?.name)
    }

    @Test
    fun `Test custom naming`() {
        val result = generateInterface(generator, HappyInterfaceCustomName::class) as? GraphQLInterfaceType
        assertEquals("HappyInterfaceRenamed", result?.name)
    }

    @Test
    fun `Test description`() {
        val result = generateInterface(generator, HappyInterface::class) as? GraphQLInterfaceType
        assertEquals("The truth", result?.description)
    }

    @Test
    fun `Interfaces can have directives`() {
        val result = generateInterface(generator, HappyInterface::class) as? GraphQLInterfaceType
        assertEquals(1, result?.directives?.size)
        assertEquals("simpleDirective", result?.directives?.first()?.name)
    }

    @Test
    fun `absctract classes generate interfaces`() {
        assertEquals(0, generator.additionalTypes.size)
        val result = generateInterface(generator, Shape::class) as? GraphQLInterfaceType
        assertEquals("Shape", result?.name)
        assertEquals(2, generator.additionalTypes.size)
        assertNotNull(generator.additionalTypes.find { it.getSimpleName() == "Circle" })
        assertNotNull(generator.additionalTypes.find { it.getSimpleName() == "Square" })
    }

    @Test
    fun `sealed classes generate interfaces`() {
        assertEquals(0, generator.additionalTypes.size)
        val result = generateInterface(generator, Pet::class) as? GraphQLInterfaceType
        assertEquals("Pet", result?.name)
        assertEquals(2, generator.additionalTypes.size)
        assertNotNull(generator.additionalTypes.find { it.getSimpleName() == "Cat" })
        assertNotNull(generator.additionalTypes.find { it.getSimpleName() == "Dog" })
    }

    @Suppress("Detekt.UnusedPrivateClass")
    @GraphQLDescription("The truth")
    @SimpleDirective
    interface HappyInterface

    @Suppress("Detekt.UnusedPrivateClass")
    @GraphQLName("HappyInterfaceRenamed")
    interface HappyInterfaceCustomName

    abstract class Shape(val area: Double)
    class Circle(radius: Double) : Shape(PI * radius * radius)
    class Square(sideLength: Double) : Shape(sideLength * sideLength)

    sealed class Pet(val name: String) {
        class Dog(name: String, val goodBoysReceived: Int) : Pet(name)
        class Cat(name: String, val livesRemaining: Int) : Pet(name)
    }
}
