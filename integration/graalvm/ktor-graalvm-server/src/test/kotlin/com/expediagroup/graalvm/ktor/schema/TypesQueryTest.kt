package com.expediagroup.graalvm.ktor.schema

import com.expediagroup.graalvm.ktor.schema.model.InputOnly
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
                    packages = listOf("com.expediagroup.graalvm.ktor.schema")
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
