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
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class GraphQLResponseTest {

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
            errors = listOf(GraphQLError("my error")),
            extensions = mapOf<Any, Any>("bar" to 2)
        )

        val expectedJson =
            """{"data":{"foo":1},"errors":[{"message":"my error"}],"extensions":{"bar":2}}"""

        assertEquals(expectedJson, mapper.writeValueAsString(request))
    }

    @Test
    fun `verify simple deserialization`() {
        val input =
            """{"data":{"foo":1}}"""

        val response: GraphQLResponse<MyQuery> = mapper.readValue(input)

        assertEquals(1, response.data?.foo)
        assertNull(response.errors)
        assertNull(response.extensions)
    }

    @Test
    fun `verify complete deserialization`() {
        val input =
            """{"data":{"foo":1},"errors":[{"message":"my error"}],"extensions":{"bar":2}}"""

        val response: GraphQLResponse<MyQuery> = mapper.readValue(input)

        assertEquals(1, response.data?.foo)
        assertEquals(1, response.errors?.size)
        assertEquals("my error", response.errors?.first()?.message)
        val extensions: Map<Any, Any>? = response.extensions
        assertNotNull(extensions)
        assertEquals(2, extensions["bar"])
    }
}
