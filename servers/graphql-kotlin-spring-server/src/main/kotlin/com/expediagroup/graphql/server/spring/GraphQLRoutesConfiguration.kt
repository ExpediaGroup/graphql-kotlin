/*
 * Copyright 2021 Expedia, Inc
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

package com.expediagroup.graphql.server.spring

import com.expediagroup.graphql.server.spring.execution.SpringGraphQLServer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import org.springframework.web.reactive.function.server.buildAndAwait
import org.springframework.web.reactive.function.server.coRouter
import org.springframework.web.reactive.function.server.json

/**
 * Default route configuration for GraphQL endpoints.
 * Can handle requests over GET or POST as per the following guidelines:
 * https://graphql.org/learn/serving-over-http/
 */
@Configuration
@Import(GraphQLSchemaConfiguration::class)
class GraphQLRoutesConfiguration(
    private val config: GraphQLConfigurationProperties,
    private val graphQLServer: SpringGraphQLServer
) {

    @Bean
    fun graphQLRoutes() = coRouter {
        val isEndpointRequest = POST(config.endpoint) or GET(config.endpoint)
        val isNotWebSocketRequest = headers { isWebSocketHeaders(it) }.not()

        (isEndpointRequest and isNotWebSocketRequest).invoke { serverRequest ->
            val graphQLResponse = graphQLServer.execute(serverRequest)
            if (graphQLResponse != null) {
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
        headers.header(headerName).map { it.lowercase() }.contains(headerValue.lowercase())
}
