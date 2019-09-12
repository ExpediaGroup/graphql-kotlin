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

package com.expediagroup.graphql.boot.server

import com.expediagroup.graphql.boot.server.model.GraphQLRequest
import com.expediagroup.graphql.extensions.print
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.type.MapType
import com.fasterxml.jackson.databind.type.TypeFactory
import graphql.schema.GraphQLSchema
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.awaitBody
import org.springframework.web.reactive.function.server.bodyAndAwait
import org.springframework.web.reactive.function.server.buildAndAwait
import org.springframework.web.reactive.function.server.coRouter
import org.springframework.web.reactive.function.server.json

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
    @Suppress("VariableNaming")
    private val APPLICATION_GRAPHQL: MediaType = MediaType("application", "graphql")
    private val mapTypeReference: MapType = TypeFactory.defaultInstance().constructMapType(HashMap::class.java, String::class.java, Any::class.java)

    @Bean
    @ExperimentalCoroutinesApi
    fun graphQLRoutes() = coRouter {
        (POST(config.endpoint) or GET(config.endpoint)).invoke { serverRequest ->
            val graphQLRequest = createGraphQLRequest(serverRequest)
            if (graphQLRequest != null) {
                val graphQLResult = queryHandler.executeQuery(graphQLRequest)
                ok().json().bodyAndAwait(graphQLResult)
            } else {
                badRequest().buildAndAwait()
            }
        }
        GET("/sdl") {
            ok().contentType(MediaType.TEXT_PLAIN).bodyAndAwait(schema.print())
        }
    }

    // https://github.com/FasterXML/jackson-module-kotlin/issues/221
    @Suppress("BlockingMethodInNonBlockingContext")
    private suspend fun createGraphQLRequest(serverRequest: ServerRequest): GraphQLRequest? = when {
        serverRequest.queryParam("query").isPresent -> {
            val query = serverRequest.queryParam("query").get()
            val operationName: String? = serverRequest.queryParam("operationName").orElseGet { null }
            val variables: String? = serverRequest.queryParam("variables").orElseGet { null }
            val graphQLVariables: Map<String, Any>? = variables?.let {
                objectMapper.readValue(it, mapTypeReference)
            }
            GraphQLRequest(query = query, operationName = operationName, variables = graphQLVariables)
        }
        serverRequest.method() == HttpMethod.POST -> when (serverRequest.headers().contentType().orElse(MediaType.APPLICATION_JSON)) {
            MediaType.APPLICATION_JSON -> serverRequest.awaitBody()
            APPLICATION_GRAPHQL -> GraphQLRequest(query = serverRequest.awaitBody())
            else -> null
        }
        else -> null
    }
}
