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

package com.expediagroup.graphql.generator.internal.types

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.generator.annotations.GraphQLName
import com.expediagroup.graphql.generator.test.utils.SimpleDirective
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class GenerateInputPropertyTest : TypeTestHelper() {

    private data class InputPropertyTestClass(
        @GraphQLDescription("Custom description")
        val description: String,

        @GraphQLName("newName")
        val changeMe: String,

        @SimpleDirective
        val directiveWithNoPrefix: String,

        @property:SimpleDirective
        val directiveWithPrefix: String
    )

    @Test
    fun `Input property can have a description`() {
        val result = generateInputProperty(generator, InputPropertyTestClass::description, InputPropertyTestClass::class)
        assertEquals("Custom description", result.description)
    }

    @Test
    fun `Input property names can change`() {
        val result = generateInputProperty(generator, InputPropertyTestClass::changeMe, InputPropertyTestClass::class)
        assertEquals("newName", result.name)
    }

    @Test
    fun `Input property can have directives`() {
        val resultWithNoPrefix = generateInputProperty(generator, InputPropertyTestClass::directiveWithNoPrefix, InputPropertyTestClass::class)
        assertEquals(1, resultWithNoPrefix.directives.size)
        assertEquals("simpleDirective", resultWithNoPrefix.directives.first().name)

        val resultWithPrefix = generateInputProperty(generator, InputPropertyTestClass::directiveWithPrefix, InputPropertyTestClass::class)
        assertEquals(1, resultWithPrefix.directives.size)
        assertEquals("simpleDirective", resultWithPrefix.directives.first().name)
    }
}
