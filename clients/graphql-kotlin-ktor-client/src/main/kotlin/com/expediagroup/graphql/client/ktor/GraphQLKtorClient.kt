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

package com.expediagroup.graphql.client.ktor

import com.expediagroup.graphql.client.GraphQLClient
import com.expediagroup.graphql.client.serializer.GraphQLClientSerializer
import com.expediagroup.graphql.client.serializer.defaultGraphQLSerializer
import com.expediagroup.graphql.client.types.GraphQLClientRequest
import com.expediagroup.graphql.client.types.GraphQLClientResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.expectSuccess
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.content.TextContent
import java.io.Closeable
import java.net.URL

/**
 * A lightweight typesafe GraphQL HTTP client using Ktor HTTP client engine.
 */
open class GraphQLKtorClient(
    private val url: URL,
    private val httpClient: HttpClient = HttpClient(engineFactory = CIO),
    private val serializer: GraphQLClientSerializer = defaultGraphQLSerializer()
) : GraphQLClient<HttpRequestBuilder>, Closeable {

    override suspend fun <T : Any> execute(request: GraphQLClientRequest<T>, requestCustomizer: HttpRequestBuilder.() -> Unit): GraphQLClientResponse<T> {
        val rawResult: String = httpClient.post(url) {
            expectSuccess = true
            apply(requestCustomizer)
            setBody(TextContent(serializer.serialize(request), ContentType.Application.Json))
        }.body()
        return serializer.deserialize(rawResult, request.responseType())
    }

    override suspend fun execute(requests: List<GraphQLClientRequest<*>>, requestCustomizer: HttpRequestBuilder.() -> Unit): List<GraphQLClientResponse<*>> {
        val rawResult: String = httpClient.post(url) {
            expectSuccess = true
            apply(requestCustomizer)
            setBody(TextContent(serializer.serialize(requests), ContentType.Application.Json))
        }.body()
        return serializer.deserialize(rawResult, requests.map { it.responseType() })
    }

    override fun close() {
        httpClient.close()
    }
}
