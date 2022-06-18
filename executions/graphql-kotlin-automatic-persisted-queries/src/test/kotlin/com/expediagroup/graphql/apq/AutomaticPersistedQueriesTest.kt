package com.expediagroup.graphql.apq

import com.expediagroup.graphql.apq.fixture.ProductGraphQL
import graphql.ExecutionInput
import graphql.execution.preparsed.persisted.PersistedQueryNotFound
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class AutomaticPersistentQueriesTest {
    @Test
    fun `ApolloPersistedQuerySupport should return error when no query with provided hash is in the cache`() {

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
}
