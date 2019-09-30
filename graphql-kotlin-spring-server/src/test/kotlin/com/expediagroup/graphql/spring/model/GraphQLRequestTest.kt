package com.expediagroup.graphql.spring.model

import graphql.GraphQLContext
import io.mockk.mockk
import org.dataloader.DataLoaderRegistry
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class GraphQLRequestTest {

    @Test
    fun `verify can convert simple request to execution input with defaults`() {
        val request = GraphQLRequest(query = "query { whatever }")
        val executionInput = request.toExecutionInput()
        assertEquals(request.query, executionInput.query)
        assertTrue(executionInput.context is GraphQLContext)
        assertNotNull(executionInput.dataLoaderRegistry)
    }

    @Test
    fun `verify can convert request with variables to execution input`() {
        val request = GraphQLRequest(
            query = "query testOperation{ whatever(id: \$id) }",
            variables = mapOf("id" to 123),
            operationName = "testOperation"
        )
        val executionInput = request.toExecutionInput()
        assertEquals(request.query, executionInput.query)
        assertEquals(request.variables, executionInput.variables)
        assertEquals(request.operationName, executionInput.operationName)
    }

    @Test
    fun `verify can convert request with context to execution input`() {
        val request = GraphQLRequest(query = "query { whatever }")
        val context = mapOf("contextValue" to 12_345)
        val executionInput = request.toExecutionInput(graphQLContext = context)
        assertEquals(request.query, executionInput.query)
        assertEquals(context, executionInput.context)
    }

    @Test
    fun `verify can convert request with data loader registry to execution input`() {
        val request = GraphQLRequest(query = "query { whatever }")
        val dataLoaderRegistry = DataLoaderRegistry()
        dataLoaderRegistry.register("abc", mockk())

        val executionInput = request.toExecutionInput(dataLoaderRegistry = dataLoaderRegistry)
        assertEquals(request.query, executionInput.query)
        assertEquals(dataLoaderRegistry, executionInput.dataLoaderRegistry)
    }
}
