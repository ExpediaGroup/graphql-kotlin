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

package com.expediagroup.graphql

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class ToSchemaKtTest {

    @Suppress("FunctionOnlyReturningConstant")
    class TestClass {
        fun greeting() = "Hello World"
    }

    @Test
    fun `valid schema`() {
        val queries = listOf(TopLevelObject(TestClass()))
        toSchema(queries = queries, config = testSchemaConfig)
    }

    @Test
    fun `generate standalone SDL`() {
        val queries = listOf(TopLevelObject(TestClass::class))
        val schema = toSchema(queries = queries, config = testSchemaConfig)

        val greetingField = schema.queryType.getFieldDefinition("greeting")
        assertEquals("String!", greetingField.type.toString())
    }
}
