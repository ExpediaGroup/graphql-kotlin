/*
 * Copyright 2023 Expedia, Inc
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

package com.expediagroup.graphql.server.ktor

import com.expediagroup.graphql.server.operations.Query
import com.expediagroup.graphql.server.types.GraphQLRequest
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.jackson.jackson
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.routing.Routing
import io.ktor.server.testing.testApplication
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class GraphQLPluginTest {

    class TestQuery : Query {
        fun hello(name: String?): String = if (name == null) {
            "Hello World"
        } else {
            "Hello $name"
        }
    }

    @Test
    fun `SDL route test`() {
        val expectedSchema = """
            schema {
              query: Query
            }

            "Marks the field, argument, input field or enum value as deprecated"
            directive @deprecated(
                "The reason for the deprecation"
                reason: String = "No longer supported"
              ) on FIELD_DEFINITION | ARGUMENT_DEFINITION | ENUM_VALUE | INPUT_FIELD_DEFINITION

            "Directs the executor to include this field or fragment only when the `if` argument is true"
            directive @include(
                "Included when true."
                if: Boolean!
              ) on FIELD | FRAGMENT_SPREAD | INLINE_FRAGMENT

            "Directs the executor to skip this field or fragment when the `if` argument is true."
            directive @skip(
                "Skipped when true."
                if: Boolean!
              ) on FIELD | FRAGMENT_SPREAD | INLINE_FRAGMENT

            "Exposes a URL that specifies the behaviour of this scalar."
            directive @specifiedBy(
                "The URL that specifies the behaviour of this scalar."
                url: String!
              ) on SCALAR

            type Query {
              hello(name: String): String!
            }
        """.trimIndent()
        testApplication {
            val response = client.get("/sdl")
            assertEquals(HttpStatusCode.OK, response.status)
            assertEquals(expectedSchema, response.bodyAsText().trim())
        }
    }

    @Test
    fun `server should handle valid GET requests`() {
        testApplication {
            val response = client.get("/graphql") {
                parameter("query", "query HelloQuery(\$name: String){ hello(name: \$name) }")
                parameter("operationName", "HelloQuery")
                parameter("variables", """{"name":"junit"}""")
            }
            assertEquals(HttpStatusCode.OK, response.status)
            assertEquals("""{"data":{"hello":"Hello junit"}}""", response.bodyAsText().trim())
        }
    }

    @Test
    fun `server should return Method Not Allowed for Mutation GET requests`() {
        testApplication {
            val response = client.get("/graphql") {
                parameter("query", "mutation { foo }")
            }
            assertEquals(HttpStatusCode.MethodNotAllowed, response.status)
        }
    }

    @Test
    fun `server should return Bad Request for invalid GET requests`() {
        testApplication {
            val response = client.get("/graphql")
            assertEquals(HttpStatusCode.BadRequest, response.status)
        }
    }

    @Test
    fun `server should handle valid POST requests`() {
        testApplication {
            val client = createClient {
                install(ContentNegotiation) {
                    jackson()
                }
            }
            val response = client.post("/graphql") {
                contentType(ContentType.Application.Json)
                setBody(GraphQLRequest(query = "query HelloQuery(\$name: String){ hello(name: \$name) }", operationName = "HelloQuery", variables = mapOf("name" to "junit")))
            }
            assertEquals(HttpStatusCode.OK, response.status)
            assertEquals("""{"data":{"hello":"Hello junit"}}""", response.bodyAsText().trim())
        }
    }

    @Test
    fun `server should return Bad Request for invalid POST requests`() {
        testApplication {
            val response = client.post("/graphql")
            assertEquals(HttpStatusCode.BadRequest, response.status)
        }
    }
}

fun Application.testGraphQLModule() {
    install(GraphQL) {
        schema {
            // packages property is read from application.conf
            queries = listOf(
                GraphQLPluginTest.TestQuery(),
            )
        }
    }
    install(Routing) {
        graphQLGetRoute()
        graphQLPostRoute()
        graphQLSDLRoute()
    }
}
