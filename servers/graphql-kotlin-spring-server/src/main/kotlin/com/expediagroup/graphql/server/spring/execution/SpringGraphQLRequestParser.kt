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

package com.expediagroup.graphql.server.spring.execution

import com.expediagroup.graphql.server.execution.GraphQLRequestParser
import com.expediagroup.graphql.server.types.GraphQLRequest
import com.expediagroup.graphql.server.types.GraphQLServerRequest
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.type.MapType
import com.fasterxml.jackson.databind.type.TypeFactory
import kotlinx.coroutines.reactive.awaitFirst
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.awaitBody
import org.springframework.web.reactive.function.server.bodyToMono

internal const val REQUEST_PARAM_QUERY = "query"
internal const val REQUEST_PARAM_OPERATION_NAME = "operationName"
internal const val REQUEST_PARAM_VARIABLES = "variables"
internal val graphQLMediaType = MediaType("application", "graphql")

open class SpringGraphQLRequestParser(
    private val objectMapper: ObjectMapper
) : GraphQLRequestParser<ServerRequest> {

    private val mapTypeReference: MapType = TypeFactory.defaultInstance().constructMapType(HashMap::class.java, String::class.java, Any::class.java)

    override suspend fun parseRequest(request: ServerRequest): GraphQLServerRequest? = when {
        request.queryParam(REQUEST_PARAM_QUERY).isPresent -> { getRequestFromGet(request) }
        request.method().equals(HttpMethod.POST) -> { getRequestFromPost(request) }
        else -> null
    }

    private fun getRequestFromGet(serverRequest: ServerRequest): GraphQLServerRequest {
        val query = serverRequest.queryParam(REQUEST_PARAM_QUERY).get()
        val operationName: String? = serverRequest.queryParam(REQUEST_PARAM_OPERATION_NAME).orElseGet { null }
        val variables: String? = serverRequest.queryParam(REQUEST_PARAM_VARIABLES).orElseGet { null }
        val graphQLVariables: Map<String, Any>? = variables?.let {
            objectMapper.readValue(it, mapTypeReference)
        }

        return GraphQLRequest(query = query, operationName = operationName, variables = graphQLVariables)
    }

    /**
     * We have to suppress the warning due to a jackson issue
     * https://github.com/FasterXML/jackson-module-kotlin/issues/221
     */
    @Suppress("BlockingMethodInNonBlockingContext")
    private suspend fun getRequestFromPost(serverRequest: ServerRequest): GraphQLServerRequest? {
        val contentType = serverRequest.headers().contentType().orElse(MediaType.APPLICATION_JSON)
        return when {
            contentType.includes(MediaType.APPLICATION_JSON) -> serverRequest.bodyToMono<GraphQLServerRequest>().awaitFirst()
            contentType.includes(graphQLMediaType) -> GraphQLRequest(query = serverRequest.awaitBody())
            else -> null
        }
    }
}
