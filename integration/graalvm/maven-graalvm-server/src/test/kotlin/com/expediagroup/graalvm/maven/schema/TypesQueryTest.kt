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
package com.expediagroup.graalvm.maven.schema

import com.expediagroup.graalvm.schema.TypesQuery
import com.expediagroup.graalvm.schema.model.InputOnly
import com.expediagroup.graphql.server.ktor.GraphQL
import com.expediagroup.graphql.server.ktor.graphQLPostRoute
import com.expediagroup.graphql.server.types.GraphQLRequest
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.jackson.jackson
import io.ktor.server.application.install
import io.ktor.server.routing.routing
import io.ktor.server.testing.testApplication
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class TypesQueryTest {

    @Test
    fun `verify input only query`() = testApplication {
        application {
            install(GraphQL) {
                schema {
                    packages = listOf("com.expediagroup.graalvm")
                    queries = listOf(TypesQuery())
                    typeHierarchy = emptyMap()
                }
            }
            routing {
                graphQLPostRoute()
            }
        }

        val client = createClient {
            install(ContentNegotiation) {
                jackson()
            }
        }
        val response = client.post("/graphql") {
            contentType(ContentType.Application.Json)
            setBody(GraphQLRequest(query = "query InputOnlyQuery(\$inputArg: InputOnlyInput){ inputTypeQuery(arg: \$inputArg) }", operationName = "InputOnlyQuery", variables = mapOf("inputArg" to InputOnly(id = 123))))
        }
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("""{"data":{"inputTypeQuery":"InputOnly(id=123)"}}""", response.bodyAsText().trim())
    }
}
