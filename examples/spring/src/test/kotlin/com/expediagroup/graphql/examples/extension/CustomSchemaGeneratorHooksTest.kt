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

package com.expediagroup.graphql.examples.extension

import com.expediagroup.graphql.directives.KotlinDirectiveWiringFactory
import io.mockk.mockk
import org.junit.jupiter.api.Test
import java.util.UUID
import javax.validation.Validator
import kotlin.reflect.full.createType
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class CustomSchemaGeneratorHooksTest {

    data class NonScalar(val id: String)

    @Test
    fun `UUID returns a string scalar`() {
        val validator: Validator = mockk()
        val wiringFactory: KotlinDirectiveWiringFactory = mockk()
        val hooks = CustomSchemaGeneratorHooks(validator, wiringFactory)

        val result = hooks.willGenerateGraphQLType(UUID::class.createType())
        assertNotNull(result)
        assertEquals(expected = "UUID", actual = result.name)
    }

    @Test
    fun `Non valid type returns null`() {
        val validator: Validator = mockk()
        val wiringFactory: KotlinDirectiveWiringFactory = mockk()
        val hooks = CustomSchemaGeneratorHooks(validator, wiringFactory)

        val result = hooks.willGenerateGraphQLType(NonScalar::class.createType())
        assertNull(result)
    }
}
