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

package com.expediagroup.graphql.spring.exception

import graphql.execution.AbortExecutionException
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

internal class SimpleKotlinGraphQLErrorTest {

    @Test
    fun `Message defaults to value if it is not set in the exception`() {
        val error = SimpleKotlinGraphQLError(Throwable())
        assertNotNull(error.message)
    }

    @Test
    fun `Message comes from the exception if set`() {
        val error = SimpleKotlinGraphQLError(Throwable("foo"))
        assertEquals(expected = "foo", actual = error.message)
    }

    @Test
    fun `extensions default to an empty map if the exception is not a GraphQLError`() {
        val error = SimpleKotlinGraphQLError(Throwable())
        assertTrue(error.extensions.isEmpty())
    }

    @Test
    fun `extensions are empty if exception is a GraphQLError but extensions are null`() {
        val graphQLError: AbortExecutionException = mockk {
            every { extensions } returns null
        }
        val error = SimpleKotlinGraphQLError(graphQLError)
        assertTrue(error.extensions.isEmpty())
    }

    @Test
    fun `extensions are populated if exception is a GraphQLError and extensions are set`() {
        val graphQLError: AbortExecutionException = mockk {
            every { extensions } returns mapOf("foo" to "bar")
        }
        val error = SimpleKotlinGraphQLError(graphQLError)
        assertEquals(expected = "bar", actual = error.extensions["foo"])
    }
}
