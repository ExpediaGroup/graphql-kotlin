package com.expediagroup.graphql.server.ktor

import com.expediagroup.graphql.server.types.GraphQLBatchRequest
import com.expediagroup.graphql.server.types.GraphQLRequest
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.ktor.http.HttpMethod
import io.ktor.server.request.ApplicationRequest
import io.ktor.server.testing.TestApplicationRequest
import io.ktor.utils.io.ByteReadChannel
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@ExperimentalCoroutinesApi
class KtorGraphQLRequestParserTest {
    private val mapper = jacksonObjectMapper()
    private val parser = KtorGraphQLRequestParser(mapper)

    @Test
    fun `parseRequest should return null if request method is not valid`() = runTest {
        val request = mockk<ApplicationRequest>(relaxed = true) {
            every { local.method } returns HttpMethod.Put
        }
        assertNull(parser.parseRequest(request))
    }

    @Test
    fun `parseRequest should throw IllegalStateException if request method is GET without query`() = runTest {
        val request = mockk<ApplicationRequest>(relaxed = true) {
            every { queryParameters[REQUEST_PARAM_QUERY] } returns null
            every { local.method } returns HttpMethod.Get
        }
        assertFailsWith<IllegalStateException> {
            parser.parseRequest(request)
        }
    }

    @Test
    fun `parseRequest should throw UnsupportedOperationException if request method is GET and specifies mutation operation`() =
        runTest {
            val request = mockk<ApplicationRequest>(relaxed = true) {
                every { queryParameters[REQUEST_PARAM_QUERY] } returns "mutation { foo }"
                every { local.method } returns HttpMethod.Get
            }
            assertFailsWith<UnsupportedOperationException> {
                parser.parseRequest(request)
            }
        }

    @Test
    fun `parseRequest should return request if method is GET with simple query`() = runTest {
        val serverRequest = mockk<ApplicationRequest>(relaxed = true) {
            every { queryParameters[REQUEST_PARAM_QUERY] } returns "{ foo }"
            every { queryParameters[REQUEST_PARAM_OPERATION_NAME] } returns null
            every { queryParameters[REQUEST_PARAM_VARIABLES] } returns null
            every { local.method } returns HttpMethod.Get
        }
        val graphQLRequest = parser.parseRequest(serverRequest)
        assertNotNull(graphQLRequest)
        assertTrue(graphQLRequest is GraphQLRequest)
        assertEquals("{ foo }", graphQLRequest.query)
        assertNull(graphQLRequest.operationName)
        assertNull(graphQLRequest.variables)
    }

    @Test
    fun `parseRequest should return request if method is GET with full query`() = runTest {
        val serverRequest = mockk<ApplicationRequest>(relaxed = true) {
            every { queryParameters[REQUEST_PARAM_QUERY] } returns "query MyFoo { foo }"
            every { queryParameters[REQUEST_PARAM_OPERATION_NAME] } returns "MyFoo"
            every { queryParameters[REQUEST_PARAM_VARIABLES] } returns """{"a":1}"""
            every { local.method } returns HttpMethod.Get
        }
        val graphQLRequest = parser.parseRequest(serverRequest)
        assertNotNull(graphQLRequest)
        assertTrue(graphQLRequest is GraphQLRequest)
        assertEquals("query MyFoo { foo }", graphQLRequest.query)
        assertEquals("MyFoo", graphQLRequest.operationName)
        assertEquals(1, graphQLRequest.variables?.get("a"))
    }

    @Test
    fun `parseRequest should return request if method is POST`() = runTest {
        val mockRequest = GraphQLRequest("query MyFoo { foo }", "MyFoo", mapOf("a" to 1))
        val serverRequest = mockk<TestApplicationRequest>(relaxed = true) {
            every { call } returns mockk(relaxed = true) {
                every { attributes.getOrNull<Any>(any()) } returns null
                coEvery { request.pipeline.execute(any(), any()) } returns ByteReadChannel(mapper.writeValueAsBytes(mockRequest))
            }
            every { local.method } returns HttpMethod.Post
        }

        val graphQLRequest = parser.parseRequest(serverRequest)
        assertNotNull(graphQLRequest)
        assertTrue(graphQLRequest is GraphQLRequest)
        assertEquals("query MyFoo { foo }", graphQLRequest.query)
        assertEquals("MyFoo", graphQLRequest.operationName)
        assertEquals(1, graphQLRequest.variables?.get("a"))
    }

    @Test
    fun `parseRequest should return list of requests if method is POST with array body`() = runTest {
        val mockRequest1 = GraphQLRequest("query MyFoo { foo }", "MyFoo", mapOf("a" to 1))
        val mockRequest2 = GraphQLRequest("query MyBar { bar }", "MyBar")
        val mockRequest = GraphQLBatchRequest(listOf(mockRequest1, mockRequest2))

        val serverRequest = mockk<TestApplicationRequest>(relaxed = true) {
            every { call } returns mockk(relaxed = true) {
                every { attributes.getOrNull<Any>(any()) } returns null
                coEvery { request.pipeline.execute(any(), any()) } returns ByteReadChannel(mapper.writeValueAsBytes(mockRequest))
            }
            every { local.method } returns HttpMethod.Post
        }

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

/*
    @Suppress("BlockingMethodInNonBlockingContext")

 */
