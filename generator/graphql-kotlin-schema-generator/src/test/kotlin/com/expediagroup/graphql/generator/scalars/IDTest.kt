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

package com.expediagroup.graphql.generator.scalars

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class IDTest {

    private val mapper = jacksonObjectMapper()

    @Test
    fun testToString() {
        val id = ID("1")
        assertEquals(expected = "1", actual = id.toString())
    }

    @Test
    fun serialization() {
        val id = ID("2")

        val json = mapper.writeValueAsString(id)

        assertEquals(expected = "\"2\"", actual = json)

        val parsed: ID = mapper.readValue(json)

        assertEquals(expected = "2", actual = parsed.value)
    }
}
