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

package com.expediagroup.graphql.apq.preparsed

import com.expediagroup.graphql.apq.fixture.ProductGraphQL
import graphql.ExecutionInput
import graphql.execution.preparsed.persisted.PersistedQueryNotFound
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class ApolloPersistedQuerySupportAsyncTest {
    @Test
    fun `Using ApolloPersistedQuerySupportAsync should return error when no query with provided hash is in the cache`() {

        // First execution fails to find persisted query string

        val extensions = mapOf(
            "persistedQuery" to mapOf(
                "version" to 1,
                "sha256Hash" to "2ec03b0d1d2e458ffafa173a7e965de18e1c91e7c28546f0ef093778ddeeb49c"
            )
        )
        val executionInputWithQueryId = ExecutionInput
            .newExecutionInput("")
            .extensions(extensions)
            .build()
        val firstResultWithQueryId = ProductGraphQL.execute(executionInputWithQueryId)

        assertEquals(firstResultWithQueryId.errors.size, 1)
        assertTrue(firstResultWithQueryId.errors[0].errorType is PersistedQueryNotFound)
        assertEquals("PersistedQueryNotFound", firstResultWithQueryId.errors[0].message)
        assertEquals("2ec03b0d1d2e458ffafa173a7e965de18e1c91e7c28546f0ef093778ddeeb49c", firstResultWithQueryId.errors[0].extensions["persistedQueryId"])
        assertEquals("graphql-java", firstResultWithQueryId.errors[0].extensions["generatedBy"])

        // Second execution persists query string and hash

        val executionInputWithQuery = ExecutionInput
            .newExecutionInput("{ product(id: 1) { summary { name } details { rating } } }")
            .extensions(extensions)
            .build()
        val resultWithQuery = ProductGraphQL.execute(executionInputWithQuery).toSpecification()

        assertNotNull(resultWithQuery["data"] as? Map<*, *>) { data ->
            assertNotNull(data["product"] as? Map<*, *>) { product ->
                assertNotNull(product["summary"] as? Map<*, *>) { summary ->
                    assertEquals(summary["name"], "Product 1")
                }
                assertNotNull(product["details"] as? Map<*, *>) { details ->
                    assertEquals(details["rating"], "4 out of 5")
                }
            }
        }

        // Third execution finds persisted query string

        val secondResultWithQueryId = ProductGraphQL.execute(executionInputWithQueryId).toSpecification()
        assertNotNull(secondResultWithQueryId["data"] as? Map<*, *>) { data ->
            assertNotNull(data["product"] as? Map<*, *>) { product ->
                assertNotNull(product["summary"] as? Map<*, *>) { summary ->
                    assertEquals(summary["name"], "Product 1")
                }
                assertNotNull(product["details"] as? Map<*, *>) { details ->
                    assertEquals(details["rating"], "4 out of 5")
                }
            }
        }
    }

    @Test
    fun `Using ApolloPersistedQuerySupportAsync should execute GraphQL operation normally when no persistedQueryId is provided`() {
        val executionInput = ExecutionInput
            .newExecutionInput("{ product(id: 1) { summary { name } details { rating } } }")
            .build()
        val result = ProductGraphQL.execute(executionInput).toSpecification()

        assertNotNull(result["data"] as? Map<*, *>) { data ->
            assertNotNull(data["product"] as? Map<*, *>) { product ->
                assertNotNull(product["summary"] as? Map<*, *>) { summary ->
                    assertEquals(summary["name"], "Product 1")
                }
                assertNotNull(product["details"] as? Map<*, *>) { details ->
                    assertEquals(details["rating"], "4 out of 5")
                }
            }
        }
    }
}
