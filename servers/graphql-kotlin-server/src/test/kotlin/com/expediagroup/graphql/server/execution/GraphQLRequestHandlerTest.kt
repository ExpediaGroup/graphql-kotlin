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

package com.expediagroup.graphql.server.execution

import com.expediagroup.graphql.generator.SchemaGeneratorConfig
import com.expediagroup.graphql.generator.TopLevelObject
import com.expediagroup.graphql.generator.execution.GraphQLContext
import com.expediagroup.graphql.generator.toSchema
import com.expediagroup.graphql.server.types.GraphQLRequest
import com.expediagroup.graphql.server.types.GraphQLResponse
import graphql.ExecutionInput
import graphql.GraphQL
import graphql.execution.AbortExecutionException
import graphql.execution.instrumentation.ChainedInstrumentation
import graphql.schema.DataFetchingEnvironment
import graphql.schema.GraphQLSchema
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.random.Random
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class GraphQLRequestHandlerTest {

    private val testSchema: GraphQLSchema = toSchema(
        config = SchemaGeneratorConfig(supportedPackages = listOf("com.expediagroup.graphql.server.execution")),
        queries = listOf(TopLevelObject(BasicQuery()))
    )
    private val testGraphQL: GraphQL = GraphQL.newGraphQL(testSchema).build()
    private val graphQLRequestHandler = GraphQLRequestHandler(testGraphQL)

    @Test
    @ExperimentalCoroutinesApi
    fun `execute graphQL query`() = runTest {
        val request = GraphQLRequest(query = "query { random }")

        val response = graphQLRequestHandler.executeRequest(request) as GraphQLResponse<*>
        assertNotNull(response.data as? Map<*, *>) { data ->
            assertNotNull(data["random"] as? Int)
        }
        assertNull(response.errors)
        assertNull(response.extensions)
    }

    @Test
    @ExperimentalCoroutinesApi
    fun `execute graphQL query with arguments`() = runTest {
        val request = GraphQLRequest(query = "query { hello(name: \"JUNIT\") }")

        val response = graphQLRequestHandler.executeRequest(request) as GraphQLResponse<*>
        assertNotNull(response.data as? Map<*, *>) { data ->
            assertNotNull(data["hello"] as? String) { msg ->
                assertEquals("Hello JUNIT!", msg)
            }
        }
        assertNull(response.errors)
        assertNull(response.extensions)
    }

    @Test
    @ExperimentalCoroutinesApi
    fun `execute graphQL query with variables`() {
        val response = runBlocking {
            val request = GraphQLRequest(
                query = "query helloWorldQuery(\$name: String!) { hello(name: \$name) }",
                variables = mapOf("name" to "JUNIT with variables"),
                operationName = "helloWorldQuery"
            )
            graphQLRequestHandler.executeRequest(request) as GraphQLResponse<*>
        }

        assertNotNull(response.data as? Map<*, *>) { data ->
            assertNotNull(data["hello"] as? String) { msg ->
                assertEquals("Hello JUNIT with variables!", msg)
            }
        }
        assertNull(response.errors)
        assertNull(response.extensions)
    }

    @Test
    @ExperimentalCoroutinesApi
    fun `execute failing graphQL query`() {
        val response = runBlocking {
            val request = GraphQLRequest(query = "query { alwaysThrows }")
            graphQLRequestHandler.executeRequest(request) as GraphQLResponse<*>
        }

        assertNull(response.data)
        assertNotNull(response.errors) { errors ->
            assertEquals(1, errors.size)
            val error = errors.first()
            assertEquals("Exception while fetching data (/alwaysThrows) : JUNIT Failure", error.message)
        }
        assertNull(response.extensions)
    }

    @Test
    @ExperimentalCoroutinesApi
    fun `execute graphQL query with context`() {
        val response = runBlocking {
            val context = MyContext("JUNIT context value")
            val request = GraphQLRequest(query = "query { contextualValue }")
            graphQLRequestHandler.executeRequest(request, context) as GraphQLResponse<*>
        }

        assertNotNull(response.data as? Map<*, *>) { data ->
            assertNotNull(data["contextualValue"] as? String) { msg ->
                assertEquals("JUNIT context value", msg)
            }
        }
        assertNull(response.errors)
        assertNull(response.extensions)
    }

    @Test
    @ExperimentalCoroutinesApi
    fun `execute graphQL query with graphql context map`() {
        val response = runBlocking {
            val context = mapOf("foo" to "JUNIT context value")
            val request = GraphQLRequest(query = "query { graphQLContextualValue }")
            graphQLRequestHandler.executeRequest(
                request,
                context = null,
                graphQLContext = context
            ) as GraphQLResponse<*>
        }

        assertNotNull(response.data as? Map<*, *>) { data ->
            assertNotNull(data["graphQLContextualValue"] as? String) { msg ->
                assertEquals("JUNIT context value", msg)
            }
        }
        assertNull(response.errors)
        assertNull(response.extensions)
    }

    @Test
    @ExperimentalCoroutinesApi
    fun `execute graphQL query throwing uncaught exception`() {
        val response = runBlocking {
            val mockGraphQL = mockk<GraphQL> {
                every { executeAsync(any<ExecutionInput>()) } throws RuntimeException("Uncaught JUNIT")
                every { instrumentation } returns ChainedInstrumentation()
            }
            val mockQueryHandler = GraphQLRequestHandler(mockGraphQL)
            mockQueryHandler.executeRequest(
                GraphQLRequest(query = "query { whatever }")
            ) as GraphQLResponse<*>
        }

        assertNull(response.data)
        assertNotNull(response.errors) { errors ->
            assertEquals(1, errors.size)
            assertEquals("Uncaught JUNIT", errors.first().message)
        }
        assertNull(response.extensions)
    }

    @Test
    @ExperimentalCoroutinesApi
    fun `execute graphQL query throwing uncaught graphql exception`() {
        val response = runBlocking {
            val mockGraphQL = mockk<GraphQL> {
                every { executeAsync(any<ExecutionInput>()) } throws AbortExecutionException("Uncaught abort exception")
                every { instrumentation } returns ChainedInstrumentation()
            }
            val mockQueryHandler = GraphQLRequestHandler(mockGraphQL)
            mockQueryHandler.executeRequest(
                GraphQLRequest(query = "query { whatever }")
            )  as GraphQLResponse<*>
        }

        assertNull(response.data)
        assertNotNull(response.errors) { errors ->
            assertEquals(1, errors.size)
            val error = errors.first()
            assertEquals("Uncaught abort exception", error.message)
        }
        assertNull(response.extensions)
    }

    class BasicQuery {
        fun random(): Int = Random.nextInt()

        fun hello(name: String): String = "Hello $name!"

        fun alwaysThrows(): String = throw Exception("JUNIT Failure")

        fun contextualValue(context: MyContext): String = context.value ?: "default"

        fun graphQLContextualValue(dataFetchingEnvironment: DataFetchingEnvironment): String = dataFetchingEnvironment.graphQlContext.get("foo") ?: "default"
    }

    data class MyContext(val value: String? = null) : GraphQLContext
}
