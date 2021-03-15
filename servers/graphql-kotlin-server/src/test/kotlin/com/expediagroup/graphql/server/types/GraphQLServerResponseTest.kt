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
import kotlin.test.assertTrue

class GraphQLServerResponseTest {

    class MyQuery(val foo: Int)

    private val mapper = jacksonObjectMapper()

    @Test
    fun `verify simple serialization`() {
        val response = GraphQLResponse(
            data = MyQuery(1)
        )

        val expectedJson =
            """{"data":{"foo":1}}"""

        assertEquals(expectedJson, mapper.writeValueAsString(response))
    }

    @Test
    fun `verify complete serialization`() {
        val request = GraphQLResponse(
            data = MyQuery(1),
            errors = listOf(GraphQLServerError("my error")),
            extensions = mapOf("bar" to 2)
        )

        val expectedJson =
            """{"data":{"foo":1},"errors":[{"message":"my error"}],"extensions":{"bar":2}}"""

        assertEquals(expectedJson, mapper.writeValueAsString(request))
    }

    @Test
    fun `verify batch response serialization`() {
        val batchResponse = GraphQLBatchResponse(
            responses = listOf(
                GraphQLResponse(
                    data = MyQuery(1)
                ),
                GraphQLResponse(
                    data = MyQuery(2),
                    errors = listOf(GraphQLServerError("my error")),
                    extensions = mapOf("bar" to 2)
                )
            )
        )
        val expectedJson =
            """[{"data":{"foo":1}},{"data":{"foo":2},"errors":[{"message":"my error"}],"extensions":{"bar":2}}]"""
        assertEquals(expectedJson, mapper.writeValueAsString(batchResponse))
    }

    @Test
    fun `verify simple deserialization`() {
        val input =
            """{"data":{"foo":1}}"""

        val response = mapper.readValue<GraphQLServerResponse>(input)

        assertTrue(response is GraphQLResponse<*>)
        val data = response.data as? Map<*, *>?
        assertNotNull(data)
        assertEquals(1, data["foo"])
        assertNull(response.errors)
        assertNull(response.extensions)
    }

    @Test
    fun `verify complete deserialization`() {
        val input =
            """{"data":{"foo":1},"errors":[{"message":"my error"}],"extensions":{"bar":2}}"""

        val response = mapper.readValue<GraphQLServerResponse>(input)

        assertTrue(response is GraphQLResponse<*>)
        val data = response.data as? Map<*, *>?
        assertNotNull(data)
        assertEquals(1, data["foo"])
        assertEquals(1, response.errors?.size)
        assertEquals("my error", response.errors?.first()?.message)
        val extensions = response.extensions
        assertNotNull(extensions)
        assertEquals(2, extensions["bar"])
    }

    @Test
    fun `verify batch response deserialization`() {
        val serialized =
            """[{"data":{"foo":1},"errors":[{"message":"my error"}],"extensions":{"bar":2}},{"data":{"foo":2}}]"""

        val response = mapper.readValue<GraphQLServerResponse>(serialized)
        assertTrue(response is GraphQLBatchResponse)
        assertEquals(2, response.responses.size)
        val first = response.responses[0]
        val firstData = first.data as? Map<*, *>?
        assertNotNull(firstData)
        assertEquals(1, firstData["foo"])
        assertEquals(1, first.errors?.size)
        assertEquals("my error", first.errors?.first()?.message)
        val extensions = first.extensions
        assertNotNull(extensions)
        assertEquals(2, extensions["bar"])

        val second = response.responses[1]
        val secondData = second.data as? Map<*, *>?
        assertNotNull(secondData)
        assertEquals(2, secondData["foo"])
        assertNull(second.errors)
        assertNull(second.extensions)
    }
}
