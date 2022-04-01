/*
 * Copyright 2022 Expedia, Inc
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

package com.expediagroup.graphql.generator.internal.types

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.generator.annotations.GraphQLName
import com.expediagroup.graphql.generator.annotations.GraphQLValidObjectLocations
import com.expediagroup.graphql.generator.exceptions.InvalidGraphQLNameException
import com.expediagroup.graphql.generator.exceptions.InvalidObjectLocationException
import com.expediagroup.graphql.generator.exceptions.PrimaryConstructorNotFound
import com.expediagroup.graphql.generator.test.utils.SimpleDirective
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class GenerateInputObjectTest : TypeTestHelper() {

    @Suppress("Detekt.UnusedPrivateClass")
    @GraphQLDescription("The truth")
    @SimpleDirective
    class InputClass {
        @SimpleDirective
        val myField: String = "car"
    }

    @Suppress("Detekt.UnusedPrivateClass")
    @GraphQLName("InputClassRenamed")
    class InputClassCustomName {
        @GraphQLName("myFieldRenamed")
        val myField: String = "car"
    }

    @GraphQLValidObjectLocations(locations = [GraphQLValidObjectLocations.Locations.INPUT_OBJECT])
    class InputOnly {
        val myField: String = "car"
    }

    @GraphQLValidObjectLocations(locations = [GraphQLValidObjectLocations.Locations.OBJECT])
    class OutputOnly {
        val myField: String = "car"
    }

    class `Invalid$InputTypeName`

    class MissingPublicConstructor private constructor(val id: Int)

    @Test
    fun `Test naming`() {
        val result = generateInputObject(generator, InputClass::class)
        assertEquals("InputClassInput", result.name)
    }

    @Test
    fun `Test custom naming on classes`() {
        val result = generateInputObject(generator, InputClassCustomName::class)
        assertEquals("InputClassRenamedInput", result.name)
    }

    @Test
    fun `Test custom naming on arguments`() {
        val result = generateInputObject(generator, InputClassCustomName::class)
        assertEquals(expected = 1, actual = result.fields.size)
        assertEquals("myFieldRenamed", result.fields.first().name)
    }

    @Test
    fun `Test description`() {
        val result = generateInputObject(generator, InputClass::class)
        assertEquals("The truth", result.description)
    }

    @Test
    fun `directives should be on input objects`() {
        val result = generateInputObject(generator, InputClass::class)
        assertEquals(1, result.appliedDirectives.size)
        assertEquals("simpleDirective", result.appliedDirectives.first().name)
    }

    @Test
    fun `directives should be on input object fields`() {
        val result = generateInputObject(generator, InputClass::class)
        assertEquals(1, result.fields.first().appliedDirectives.size)
        assertEquals("simpleDirective", result.fields.first().appliedDirectives.first().name)
    }

    @Test
    fun `input only objects are generated`() {
        assertDoesNotThrow {
            generateInputObject(generator, InputOnly::class)
        }
    }

    @Test
    fun `output only objects throw an exception`() {
        assertFailsWith(InvalidObjectLocationException::class) {
            generateInputObject(generator, OutputOnly::class)
        }
    }

    @Test
    fun `Generation of input object will fail if it specifies invalid name`() {
        assertFailsWith(InvalidGraphQLNameException::class) {
            generateInputObject(generator, `Invalid$InputTypeName`::class)
        }
    }

    @Test
    fun `Generation of input object will fail if it does not have public constructor`() {
        assertFailsWith(PrimaryConstructorNotFound::class) {
            generateInputObject(generator, MissingPublicConstructor::class)
        }
    }
}
