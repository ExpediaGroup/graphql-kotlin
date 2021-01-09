/*
 * Copyright 2019 Expedia, Inc
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

package com.expediagroup.graphql.server.spring.exception

import com.expediagroup.graphql.server.exception.KotlinGraphQLError
import graphql.execution.AbortExecutionException
import graphql.execution.DataFetcherExceptionHandlerParameters
import graphql.execution.ResultPath
import graphql.language.SourceLocation
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class KotlinDataFetcherExceptionHandlerTest {

    @Test
    fun `test exception handler does not modify existing GraphQLErrors`() {
        val parameters: DataFetcherExceptionHandlerParameters = mockk {
            every { exception } returns AbortExecutionException("my exception")
            every { sourceLocation } returns SourceLocation(1, 1)
            every { path } returns ResultPath.parse("/foo")
        }

        val handler = KotlinDataFetcherExceptionHandler()
        val result = handler.onException(parameters)

        assertNotNull(result.errors)
        assertEquals(expected = 1, actual = result.errors.size)
        val error = result.errors.first()
        assertTrue(error is KotlinGraphQLError)
        assertEquals("Exception while fetching data (foo) : my exception", error.message)
    }

    @Test
    fun `test exception handler wraps generic exceptions`() {
        val parameters: DataFetcherExceptionHandlerParameters = mockk {
            every { exception } returns Throwable("generic exception")
            every { sourceLocation } returns SourceLocation(1, 1)
            every { path } returns ResultPath.parse("/foo")
        }

        val handler = KotlinDataFetcherExceptionHandler()
        val result = handler.onException(parameters)

        assertNotNull(result.errors)
        assertEquals(expected = 1, actual = result.errors.size)
        val error = result.errors.first()
        assertTrue(error is KotlinGraphQLError)
        assertEquals("Exception while fetching data (foo) : generic exception", error.message)
    }
}
