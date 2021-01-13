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

package com.expediagroup.graphql.generator.internal.types

import com.expediagroup.graphql.generator.extensions.unwrapType
import graphql.schema.GraphQLInterfaceType
import org.junit.jupiter.api.Test
import kotlin.reflect.full.createType
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GenerateGraphQLTypeKtTest : TypeTestHelper() {

    @Test
    fun generateGraphQLType() {
        assertEquals(0, generator.additionalTypes.size)
        val result = generateGraphQLType(generator, Pet::class.createType()).unwrapType()
        assertTrue(result is GraphQLInterfaceType)
        assertEquals("name", result.fieldDefinitions.first().name)
        assertEquals(2, generator.additionalTypes.size)
    }

    sealed class Pet(open val name: String) {
        data class Dog(override val name: String, val goodBoysReceived: Int) : Pet(name)
        data class Cat(override val name: String, val livesRemaining: Int) : Pet(name)
    }
}
