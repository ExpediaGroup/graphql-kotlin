package com.expediagroup.graphql.spring

import com.expediagroup.graphql.SchemaGeneratorConfig
import com.expediagroup.graphql.TopLevelObject
import com.expediagroup.graphql.annotations.GraphQLContext
import com.expediagroup.graphql.exceptions.GraphQLKotlinException
import com.expediagroup.graphql.spring.exception.SimpleKotlinGraphQLError
import com.expediagroup.graphql.spring.model.GraphQLRequest
import com.expediagroup.graphql.toSchema
import graphql.ErrorType
import graphql.ExecutionInput
import graphql.GraphQL
import graphql.schema.GraphQLSchema
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.reactor.asCoroutineContext
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.Test
import reactor.util.context.Context
import kotlin.random.Random
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class QueryHandlerTest {

    private val testSchema: GraphQLSchema = toSchema(
        config = SchemaGeneratorConfig(supportedPackages = listOf("com.expediagroup.graphql.spring")),
        queries = listOf(TopLevelObject(BasicQuery()))
    )
    private val testGraphQL: GraphQL = GraphQL.newGraphQL(testSchema).build()
    private val queryHandler = SimpleQueryHandler(testGraphQL)

    @Test
    @ExperimentalCoroutinesApi
    fun `execute graphQL query`() = runBlockingTest {
        val request = GraphQLRequest(query = "query { random }")

        val response = queryHandler.executeQuery(request)
        assertNotNull(response.data as? Map<*, *>) { data ->
            assertNotNull(data["random"] as? Int)
        }
        assertNull(response.errors)
        assertNull(response.extensions)
    }

    @Test
    @ExperimentalCoroutinesApi
    fun `execute graphQL query with arguments`() = runBlockingTest {
        val request = GraphQLRequest(query = "query { hello(name: \"JUNIT\") }")

        val response = queryHandler.executeQuery(request)
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
    fun `execute graphQL query with variables`() = runBlockingTest {
        val request = GraphQLRequest(
            query = "query helloWorldQuery(\$name: String!) { hello(name: \$name) }",
            variables = mapOf("name" to "JUNIT with variables"),
            operationName = "helloWorldQuery"
        )

        val response = queryHandler.executeQuery(request)
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
    fun `execute failing graphQL query`() = runBlockingTest {
        val request = GraphQLRequest(query = "query { alwaysThrows }")

        val response = queryHandler.executeQuery(request)
        assertNull(response.data)
        assertNotNull(response.errors) { errors ->
            assertEquals(1, errors.size)
            val error = errors.first()
            assertEquals("Exception while fetching data (/alwaysThrows) : JUNIT Failure", error.message)
            assertEquals(ErrorType.DataFetchingException, error.errorType)
        }
        assertNull(response.extensions)
    }

    @Test
    @ExperimentalCoroutinesApi
    fun `execute graphQL query with context`() = runBlockingTest(Context.of(GRAPHQL_CONTEXT_KEY, MyContext("JUNIT context value")).asCoroutineContext()) {
        val request = GraphQLRequest(query = "query { context }")

        val response = queryHandler.executeQuery(request)
        assertNotNull(response.data as? Map<*, *>) { data ->
            assertNotNull(data["context"] as? String) { msg ->
                assertEquals("JUNIT context value", msg)
            }
        }
        assertNull(response.errors)
        assertNull(response.extensions)
    }

    @Test
    @ExperimentalCoroutinesApi
    fun `execute graphQL query throwing uncaught exception`() = runBlockingTest {
        val mockGraphQL: GraphQL = mockk {
            every { executeAsync(any<ExecutionInput>()) } throws RuntimeException("Uncaught JUNIT")
        }
        val mockQueryHandler = SimpleQueryHandler(mockGraphQL)
        val response = mockQueryHandler.executeQuery(GraphQLRequest(query = "query { whatever }"))
        assertNull(response.data)
        assertNotNull(response.errors) { errors ->
            assertEquals(1, errors.size)
            val error = errors.first()
            assertTrue(error is SimpleKotlinGraphQLError)
            assertEquals(ErrorType.DataFetchingException, error.errorType)
            assertEquals("Uncaught JUNIT", error.message)
        }
        assertNull(response.extensions)
    }

    class BasicQuery {
        fun random(): Int = Random.nextInt()

        fun hello(name: String): String = "Hello $name!"

        fun alwaysThrows(): String = throw GraphQLKotlinException("JUNIT Failure")

        fun context(@GraphQLContext context: MyContext): String = context.value ?: "default"
    }

    data class MyContext(val value: String? = null)
}
