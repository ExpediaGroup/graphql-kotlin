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

import com.expediagroup.graphql.generator.extensions.print
import com.fasterxml.jackson.databind.ObjectMapper
import io.ktor.http.ContentType
import io.ktor.serialization.jackson.jackson
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.application.plugin
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.application
import io.ktor.server.routing.get
import io.ktor.server.routing.post

/**
 * Configures GraphQL GET route
 *
 * @param endpoint GraphQL server GET endpoint, defaults to 'graphql'
 * @param streamingResponse Enable streaming response body without keeping it fully in memory. If set to true (default) it will set `Transfer-Encoding: chunked` header on the responses.
 * @param jacksonConfiguration Jackson Object Mapper customizations
 */
fun Route.graphQLGetRoute(endpoint: String = "graphql", streamingResponse: Boolean = true, jacksonConfiguration: ObjectMapper.() -> Unit = {}): Route {
    val graphQLPlugin = this.application.plugin(GraphQL)
    val route = get(endpoint) {
        graphQLPlugin.server.executeRequest(call)
    }
    route.install(ContentNegotiation) {
        jackson(streamRequestBody = streamingResponse) {
            apply(jacksonConfiguration)
        }
    }
    return route
}

/**
 * Configures GraphQL POST route
 *
 * @param endpoint GraphQL server POST endpoint, defaults to 'graphql'
 * @param streamingResponse Enable streaming response body without keeping it fully in memory. If set to true (default) it will set `Transfer-Encoding: chunked` header on the responses.
 * @param jacksonConfiguration Jackson Object Mapper customizations
 */
fun Route.graphQLPostRoute(endpoint: String = "graphql", streamingResponse: Boolean = true, jacksonConfiguration: ObjectMapper.() -> Unit = {}): Route {
    val graphQLPlugin = this.application.plugin(GraphQL)
    val route = post(endpoint) {
        graphQLPlugin.server.executeRequest(call)
    }
    route.install(ContentNegotiation) {
        jackson(streamRequestBody = streamingResponse) {
            apply(jacksonConfiguration)
        }
    }
    return route
}

/**
 * Configures GraphQL SDL route.
 *
 * @param endpoint GET endpoint that will return GraphQL schema in SDL format, defaults to 'sdl'
 */
fun Route.graphQLSDLRoute(endpoint: String = "sdl"): Route {
    val graphQLPlugin = this.application.plugin(GraphQL)
    val sdl = graphQLPlugin.schema.print()
    return get(endpoint) {
        call.respondText(text = sdl)
    }
}

/**
 * Configures GraphiQL IDE route.
 *
 * @param endpoint GET endpoint that will return instance of GraphiQL IDE, defaults to 'graphiql'
 * @param graphQLEndpoint your GraphQL endpoint for processing requests
 */
fun Route.graphiQLRoute(endpoint: String = "graphiql", graphQLEndpoint: String = "graphql"): Route {
    val contextPath = this.environment?.rootPath
    val graphiQL = GraphQL::class.java.classLoader.getResourceAsStream("graphql-graphiql.html")?.bufferedReader()?.use { reader ->
        reader.readText()
            .replace("\${graphQLEndpoint}", if (contextPath.isNullOrBlank()) graphQLEndpoint else "$contextPath/$graphQLEndpoint")
            .replace("\${subscriptionsEndpoint}", if (contextPath.isNullOrBlank()) "subscriptions" else "$contextPath/subscriptions")
//            .replace("\${subscriptionsEndpoint}", if (contextPath.isBlank()) config.routing.subscriptions.endpoint else "$contextPath/${config.routing.subscriptions.endpoint}")
    } ?: throw IllegalStateException("Unable to load GraphiQL")
    return get(endpoint) {
        call.respondText(graphiQL, ContentType.Text.Html)
    }
}
