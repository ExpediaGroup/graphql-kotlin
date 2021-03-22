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

package com.expediagroup.graphql.server.extensions

import graphql.ErrorType
import graphql.ExecutionResult
import graphql.GraphQLError
import graphql.execution.AbortExecutionException
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ResponseExtensionsKtTest {

    @Test
    fun `null data, errors, and extensions can still be mapped`() {
        val executionResult: ExecutionResult = mockk {
            every { getData<Any>() } returns null
            every { errors } returns null
            every { extensions } returns null
        }

        val result = executionResult.toGraphQLResponse()

        assertNull(result.data)
        assertNull(result.errors)
        assertNull(result.extensions)
    }

    @Test
    fun `null errors or null extensions converts to null`() {
        val executionResult: ExecutionResult = mockk {
            every { getData<Any>() } returns mockk()
            every { errors } returns null
            every { extensions } returns null
        }

        val result = executionResult.toGraphQLResponse()

        assertNotNull(result.data)
        assertNull(result.errors)
        assertNull(result.extensions)
    }

    @Test
    fun `empty list for errors or empty map for extensions converts to null`() {
        val executionResult: ExecutionResult = mockk {
            every { getData<Any>() } returns mockk()
            every { errors } returns emptyList()
            every { extensions } returns emptyMap()
        }

        val result = executionResult.toGraphQLResponse()

        assertNotNull(result.data)
        assertNull(result.errors)
        assertNull(result.extensions)
    }

    @Test
    fun `if errors or extensions is set, the values are copied`() {
        val executionResult: ExecutionResult = mockk {
            every { getData<Any>() } returns mockk()
            every { errors } returns listOf(
                mockk(relaxed = true) {
                    every { message } returns "hello"
                }
            )
            every { extensions } returns mapOf("foo" to "bar")
        }

        val result = executionResult.toGraphQLResponse()

        assertNotNull(result.data)
        val errors = result.errors
        assertNotNull(errors)
        assertEquals(expected = "hello", actual = errors.firstOrNull()?.message)
        val extensions = result.extensions
        assertNotNull(extensions)
        assertEquals(expected = "bar", actual = extensions["foo"])
    }

    @Test
    fun `error due to an Exception is mapped as expected`() {
        val executionResult: ExecutionResult = mockk {
            every { getData<Any>() } returns mockk()
            every { errors } returns listOf(AbortExecutionException())
            every { extensions } returns mapOf("foo" to "bar")
        }

        val result = executionResult.toGraphQLResponse()

        assertNotNull(result.data)
        val errors = result.errors
        assertNotNull(errors)
        assertEquals(1, errors.size)
        val extensions = result.extensions
        assertNotNull(extensions)
        assertEquals(expected = "bar", actual = extensions["foo"])
    }

    @Test
    fun `the error type is set as the classification`() {
        val executionResult: ExecutionResult = mockk {
            every { getData<Any>() } returns mockk()
            every { errors } returns listOf(AbortExecutionException())
            every { extensions } returns emptyMap()
        }

        val result = executionResult.toGraphQLResponse()

        val error = assertNotNull(result.errors?.firstOrNull())
        val extensions = assertNotNull(error.extensions)
        assertEquals(expected = "ExecutionAborted", actual = extensions["classification"])
    }

    @Test
    fun `does not throw when error type is null`() {
        val mockError = mockk<GraphQLError> {
            every { message } returns ""
            every { path } returns null
            every { locations } returns null
            every { extensions } returns emptyMap()
            every { errorType } returns null
        }
        val executionResult: ExecutionResult = mockk {
            every { getData<Any>() } returns mockk()
            every { errors } returns listOf(mockError)
            every { extensions } returns emptyMap()
        }

        val result = executionResult.toGraphQLResponse()

        assertNotNull(result.data)
        val error = assertNotNull(result.errors?.firstOrNull())
        val extenstions = assertNotNull(error.extensions)
        assertTrue(extenstions.isEmpty())
    }

    @Test
    fun `the error classification is not overriden`() {
        val mockError = mockk<GraphQLError> {
            every { message } returns ""
            every { path } returns null
            every { locations } returns null
            every { extensions } returns mapOf("classification" to "foo")
            every { errorType } returns ErrorType.ExecutionAborted
        }
        val executionResult: ExecutionResult = mockk {
            every { getData<Any>() } returns mockk()
            every { errors } returns listOf(mockError)
            every { extensions } returns emptyMap()
        }

        val result = executionResult.toGraphQLResponse()

        assertNotNull(result.data)
        val error = assertNotNull(result.errors?.firstOrNull())
        val extensions = assertNotNull(error.extensions)
        assertEquals(expected = "foo", actual = extensions["classification"])
    }
}
