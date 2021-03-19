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

package com.expediagroup.graphql.server.spring.execution

import com.expediagroup.graphql.server.types.GraphQLBatchRequest
import com.expediagroup.graphql.server.types.GraphQLRequest
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.Test
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.mock.web.reactive.function.server.MockServerRequest
import org.springframework.web.reactive.function.server.ServerRequest
import reactor.core.publisher.Mono
import java.util.Optional
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@ExperimentalCoroutinesApi
class SpringGraphQLRequestParserTest {

    private val objectMapper = jacksonObjectMapper()
    private val parser = SpringGraphQLRequestParser(objectMapper)

    @Test
    fun `parseRequest should return null if request method is not valid`() = runBlockingTest {
        val request = mockk<ServerRequest>(relaxed = true) {
            every { queryParam(REQUEST_PARAM_QUERY) } returns Optional.empty()
            every { method() } returns HttpMethod.PUT
        }
        assertNull(parser.parseRequest(request))
    }

    @Test
    fun `parseRequest should return null if request method is GET without query`() = runBlockingTest {
        val request = mockk<ServerRequest>(relaxed = true) {
            every { queryParam(REQUEST_PARAM_QUERY) } returns Optional.empty()
            every { method() } returns HttpMethod.GET
        }
        assertNull(parser.parseRequest(request))
    }

    @Test
    fun `parseRequest should return request if method is GET with simple query`() = runBlockingTest {
        val serverRequest = mockk<ServerRequest>(relaxed = true) {
            every { queryParam(REQUEST_PARAM_QUERY) } returns Optional.of("{ foo }")
            every { queryParam(REQUEST_PARAM_OPERATION_NAME) } returns Optional.empty()
            every { queryParam(REQUEST_PARAM_VARIABLES) } returns Optional.empty()
            every { method() } returns HttpMethod.GET
        }
        val graphQLRequest = parser.parseRequest(serverRequest)
        assertNotNull(graphQLRequest)
        assertTrue(graphQLRequest is GraphQLRequest)
        assertEquals("{ foo }", graphQLRequest.query)
        assertNull(graphQLRequest.operationName)
        assertNull(graphQLRequest.variables)
    }

    @Test
    fun `parseRequest should return request if method is GET with full query`() = runBlockingTest {
        val serverRequest = mockk<ServerRequest>(relaxed = true) {
            every { queryParam(REQUEST_PARAM_QUERY) } returns Optional.of("query MyFoo { foo }")
            every { queryParam(REQUEST_PARAM_OPERATION_NAME) } returns Optional.of("MyFoo")
            every { queryParam(REQUEST_PARAM_VARIABLES) } returns Optional.of("""{ "a": 1 }""")
            every { method() } returns HttpMethod.GET
        }
        val graphQLRequest = parser.parseRequest(serverRequest)
        assertNotNull(graphQLRequest)
        assertTrue(graphQLRequest is GraphQLRequest)
        assertEquals("query MyFoo { foo }", graphQLRequest.query)
        assertEquals("MyFoo", graphQLRequest.operationName)
        assertEquals(1, graphQLRequest.variables?.get("a"))
    }

    @Test
    fun `parseRequest should return request if method is POST with no content-type`() = runBlockingTest {
        val mockRequest = GraphQLRequest("query MyFoo { foo }", "MyFoo", mapOf("a" to 1))
        val serverRequest = MockServerRequest.builder()
            .method(HttpMethod.POST)
            .body(Mono.justOrEmpty(mockRequest))

        val graphQLRequest = parser.parseRequest(serverRequest)
        assertNotNull(graphQLRequest)
        assertTrue(graphQLRequest is GraphQLRequest)
        assertEquals("query MyFoo { foo }", graphQLRequest.query)
        assertEquals("MyFoo", graphQLRequest.operationName)
        assertEquals(1, graphQLRequest.variables?.get("a"))
    }

    @Test
    fun `parseRequest should return request if method is POST with content-type json`() = runBlockingTest {
        val mockRequest = GraphQLRequest("query MyFoo { foo }", "MyFoo", mapOf("a" to 1))
        val serverRequest = MockServerRequest.builder()
            .method(HttpMethod.POST)
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .body(Mono.justOrEmpty(mockRequest))

        val graphQLRequest = parser.parseRequest(serverRequest)
        assertNotNull(graphQLRequest)
        assertTrue(graphQLRequest is GraphQLRequest)
        assertEquals("query MyFoo { foo }", graphQLRequest.query)
        assertEquals("MyFoo", graphQLRequest.operationName)
        assertEquals(1, graphQLRequest.variables?.get("a"))
    }

    @Test
    fun `parseRequest should return request if method is POST with content-type graphql`() = runBlockingTest {
        val serverRequest = MockServerRequest.builder()
            .method(HttpMethod.POST)
            .header(HttpHeaders.CONTENT_TYPE, "application/graphql")
            .body(Mono.justOrEmpty("{ foo }"))

        val graphQLRequest = parser.parseRequest(serverRequest)
        assertNotNull(graphQLRequest)
        assertTrue(graphQLRequest is GraphQLRequest)
        assertEquals("{ foo }", graphQLRequest.query)
        assertNull(graphQLRequest.operationName)
        assertNull(graphQLRequest.variables)
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    @Test
    fun `parseRequest should return list of requests if method is POST with array body`() = runBlockingTest {
        val mockRequest1 = GraphQLRequest("query MyFoo { foo }", "MyFoo", mapOf("a" to 1))
        val mockRequest2 = GraphQLRequest("query MyBar { bar }", "MyBar")
        val mockRequest = GraphQLBatchRequest(listOf(mockRequest1, mockRequest2))

        val serverRequest = MockServerRequest.builder()
            .method(HttpMethod.POST)
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .body(Mono.justOrEmpty(mockRequest))

        val graphQLServerRequest = parser.parseRequest(serverRequest)
        assertNotNull(graphQLServerRequest)
        assertTrue(graphQLServerRequest is GraphQLBatchRequest)

        val graphQLRequests = graphQLServerRequest.requests
        assertEquals(2, graphQLRequests.size)

        val firstRequest = graphQLRequests[0]
        assertEquals("query MyFoo { foo }", firstRequest.query)
        assertEquals("MyFoo", firstRequest.operationName)
        assertEquals(1, firstRequest.variables?.get("a"))

        val secondRequest = graphQLRequests[1]
        assertEquals("query MyBar { bar }", secondRequest.query)
        assertEquals("MyBar", secondRequest.operationName)
        assertNull(secondRequest.variables)
    }
}
