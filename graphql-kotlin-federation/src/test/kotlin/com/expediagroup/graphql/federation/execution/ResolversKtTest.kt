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

package com.expediagroup.graphql.federation.execution

import com.expediagroup.graphql.federation.exception.FederatedRequestFailure
import com.expediagroup.graphql.federation.exception.InvalidFederatedRequest
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ResolversKtTest {

    @Test
    fun `resolveBatch should call the resolver from the registry`() {
        val indexedValue = mapOf<String, Any>()
        val indexedRequests: List<IndexedValue<Map<String, Any>>> = listOf(IndexedValue(7, indexedValue))
        val mockResolver: FederatedTypeResolver<*> = mockk {
            every { typeName } returns "MyType"
            coEvery { resolve(any(), any()) } returns listOf("foo")
        }
        val resolverMap = mapOf("MyType" to mockResolver)

        runBlocking {
            val result = resolveType(mockk(), "MyType", indexedRequests, resolverMap)
            assertTrue(result.isNotEmpty())
            assertEquals(expected = 7 to "foo", actual = result.first())
            coVerify(exactly = 1) { mockResolver.resolve(any(), any()) }
        }
    }

    @Test
    fun `resolver still works when registry returns null`() {
        val indexedValue = mapOf<String, Any>()
        val indexedRequests: List<IndexedValue<Map<String, Any>>> = listOf(IndexedValue(7, indexedValue))
        val mockResolver: FederatedTypeResolver<*> = mockk {
            coEvery { resolve(any(), any()) } returns listOf("foo")
        }

        runBlocking {
            val result = resolveType(mockk(), "MyType", indexedRequests, emptyMap())
            assertTrue(result.isNotEmpty())
            val mappedValue = result.first()
            val response = mappedValue.second
            assertTrue(response is InvalidFederatedRequest)
            coVerify(exactly = 0) { mockResolver.resolve(any(), any()) }
        }
    }

    @Test
    fun `resolver maps the value to a failure when the federated resolver throws an exception`() {
        val indexedValue = mapOf<String, Any>()
        val indexedRequests: List<IndexedValue<Map<String, Any>>> = listOf(IndexedValue(7, indexedValue))
        val mockResolver: FederatedTypeResolver<*> = mockk {
            every { typeName } returns "MyType"
            coEvery { resolve(any(), any()) } throws Exception("custom exception")
        }
        val resolverMap = mapOf("MyType" to mockResolver)

        runBlocking {
            val result = resolveType(mockk(), "MyType", indexedRequests, resolverMap)
            assertTrue(result.isNotEmpty())
            val mappedValue = result.first()
            val response = mappedValue.second
            assertTrue(response is FederatedRequestFailure)
            coVerify(exactly = 1) { mockResolver.resolve(any(), any()) }
        }
    }

    @Test
    fun `maps to failure if the result size does not match request size`() {
        val indexedValue = mapOf<String, Any>()
        val indexedRequests: List<IndexedValue<Map<String, Any>>> = listOf(
            IndexedValue(7, indexedValue),
            IndexedValue(5, indexedValue)
        )
        val mockResolver: FederatedTypeResolver<*> = mockk {
            every { typeName } returns "MyType"
            coEvery { resolve(any(), any()) } returns listOf("foo")
        }
        val registry = mapOf("MyType" to mockResolver)

        runBlocking {
            val result = resolveType(mockk(), "MyType", indexedRequests, registry)
            assertEquals(expected = 2, actual = result.size)
            val mappedValue = result.first()
            val response = mappedValue.second
            assertTrue(response is FederatedRequestFailure)
            coVerify(exactly = 1) { mockResolver.resolve(any(), any()) }
        }
    }
}
