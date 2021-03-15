/*
 * Copyright 2021 Expedia, Inc
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

package com.expediagroup.graphql.server.types

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class GraphQLErrorTest {

    private val mapper = jacksonObjectMapper()

    @Test
    fun `Simple error is spec compliant with serialization`() {
        val error = GraphQLServerError(
            message = "error thrown"
        )

        val expectedJson =
            """{"message":"error thrown"}"""

        assertEquals(expectedJson, mapper.writeValueAsString(error))
    }

    @Test
    fun `Error with path & locations is spec compliant with serialization`() {
        val error = GraphQLServerError(
            message = "error thrown",
            locations = listOf(GraphQLSourceLocation(6, 7)),
            path = listOf("path", 1, "field")
        )

        val expectedJson =
            """{"message":"error thrown","locations":[{"line":6,"column":7}],"path":["path",1,"field"]}"""

        assertEquals(expectedJson, mapper.writeValueAsString(error))
    }

    @Test
    fun `Error with extensions is spec compliant with serialization`() {
        val error = GraphQLServerError(
            message = "error thrown",
            extensions = mapOf("foo" to "bar")
        )

        val expectedJson =
            """{"message":"error thrown","extensions":{"foo":"bar"}}"""

        assertEquals(expectedJson, mapper.writeValueAsString(error))
    }

    @Test
    fun `Full error is spec compliant with serialization`() {
        val error = GraphQLServerError(
            message = "error thrown",
            locations = listOf(GraphQLSourceLocation(6, 7)),
            path = listOf("path", 1, "field"),
            extensions = mapOf("foo" to "bar")
        )

        val expectedJson =
            """{"message":"error thrown","locations":[{"line":6,"column":7}],"path":["path",1,"field"],"extensions":{"foo":"bar"}}"""

        assertEquals(expectedJson, mapper.writeValueAsString(error))
    }

    @Test
    fun `Simple spec compliant error works with deserialization`() {
        val input =
            """{"message":"error thrown"}"""

        val error: GraphQLServerError = mapper.readValue(input)

        assertEquals("error thrown", error.message)
        assertNull(error.locations)
        assertNull(error.path)
        assertNull(error.extensions)
    }

    @Test
    fun `Full spec compliant error works with deserialization`() {
        val input =
            """{"message":"error thrown","locations":[{"line":6,"column":7}],"path":["path",1,"field"],"extensions":{"foo":"bar"}}"""

        val error: GraphQLServerError = mapper.readValue(input)

        assertEquals("error thrown", error.message)
        assertNotNull(error.locations) { locations ->
            assertEquals(1, locations.size)
            assertEquals(6, locations.first().line)
            assertEquals(7, locations.first().column)
        }
        assertEquals(listOf("path", 1, "field"), error.path)
        assertEquals(mapOf("foo" to "bar"), error.extensions)
    }
}
