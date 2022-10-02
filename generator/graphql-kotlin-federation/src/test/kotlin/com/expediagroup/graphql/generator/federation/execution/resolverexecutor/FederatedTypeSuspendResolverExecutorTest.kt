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
import com.expediagroup.graphql.generator.federation.execution.FederatedTypeSuspendResolver
import graphql.GraphQLContext
import graphql.schema.DataFetchingEnvironment
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.delay
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class FederatedTypeSuspendResolverExecutorTest {
    @Test
    fun `resolver executor should invoke the resolver and provide results in order`() {
        val environment = mockk<DataFetchingEnvironment> {
            every { graphQlContext } returns GraphQLContext.newContext().build()
        }
        val mockResolverA = mockk<FederatedTypeSuspendResolver<*>> {
            every { typeName } returns "MyTypeA"
            coEvery { resolve(environment, mapOf("key" to "keyA1")) } coAnswers {
                delay(100)
                "resultA1"
            }
            coEvery { resolve(environment, mapOf("key" to "keyA2")) } coAnswers {
                delay(10)
                "resultA2"
            }
        }
        val resolvableEntityA = ResolvableEntity(
            "MyTypeA",
            listOf(IndexedValue(1, mapOf("key" to "keyA1")), IndexedValue(2, mapOf("key" to "keyA2"))),
            mockResolverA
        )

        val mockResolverB = mockk<FederatedTypeSuspendResolver<*>> {
            every { typeName } returns "MyTypeB"
            coEvery { resolve(environment, mapOf("key" to "keyB1")) } coAnswers {
                delay(300)
                "resultB1"
            }
            coEvery { resolve(environment, mapOf("key" to "keyB2")) } coAnswers {
                delay(30)
                "resultB2"
            }
        }
        val resolvableEntityB = ResolvableEntity(
            "MyTypeB",
            listOf(IndexedValue(3, mapOf("key" to "keyB1")), IndexedValue(4, mapOf("key" to "keyB2"))),
            mockResolverB
        )

        val result = FederatedTypeSuspendResolverExecutor.execute(
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
        coVerify(exactly = 2) {
            mockResolverA.resolve(any(), any())
            mockResolverB.resolve(any(), any())
        }
    }

    @Test
    fun `resolver executor maps the value to a failure when the resolver throws an exception`() {
        val representation1 = mapOf("__typename" to "MyType", "id" to 1)
        val representation2 = mapOf("__typename" to "MyType", "id" to 2)

        val indexedRequests: List<IndexedValue<Map<String, Any>>> = listOf(
            IndexedValue(1, representation1),
            IndexedValue(2, representation2)
        )

        val mockResolver = mockk<FederatedTypeSuspendResolver<*>> {
            every { typeName } returns "MyType"
            coEvery { resolve(any(), representation1) } returns "MyType1"
            coEvery { resolve(any(), representation2) } throws Exception("custom exception")
        }

        val resolvableEntity = ResolvableEntity("MyType", indexedRequests, mockResolver)
        val environment = mockk<DataFetchingEnvironment> {
            every { graphQlContext } returns GraphQLContext.newContext().build()
        }

        val result = FederatedTypeSuspendResolverExecutor.execute(listOf(resolvableEntity), environment).get()
        assertEquals(1, result.size)

        val resolverResults = result[0]

        assertEquals("MyType1", resolverResults[1])
        assertIs<FederatedRequestFailure>(resolverResults[2])

        coVerify(exactly = 1) {
            mockResolver.resolve(any(), representation1)
            mockResolver.resolve(any(), representation2)
        }
    }
}
