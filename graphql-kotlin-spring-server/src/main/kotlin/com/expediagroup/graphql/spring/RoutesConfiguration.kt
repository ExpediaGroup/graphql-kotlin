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

import com.expediagroup.graphql.extensions.print
import com.expediagroup.graphql.spring.execution.QueryHandler
import com.expediagroup.graphql.spring.model.GraphQLRequest
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.type.MapType
import com.fasterxml.jackson.databind.type.TypeFactory
import graphql.schema.GraphQLSchema
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.CoRouterFunctionDsl
import org.springframework.web.reactive.function.server.RequestPredicate
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.awaitBody
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import org.springframework.web.reactive.function.server.buildAndAwait
import org.springframework.web.reactive.function.server.coRouter
import org.springframework.web.reactive.function.server.json

private const val GET_PARAM_QUERY = "query"
private const val GET_PARAM_OPERATION_NAME = "operationName"
private const val GET_PARAM_VARIALBES = "variables"

/**
 * Default route configuration for GraphQL service and SDL service endpoints.
 */
@Configuration
class RoutesConfiguration(
    private val config: GraphQLConfigurationProperties,
    private val schema: GraphQLSchema,
    private val queryHandler: QueryHandler,
    private val objectMapper: ObjectMapper
) {
    private val graphQLMediaType: MediaType = MediaType("application", "graphql")
    private val mapTypeReference: MapType = TypeFactory.defaultInstance().constructMapType(HashMap::class.java, String::class.java, Any::class.java)

    @Bean
    fun graphQLRoute() = coRouter {
        (isEndpointRequest() and isNotWebSocketRequest()).invoke { serverRequest ->
            val graphQLRequest = createGraphQLRequest(serverRequest)
            if (graphQLRequest != null) {
                val graphQLResult = queryHandler.executeQuery(graphQLRequest)
                ok().json().bodyValueAndAwait(graphQLResult)
            } else {
                badRequest().buildAndAwait()
            }
        }
    }

    @Bean
    @ConditionalOnProperty(value = ["graphql.sdl.enabled"], havingValue = "true", matchIfMissing = true)
    fun sdlRoute() = coRouter {
        GET(config.sdl.endpoint) {
            ok().contentType(MediaType.TEXT_PLAIN).bodyValueAndAwait(schema.print())
        }
    }

    private fun CoRouterFunctionDsl.isEndpointRequest(): RequestPredicate = POST(config.endpoint) or GET(config.endpoint)

    private fun CoRouterFunctionDsl.isNotWebSocketRequest(): RequestPredicate = headers {
        // These headers are defined in the HTTP Protocol upgrade mechanism
        // https://developer.mozilla.org/en-US/docs/Web/HTTP/Protocol_upgrade_mechanism
        val isUpgrade = requestContainsHeader(it, "Connection", "Upgrade")
        val isWebSocket = requestContainsHeader(it, "Upgrade", "websocket")
        isUpgrade and isWebSocket
    }.not()

    private fun requestContainsHeader(headers: ServerRequest.Headers, headerName: String, headerValue: String): Boolean =
        headers.header(headerName).map { it.toLowerCase() }.contains(headerValue.toLowerCase())

    private suspend fun createGraphQLRequest(serverRequest: ServerRequest): GraphQLRequest? = when {
        isValidGetRequest(serverRequest) -> { createGraphQLRequestFromGet(serverRequest) }
        serverRequest.method() == HttpMethod.POST -> { createGraphQLRequestFromPost(serverRequest) }
        else -> null
    }

    /**
     * Check if the request has the required query parameter.
     * This is valid for both GET and POST requests and should take priority over POST body requests.
     *
     * See: https://graphql.org/learn/serving-over-http/#get-request
     */
    private fun isValidGetRequest(serverRequest: ServerRequest) = serverRequest.queryParam(GET_PARAM_QUERY).isPresent

    /**
     * Parse the GET request parameters into the [GraphQLRequest] object.
     *
     * See: https://graphql.org/learn/serving-over-http/#get-request
     */
    private fun createGraphQLRequestFromGet(serverRequest: ServerRequest): GraphQLRequest {
        val query = serverRequest.queryParam(GET_PARAM_QUERY).get()
        val operationName: String? = serverRequest.queryParam(GET_PARAM_OPERATION_NAME).orElseGet { null }
        val variables: String? = serverRequest.queryParam(GET_PARAM_VARIALBES).orElseGet { null }
        val graphQLVariables: Map<String, Any>? = variables?.let { objectMapper.readValue(it, mapTypeReference) }
        return GraphQLRequest(query = query, operationName = operationName, variables = graphQLVariables)
    }

    /**
     * Parse the POST body into a possible [GraphQLRequest]. Might return null if not a valid media type.
     *
     * We support two media types:
     *  - "application/json": Parse the body as-is to a [GraphQLRequest]
     *  - "application/graphql": The body is just the query/operation part of a [GraphQLRequest]
     *
     * See: https://graphql.org/learn/serving-over-http/#post-request
     */
    private suspend fun createGraphQLRequestFromPost(serverRequest: ServerRequest): GraphQLRequest? {
        val contentType = serverRequest.headers().contentType().orElse(MediaType.APPLICATION_JSON)
        return when {
            contentType.includes(MediaType.APPLICATION_JSON) -> serverRequest.awaitBody()
            contentType.includes(graphQLMediaType) -> GraphQLRequest(query = serverRequest.awaitBody())
            else -> null
        }
    }
}
