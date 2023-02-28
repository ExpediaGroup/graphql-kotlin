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

import com.expediagroup.graphql.server.execution.GraphQLRequestParser
import com.expediagroup.graphql.server.types.GraphQLRequest
import com.expediagroup.graphql.server.types.GraphQLServerRequest
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.type.MapType
import com.fasterxml.jackson.databind.type.TypeFactory
import io.ktor.http.HttpMethod
import io.ktor.server.request.ApplicationRequest
import io.ktor.server.request.receiveText
import java.io.IOException

internal const val REQUEST_PARAM_QUERY = "query"
internal const val REQUEST_PARAM_OPERATION_NAME = "operationName"
internal const val REQUEST_PARAM_VARIABLES = "variables"

/**
 * GraphQL Ktor [ApplicationRequest] parser.
 */
class KtorGraphQLRequestParser(
    private val mapper: ObjectMapper
) : GraphQLRequestParser<ApplicationRequest> {

    private val mapTypeReference: MapType = TypeFactory.defaultInstance().constructMapType(HashMap::class.java, String::class.java, Any::class.java)
//    private val graphQLContentType: ContentType = ContentType.parse("application/graphql-response+json")

    override suspend fun parseRequest(request: ApplicationRequest): GraphQLServerRequest? = when (request.local.method) {
        HttpMethod.Get -> parseGetRequest(request)
        HttpMethod.Post -> parsePostRequest(request)
        else -> null
    }

    private fun parseGetRequest(request: ApplicationRequest): GraphQLServerRequest? {
        val query = request.queryParameters[REQUEST_PARAM_QUERY] ?: throw IllegalStateException("Invalid HTTP request - GET request has to specify query parameter")
        if (query.startsWith("mutation ") || query.startsWith("subscription ")) {
            throw UnsupportedOperationException("Invalid GraphQL operation - only queries are supported for GET requests")
        }
        val operationName: String? = request.queryParameters[REQUEST_PARAM_OPERATION_NAME]
        val variables: String? = request.queryParameters[REQUEST_PARAM_VARIABLES]
        val graphQLVariables: Map<String, Any>? = variables?.let {
            mapper.readValue(it, mapTypeReference)
        }
        return GraphQLRequest(query = query, operationName = operationName, variables = graphQLVariables)
    }

    private suspend fun parsePostRequest(request: ApplicationRequest): GraphQLServerRequest? = try {
        val rawRequest = request.call.receiveText()
        mapper.readValue(rawRequest, GraphQLServerRequest::class.java)
    } catch (e: IOException) {
        throw IllegalStateException("Invalid HTTP request - unable to parse GraphQL request from POST payload")
    }
}
