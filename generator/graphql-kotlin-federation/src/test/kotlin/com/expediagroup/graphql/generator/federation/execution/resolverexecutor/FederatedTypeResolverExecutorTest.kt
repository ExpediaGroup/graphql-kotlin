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

package com.expediagroup.graphql.generator.federation.execution.resolverexecutor

import com.expediagroup.graphql.generator.federation.exception.FederatedRequestFailure
import com.expediagroup.graphql.generator.federation.execution.FederatedTypeResolver
import graphql.GraphQLContext
import graphql.schema.DataFetchingEnvironment
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FederatedTypeResolverExecutorTest {
    @Test
    fun `resolver executor should call the resolver`() {
        val indexedValue = mapOf<String, Any>()
        val indexedRequests: List<IndexedValue<Map<String, Any>>> = listOf(IndexedValue(7, indexedValue))
        val mockResolver: FederatedTypeResolver<*> = mockk {
            every { typeName } returns "MyType"
            coEvery { resolve(any(), any()) } returns listOf("foo")
        }

        val resolvableEntity = ResolvableEntity("MyType", indexedRequests, mockResolver)
        val environment = mockk<DataFetchingEnvironment> {
            every { graphQlContext } returns GraphQLContext.newContext().build()
        }

        val result = FederatedTypeResolverExecutor.execute(listOf(resolvableEntity), environment).get()
        assertTrue(result.isNotEmpty())
        assertEquals(mapOf(7 to "foo"), result.first())
        coVerify(exactly = 1) { mockResolver.resolve(any(), any()) }
    }

    @Test
    fun `resolver maps the value to a failure when the federated resolver throws an exception`() {
        val indexedValue = mapOf<String, Any>()
        val indexedRequests: List<IndexedValue<Map<String, Any>>> = listOf(IndexedValue(7, indexedValue))
        val mockResolver: FederatedTypeResolver<*> = mockk {
            every { typeName } returns "MyType"
            coEvery { resolve(any(), any()) } throws Exception("custom exception")
        }

        val resolvableEntity = ResolvableEntity("MyType", indexedRequests, mockResolver)
        val environment = mockk<DataFetchingEnvironment> {
            every { graphQlContext } returns GraphQLContext.newContext().build()
        }

        val result = FederatedTypeResolverExecutor.execute(listOf(resolvableEntity), environment).get()
        assertTrue(result.isNotEmpty())
        val mappedValue = result.first()
        val response = mappedValue[7]
        assertTrue(response is FederatedRequestFailure)
        coVerify(exactly = 1) { mockResolver.resolve(any(), any()) }
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

        val resolvableEntity = ResolvableEntity("MyType", indexedRequests, mockResolver)
        val environment = mockk<DataFetchingEnvironment> {
            every { graphQlContext } returns GraphQLContext.newContext().build()
        }

        val result = FederatedTypeResolverExecutor.execute(listOf(resolvableEntity), environment).get()
        val myTypeResults = result.first()
        assertEquals(2, myTypeResults.size)
        myTypeResults.forEach { (_, resultForIndex) ->
            assertTrue(resultForIndex is FederatedRequestFailure)
        }
        coVerify(exactly = 1) { mockResolver.resolve(any(), any()) }
    }
}
