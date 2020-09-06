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

package com.expediagroup.graphql.generator.types

import com.expediagroup.graphql.extensions.deepName
import com.expediagroup.graphql.extensions.unwrapType
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class GenerateSuperclassesKtTest : TypeTestHelper() {

    @Test
    fun `verify the correct classes are picked up`() {
        val result = generateSuperclasses(generator, Pet.Dog::class)

        assertEquals(1, result.size)
        assertNotNull(result.find { it.unwrapType().deepName == "Pet" })
    }

    sealed class Pet(val name: String) {
        class Dog(name: String) : Pet(name)
    }
}
