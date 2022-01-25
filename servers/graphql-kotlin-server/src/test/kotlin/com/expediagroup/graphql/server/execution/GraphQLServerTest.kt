/*
 * Copyright 2022 Expedia, Inc
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

package com.expediagroup.graphql.server.execution

import com.expediagroup.graphql.server.types.GraphQLBatchRequest
import com.expediagroup.graphql.server.types.GraphQLRequest
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.Test

@ExperimentalCoroutinesApi
class GraphQLServerTest {

    class MockHttpRequest

    @Test
    fun `the request handler and parser are called`() {
        val mockParser = mockk<GraphQLRequestParser<MockHttpRequest>> {
            coEvery { parseRequest(any()) } returns GraphQLBatchRequest(requests = listOf(mockk()))
        }
        val mockContextFactory = mockk<GraphQLContextFactory<MockHttpRequest>> {
            coEvery { generateContextMap(any()) } returns mapOf("foo" to 1)
        }
        val mockHandler = mockk<GraphQLRequestHandler> {
            coEvery { executeRequest(any(), any()) } returns mockk()
        }

        val server = GraphQLServer(mockParser, mockContextFactory, mockHandler)

        runBlockingTest { server.execute(mockk()) }

        coVerify(exactly = 1) {
            mockParser.parseRequest(any())
            mockHandler.executeRequest(any(), any())
        }
    }

    @Test
    fun `null context map is used and passed to the request handler`() {
        val mockParser = mockk<GraphQLRequestParser<MockHttpRequest>> {
            coEvery { parseRequest(any()) } returns mockk<GraphQLRequest>()
        }
        val mockContextFactory = mockk<GraphQLContextFactory<MockHttpRequest>> {
            coEvery { generateContextMap(any()) } returns null
        }
        val mockHandler = mockk<GraphQLRequestHandler> {
            coEvery { executeRequest(any(), any()) } returns mockk()
        }

        val server = GraphQLServer(mockParser, mockContextFactory, mockHandler)

        runBlockingTest { server.execute(mockk()) }

        coVerify(exactly = 1) {
            mockParser.parseRequest(any())
            mockContextFactory.generateContextMap(any())
            mockHandler.executeRequest(any(), any())
        }
    }

    @Test
    fun `returns null if the request handler returns null`() {
        val mockParser = mockk<GraphQLRequestParser<MockHttpRequest>> {
            coEvery { parseRequest(any()) } returns null
        }
        val mockContextFactory = mockk<GraphQLContextFactory<MockHttpRequest>> {
            coEvery { generateContextMap(any()) } returns null
        }
        val mockHandler = mockk<GraphQLRequestHandler> {
            coEvery { executeRequest(any(), any()) } returns mockk()
        }

        val server = GraphQLServer(mockParser, mockContextFactory, mockHandler)

        runBlockingTest { server.execute(mockk()) }

        coVerify(exactly = 1) {
            mockParser.parseRequest(any())
        }
        coVerify(exactly = 0) {
            mockContextFactory.generateContextMap(any())
            mockHandler.executeRequest(any(), any())
        }
    }
}
