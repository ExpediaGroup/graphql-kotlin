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
import com.expediagroup.graphql.generator.federation.execution.FederatedTypePromiseResolver
import graphql.schema.DataFetchingEnvironment
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import reactor.kotlin.core.publisher.toMono
import java.time.Duration
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FederatedTypePromiseResolverExecutorTest {
    @Test
    fun `resolver executor should call the resolver`() {
        val environment = mockk<DataFetchingEnvironment>()
        val mockResolverA = mockk<FederatedTypePromiseResolver<*>> {
            every { typeName } returns "MyTypeA"
            every { resolve(environment, mapOf("key" to "keyA1")) } returns "resultA1".toMono().delayElement(Duration.ofMillis(100)).toFuture()
            every { resolve(environment, mapOf("key" to "keyA2")) } returns "resultA2".toMono().delayElement(Duration.ofMillis(10)).toFuture()
        }
        val resolvableEntityA = ResolvableEntity(
            "MyTypeA",
            listOf(IndexedValue(1, mapOf("key" to "keyA1")), IndexedValue(2, mapOf("key" to "keyA2"))),
            mockResolverA
        )

        val mockResolverB = mockk<FederatedTypePromiseResolver<*>> {
            every { typeName } returns "MyTypeB"
            every { resolve(environment, mapOf("key" to "keyB1")) } returns "resultB1".toMono().delayElement(Duration.ofMillis(300)).toFuture()
            coEvery { resolve(environment, mapOf("key" to "keyB2")) } returns "resultB2".toMono().delayElement(Duration.ofMillis(30)).toFuture()
        }
        val resolvableEntityB = ResolvableEntity(
            "MyTypeB",
            listOf(IndexedValue(3, mapOf("key" to "keyB1")), IndexedValue(4, mapOf("key" to "keyB2"))),
            mockResolverB
        )

        val result = FederatedTypePromiseResolverExecutor.execute(
            listOf(resolvableEntityA, resolvableEntityB),
            environment
        ).get()
        assertEquals(2, result.size)
        assertEquals(
            mapOf(
                1 to "resultA1",
                2 to "resultA2"
            ),
            result[0]
        )
        assertEquals(
            mapOf(
                3 to "resultB1",
                4 to "resultB2"
            ),
            result[1]
        )
        verify(exactly = 2) {
            mockResolverA.resolve(any(), any())
            mockResolverB.resolve(any(), any())
        }
    }

    @Test
    fun `resolver maps the value to a failure when the federated resolver throws an exception`() {
        val representation = emptyMap<String, Any>()
        val indexedRequests: List<IndexedValue<Map<String, Any>>> = listOf(IndexedValue(7, representation))
        val mockResolver: FederatedTypePromiseResolver<*> = mockk {
            every { typeName } returns "MyType"
            every { resolve(any(), any()) } throws Exception("custom exception")
        }

        val resolvableEntity = ResolvableEntity("MyType", indexedRequests, mockResolver)
        val environment = mockk<DataFetchingEnvironment>()

        val result = FederatedTypePromiseResolverExecutor.execute(listOf(resolvableEntity), environment).get()
        assertTrue(result.isNotEmpty())
        val mappedValue = result.first()
        val response = mappedValue[7]
        assertTrue(response is FederatedRequestFailure)
        verify(exactly = 1) { mockResolver.resolve(any(), any()) }
    }
}
