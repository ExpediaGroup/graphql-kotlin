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

package com.expediagroup.graphql.types

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonPrimitive
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class GraphQLErrorTest {
    private val objectMapper = Json {
        ignoreUnknownKeys = true
    }

    @Test
    fun `Simple error is spec compliant with serialization`() {
        val error = GraphQLError(
            message = "error thrown"
        )

        val expectedJson =
            """{"message":"error thrown"}"""

        assertEquals(expectedJson, objectMapper.encodeToString(error))
    }

    @Test
    fun `Error with path & locations is spec compliant with serialization`() {
        val error = GraphQLError(
            message = "error thrown",
            locations = listOf(SourceLocation(6, 7)),
            path = listOf(JsonPrimitive("path"), JsonPrimitive(1), JsonPrimitive("field"))
        )

        val expectedJson =
            """{"message":"error thrown","locations":[{"line":6,"column":7}],"path":["path",1,"field"]}"""

        assertEquals(expectedJson, objectMapper.encodeToString(error))
    }

    @Test
    fun `Error with extensions is spec compliant with serialization`() {
        val error = GraphQLError(
            message = "error thrown",
            extensions = mapOf("foo" to JsonPrimitive("bar"))
        )

        val expectedJson =
            """{"message":"error thrown","extensions":{"foo":"bar"}}"""

        assertEquals(expectedJson, objectMapper.encodeToString(error))
    }

    @Test
    fun `Full error is spec compliant with serialization`() {
        val error = GraphQLError(
            message = "error thrown",
            locations = listOf(SourceLocation(6, 7)),
            path = listOf(JsonPrimitive("path"), JsonPrimitive(1), JsonPrimitive("field")),
            extensions = mapOf("foo" to JsonPrimitive("bar"))
        )

        val expectedJson =
            """{"message":"error thrown","locations":[{"line":6,"column":7}],"path":["path",1,"field"],"extensions":{"foo":"bar"}}"""

        assertEquals(expectedJson, objectMapper.encodeToString(error))
    }

    @Test
    fun `Simple spec compliant error works with deserialization`() {
        val input =
            """{"message":"error thrown"}"""

        val error: GraphQLError = objectMapper.decodeFromString(input)

        assertEquals("error thrown", error.message)
        assertNull(error.locations)
        assertNull(error.path)
        assertNull(error.extensions)
    }

    @Test
    fun `Full spec compliant error works with deserialization`() {
        val input =
            """{"message":"error thrown","locations":[{"line":6,"column":7}],"path":["path",1,"field"],"extensions":{"foo":"bar"}}"""

        val error: GraphQLError = objectMapper.decodeFromString(input)

        assertEquals("error thrown", error.message)
        assertNotNull(error.locations) { locations ->
            assertEquals(1, locations.size)
            assertEquals(6, locations.first().line)
            assertEquals(7, locations.first().column)
        }

        assertEquals(listOf(JsonPrimitive("path"), JsonPrimitive(1), JsonPrimitive("field")), error.path)
        assertEquals(mapOf("foo" to JsonPrimitive("bar")), error.extensions)
    }
}
