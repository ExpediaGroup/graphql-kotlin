/*
 * Copyright 2025 Expedia, Inc
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

import com.alibaba.fastjson2.JSON
import com.alibaba.fastjson2.JSONWriter
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class GraphQLServerResponseTest {

    init {
        JSON.config(JSONWriter.Feature.WriteNulls)
    }

    class MyQuery(val foo: Int)

    private val mapper = jacksonObjectMapper()

    @Test
    fun `jackson verify simple serialization`() {
        val response = GraphQLResponse(
            data = MyQuery(1)
        )
        val expectedJson =
            """{"data":{"foo":1}}"""
        assertEquals(expectedJson, mapper.writeValueAsString(response))

    }

    @Test
    fun `fastjson2 verify simple serialization`() {
        val response = GraphQLResponse(
            data = MyQuery(1)
        )
        val expectedJson =
            """{"data":{"foo":1}}"""
        assertEquals(expectedJson, JSON.toJSONString(response))
    }

    @Test
    fun `jackson verify complete serialization`() {
        val response = GraphQLResponse(
            data = MyQuery(1),
            errors = listOf(GraphQLServerError("my error")),
            extensions = mapOf("bar" to 2)
        )
        val expectedJson =
            """{"data":{"foo":1},"errors":[{"message":"my error"}],"extensions":{"bar":2}}"""
        assertEquals(expectedJson, mapper.writeValueAsString(response))
        assertEquals(expectedJson, JSON.toJSONString(response))
    }

    @Test
    fun `fastjson2 verify complete serialization`() {
        val response = GraphQLResponse(
            data = MyQuery(1),
            errors = listOf(GraphQLServerError("my error")),
            extensions = mapOf("bar" to 2)
        )
        val expectedJson =
            """{"data":{"foo":1},"errors":[{"message":"my error"}],"extensions":{"bar":2}}"""
        assertEquals(expectedJson, JSON.toJSONString(response))
    }

    @Test
    fun `jackson verify batch response serialization`() {
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
    fun `fastjson2 verify batch response serialization`() {
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
        assertEquals(expectedJson, JSON.toJSONString(batchResponse))
    }

    @Test
    fun `jackson serializes null values from data property, not null values from GraphQLResponse and GraphQLResponseError`() {
        val response =
            GraphQLResponse(
                mapOf(
                    "1" to null,
                    "1.1" to
                        mapOf(
                            "1.1.k1" to "1.1.v1",
                            "1.1.k2" to null,
                        ),
                ),
                listOf(GraphQLServerError("test")),
                null,
            )
        assertEquals(
            """{"data":{"1":null,"1.1":{"1.1.k1":"1.1.v1","1.1.k2":null}},"errors":[{"message":"test"}]}""",
            mapper.writeValueAsString(response),
        )
    }

    @Test
    fun `fastjson2 serializes null values from data property, not null values from GraphQLResponse and GraphQLResponseError`() {
        val response =
            GraphQLResponse(
                mapOf(
                    "1" to null,
                    "1.1" to
                        mapOf(
                            "1.1.k1" to "1.1.v1",
                            "1.1.k2" to null,
                        ),
                ),
                listOf(GraphQLServerError("test")),
                null,
            )
        val json = JSON.toJSONString(response)
        assertEquals(
            """{"data":{"1":null,"1.1":{"1.1.k1":"1.1.v1","1.1.k2":null}},"errors":[{"message":"test"}]}""",
            json,
        )
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
