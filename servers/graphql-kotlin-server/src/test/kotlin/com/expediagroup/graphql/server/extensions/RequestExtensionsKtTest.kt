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

package com.expediagroup.graphql.server.extensions

import com.expediagroup.graphql.server.types.GraphQLRequest
import io.mockk.mockk
import org.dataloader.DataLoaderRegistry
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class RequestExtensionsKtTest {

    @Test
    fun `verify can convert simple request to execution input with defaults`() {
        val request = GraphQLRequest(query = "query { whatever }")
        val executionInput = request.toExecutionInput()
        assertEquals(request.query, executionInput.query)
        assertNotNull(executionInput.variables)
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
    fun `verify can convert request with data loader registry to execution input`() {
        val request = GraphQLRequest(query = "query { whatever }")
        val dataLoaderRegistry = DataLoaderRegistry()
        dataLoaderRegistry.register("abc", mockk())

        val executionInput = request.toExecutionInput(dataLoaderRegistry = dataLoaderRegistry)
        assertEquals(request.query, executionInput.query)
        assertEquals(dataLoaderRegistry, executionInput.dataLoaderRegistry)
    }

    @Test
    fun `verify can convert request with context map to execution input`() {
        val request = GraphQLRequest(query = "query { whatever }")
        val context = mapOf("foo" to 1)

        val executionInput = request.toExecutionInput(graphQLContextMap = context)
        assertEquals(1, executionInput.graphQLContext.get("foo"))
    }
}
