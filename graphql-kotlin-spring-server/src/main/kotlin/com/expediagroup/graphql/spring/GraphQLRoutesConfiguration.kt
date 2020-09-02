/*
 * Copyright 2019 Expedia, Inc
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

package com.expediagroup.graphql.spring

import com.expediagroup.graphql.spring.execution.QueryHandler
import com.expediagroup.graphql.types.GraphQLRequest
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.type.MapType
import com.fasterxml.jackson.databind.type.TypeFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.awaitBody
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import org.springframework.web.reactive.function.server.buildAndAwait
import org.springframework.web.reactive.function.server.coRouter
import org.springframework.web.reactive.function.server.json

internal const val REQUEST_PARAM_QUERY = "query"
internal const val REQUEST_PARAM_OPERATION_NAME = "operationName"
internal const val REQUEST_PARAM_VARIABLES = "variables"
internal val graphQLMediaType = MediaType("application", "graphql")

/**
 * Default route configuration for GraphQL endpoints.
 * Can handle requests over GET or POST as per the following guidelines:
 * https://graphql.org/learn/serving-over-http/
 */
@Configuration
@Import(GraphQLSchemaConfiguration::class)
class GraphQLRoutesConfiguration(
    private val config: GraphQLConfigurationProperties,
    private val queryHandler: QueryHandler,
    private val objectMapper: ObjectMapper
) {

    private val mapTypeReference: MapType = TypeFactory.defaultInstance().constructMapType(HashMap::class.java, String::class.java, Any::class.java)

    @Bean
    fun graphQLRoutes() = coRouter {
        val isEndpointRequest = POST(config.endpoint) or GET(config.endpoint)
        val isNotWebsocketRequest = headers { isWebSocketHeaders(it) }.not()

        (isEndpointRequest and isNotWebsocketRequest).invoke { serverRequest ->
            val graphQLRequest = createGraphQLRequest(serverRequest)
            if (graphQLRequest != null) {
                val graphQLResponse = queryHandler.executeQuery(graphQLRequest)
                ok().json().bodyValueAndAwait(graphQLResponse)
            } else {
                badRequest().buildAndAwait()
            }
        }
    }

    /**
     * These headers are defined in the HTTP Protocol upgrade mechanism that identify a web socket request
     * https://developer.mozilla.org/en-US/docs/Web/HTTP/Protocol_upgrade_mechanism
     */
    private fun isWebSocketHeaders(headers: ServerRequest.Headers): Boolean {
        val isUpgrade = requestContainsHeader(headers, "Connection", "Upgrade")
        val isWebSocket = requestContainsHeader(headers, "Upgrade", "websocket")
        return isUpgrade and isWebSocket
    }

    private fun requestContainsHeader(headers: ServerRequest.Headers, headerName: String, headerValue: String): Boolean =
        headers.header(headerName).map { it.toLowerCase() }.contains(headerValue.toLowerCase())

    private suspend fun createGraphQLRequest(serverRequest: ServerRequest): GraphQLRequest? = when {
        serverRequest.queryParam(REQUEST_PARAM_QUERY).isPresent -> { getRequestFromGet(serverRequest) }
        serverRequest.method() == HttpMethod.POST -> { getRequestFromPost(serverRequest) }
        else -> null
    }

    private fun getRequestFromGet(serverRequest: ServerRequest): GraphQLRequest {
        val query = serverRequest.queryParam(REQUEST_PARAM_QUERY).get()
        val operationName: String? = serverRequest.queryParam(REQUEST_PARAM_OPERATION_NAME).orElseGet { null }
        val variables: String? = serverRequest.queryParam(REQUEST_PARAM_VARIABLES).orElseGet { null }
        val graphQLVariables: Map<String, Any>? = variables?.let {
            objectMapper.readValue(it, mapTypeReference)
        }

        return GraphQLRequest(query = query, operationName = operationName, variables = graphQLVariables)
    }

    /**
     * We have have to suppress the warning due to a jackson issue
     * https://github.com/FasterXML/jackson-module-kotlin/issues/221
     */
    @Suppress("BlockingMethodInNonBlockingContext")
    private suspend fun getRequestFromPost(serverRequest: ServerRequest): GraphQLRequest? {
        val contentType = serverRequest.headers().contentType().orElse(MediaType.APPLICATION_JSON)
        return when {
            contentType.includes(MediaType.APPLICATION_JSON) -> serverRequest.awaitBody()
            contentType.includes(graphQLMediaType) -> GraphQLRequest(query = serverRequest.awaitBody())
            else -> null
        }
    }
}
