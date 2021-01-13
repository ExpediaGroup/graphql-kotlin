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

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class GraphQLRequestTest {

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
    fun `verify simple deserialization`() {
        val input =
            """{"query":"{ foo }"}"""

        val request = mapper.readValue<GraphQLRequest>(input)

        assertEquals("{ foo }", request.query)
        assertNull(request.operationName)
        assertNull(request.variables)
    }

    @Test
    fun `verify complete deserialization`() {
        val input =
            """{"query":"query FooQuery(${'$'}input: Int) { foo(${'$'}input) }","operationName":"FooQuery","variables":{"input":1}}"""

        val request = mapper.readValue<GraphQLRequest>(input)

        assertEquals("query FooQuery(\$input: Int) { foo(\$input) }", request.query)
        assertEquals("FooQuery", request.operationName)
        assertEquals(mapOf("input" to 1), request.variables)
    }
}
