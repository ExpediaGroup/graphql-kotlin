/*
 * Copyright 2024 Expedia, Inc
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
import com.alibaba.fastjson2.to
import com.expediagroup.graphql.generator.scalars.ID
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class GraphQLServerRequestTest {

    init {
        JSON.config(JSONWriter.Feature.WriteNulls)
    }

    private val mapper = jacksonObjectMapper()

    @Test
    fun `verify simple serialization`() {
        val request = GraphQLRequest(
            query = "{ foo }"
        )

        val expectedJson =
            """{"query":"{ foo }"}"""

        assertEquals(expectedJson, mapper.writeValueAsString(request))
    }

    @Test
    fun `verify complete serialization`() {
        val request = GraphQLRequest(
            query = "query FooQuery(\$input: Int) { foo(\$input) }",
            operationName = "FooQuery",
            variables = mapOf("input" to 1)
        )

        val expectedJson =
            """{"query":"query FooQuery(${'$'}input: Int) { foo(${'$'}input) }","operationName":"FooQuery","variables":{"input":1}}"""
        assertEquals(expectedJson, mapper.writeValueAsString(request))
    }

    @Test
    fun `verify complete serialization including ID`() {
        val request = GraphQLRequest(
            query = "query FooQuery(\$input: ID) { foo(\$input) }",
            operationName = "FooQuery",
            variables = mapOf("input" to ID("1"))
        )

        val expectedJson =
            """{"query":"query FooQuery(${'$'}input: ID) { foo(${'$'}input) }","operationName":"FooQuery","variables":{"input":"1"}}"""

        assertEquals(expectedJson, mapper.writeValueAsString(request))
    }

    @Test
    fun `verify batch request serialization`() {
        val request = GraphQLBatchRequest(
            listOf(
                GraphQLRequest(
                    query = "query FooQuery(\$input: Int) { foo(\$input) }",
                    operationName = "FooQuery",
                    variables = mapOf("input" to 1)
                ),
                GraphQLRequest(
                    query = "query BarQuery { bar }"
                )
            )
        )
        val expectedJson =
            """[{"query":"query FooQuery(${'$'}input: Int) { foo(${'$'}input) }","operationName":"FooQuery","variables":{"input":1}},{"query":"query BarQuery { bar }"}]"""
        assertEquals(expectedJson, mapper.writeValueAsString(request))
    }

    @Test
    fun `verify simple deserialization`() {
        val input =
            """{"query":"{ foo }"}"""

        val request = mapper.readValue<GraphQLServerRequest>(input)
        assertTrue(request is GraphQLRequest)
        assertEquals("{ foo }", request.query)
        assertNull(request.operationName)
        assertNull(request.variables)

        val requestFastJson = input.to<GraphQLServerRequest>()
        assertTrue(requestFastJson is GraphQLRequest)
        assertEquals("{ foo }", requestFastJson.query)
        assertNull(requestFastJson.operationName)
        assertNull(requestFastJson.variables)
    }

    @Test
    fun `verify complete deserialization`() {
        val input =
            """{"query":"query FooQuery(${'$'}input: Int) { foo(${'$'}input) }","operationName":"FooQuery","variables":{"input":1}}"""

        val request = mapper.readValue<GraphQLServerRequest>(input)
        assertTrue(request is GraphQLRequest)
        assertEquals("query FooQuery(\$input: Int) { foo(\$input) }", request.query)
        assertEquals("FooQuery", request.operationName)
        assertEquals(mapOf("input" to 1), request.variables)

        val requestFastJson = input.to<GraphQLServerRequest>()
        assertTrue(requestFastJson is GraphQLRequest)
        assertEquals("query FooQuery(\$input: Int) { foo(\$input) }", requestFastJson.query)
        assertEquals("FooQuery", requestFastJson.operationName)
        assertEquals(mapOf("input" to 1), requestFastJson.variables)
    }

    @Test
    fun `verify batch request deserialization`() {
        val input =
            """[{"query":"query FooQuery(${'$'}input: Int) { foo(${'$'}input) }","operationName":"FooQuery","variables":{"input":1}},{"query":"query BarQuery { bar }"}]"""

        val request = mapper.readValue<GraphQLServerRequest>(input)
        assertTrue(request is GraphQLBatchRequest)
        assertEquals(2, request.requests.size)
        assertEquals("query FooQuery(\$input: Int) { foo(\$input) }", request.requests[0].query)
        assertEquals("FooQuery", request.requests[0].operationName)
        assertEquals(mapOf("input" to 1), request.requests[0].variables)
        assertEquals("query BarQuery { bar }", request.requests[1].query)

        val requestFastJson = input.to<GraphQLServerRequest>()
        assertTrue(requestFastJson is GraphQLBatchRequest)
        assertEquals(2, requestFastJson.requests.size)
        assertEquals("query FooQuery(\$input: Int) { foo(\$input) }", requestFastJson.requests[0].query)
        assertEquals("FooQuery", requestFastJson.requests[0].operationName)
        assertEquals(mapOf("input" to 1), requestFastJson.requests[0].variables)
        assertEquals("query BarQuery { bar }", requestFastJson.requests[1].query)
    }
}
