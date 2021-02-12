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

package com.expediagroup.graphql.client.ktor

import com.expediagroup.graphql.client.GraphQLClient
import com.expediagroup.graphql.client.serializer.GraphQLClientDeserializer
import com.expediagroup.graphql.client.serializer.defaultDeserializer
import com.expediagroup.graphql.client.types.GraphQLClientRequest
import com.expediagroup.graphql.client.types.GraphQLClientResponse
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.HttpClientEngineConfig
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.cio.CIO
import io.ktor.client.engine.cio.CIOEngineConfig
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.post
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.util.KtorExperimentalAPI
import java.io.Closeable
import java.net.URL

/**
 * A lightweight typesafe GraphQL HTTP client using Ktor HTTP client engine.
 */
@KtorExperimentalAPI
open class GraphQLKtorClient<in E : HttpClientEngineConfig>(
    private val url: URL,
    private val deserializer: GraphQLClientDeserializer = defaultDeserializer(),
    engineFactory: HttpClientEngineFactory<E>,
    configuration: HttpClientConfig<E>.() -> Unit = {}
) : GraphQLClient<HttpRequestBuilder>, Closeable {

    private val client = HttpClient(engineFactory = engineFactory) {
        // install default JSON serializer
        install(JsonFeature)
        apply(configuration)
    }

    override suspend fun <T : Any> execute(request: GraphQLClientRequest<T>, requestCustomizer: HttpRequestBuilder.() -> Unit): GraphQLClientResponse<T> {
        val rawResult = client.post<String>(url) {
            apply(requestCustomizer)
            contentType(ContentType.Application.Json)
            body = request
        }
        return deserializer.deserialize(rawResult, request.responseType())
    }

    override suspend fun execute(requests: List<GraphQLClientRequest<*>>, requestCustomizer: HttpRequestBuilder.() -> Unit): List<GraphQLClientResponse<*>> {
        val rawResult = client.post<String>(url) {
            apply(requestCustomizer)
            contentType(ContentType.Application.Json)
            body = requests
        }
        return deserializer.deserialize(rawResult, requests.map { it.responseType() })
    }

    override fun close() {
        client.close()
    }

    companion object {
        operator fun invoke(url: URL, config: HttpClientConfig<CIOEngineConfig>.() -> Unit = {}, deserializer: GraphQLClientDeserializer = defaultDeserializer()) =
            GraphQLKtorClient(url = url, engineFactory = CIO, configuration = config, deserializer = deserializer)
    }
}
