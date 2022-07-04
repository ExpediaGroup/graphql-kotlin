package com.expediagroup.graphql.examples.server.ktor

import com.expediagroup.graphql.server.types.GraphQLRequest
import com.expediagroup.graphql.server.types.GraphQLResponse
import com.expediagroup.graphql.server.types.GraphQLServerRequest
import graphql.introspection.IntrospectionQuery
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.jackson.*
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.testing.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class GraphQLModuleKtTest {

    @Test
    fun testHello() = testApplication {
        val client = httpClient()

        val response = client.executeGraphQlRequest(
            request = "query { hello }",
            variables = emptyMap(),
        )
        assertEquals("""{"data":{"hello":"World!"}}""", response.bodyAsText())
    }

    @Test
    fun testIntrospectionQuery() = testApplication {
        val client = httpClient()

        val response = client.executeGraphQlRequest(
            request = IntrospectionQuery.INTROSPECTION_QUERY,
            variables = emptyMap(),
        ).body<GraphQLResponse<Any>>()
        assertEquals(null, response.errors)
        println(response.data)
        assert(response.data != null)
    }

    suspend fun HttpClient.executeGraphQlRequest(
        request: String,
        variables: Map<String, String> = emptyMap(),
    ): HttpResponse {
        val endpoint = "/graphql"
        val graphQLRequest : GraphQLServerRequest = GraphQLRequest(
            query = request,
            variables = variables
        )
        return this.post(endpoint) {
            setBody(graphQLRequest)
            header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
        }
    }

    fun ApplicationTestBuilder.httpClient() = createClient {
        install(ContentNegotiation) {
            jackson()
        }
    }
}
