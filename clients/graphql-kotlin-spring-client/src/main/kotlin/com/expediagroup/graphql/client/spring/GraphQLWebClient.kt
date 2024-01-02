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

package com.expediagroup.graphql.client.spring

import com.expediagroup.graphql.client.GraphQLClient
import com.expediagroup.graphql.client.extensions.getQueryId
import com.expediagroup.graphql.client.extensions.toExtensionsBodyMap
import com.expediagroup.graphql.client.extensions.toQueryParamString
import com.expediagroup.graphql.client.serializer.GraphQLClientSerializer
import com.expediagroup.graphql.client.serializer.defaultGraphQLSerializer
import com.expediagroup.graphql.client.types.AutomaticPersistedQueriesExtension
import com.expediagroup.graphql.client.types.AutomaticPersistedQueriesSettings
import com.expediagroup.graphql.client.types.GraphQLClientRequest
import com.expediagroup.graphql.client.types.GraphQLClientResponse
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.ClientRequest
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import java.net.URI

/**
 * A lightweight typesafe GraphQL HTTP client using Spring WebClient engine.
 */
open class GraphQLWebClient(
    url: String,
    private val serializer: GraphQLClientSerializer = defaultGraphQLSerializer(),
    builder: WebClient.Builder = WebClient.builder(),
    override val automaticPersistedQueriesSettings: AutomaticPersistedQueriesSettings = AutomaticPersistedQueriesSettings()
) : GraphQLClient<WebClient.RequestBodyUriSpec> {

    private val client: WebClient = builder
        .baseUrl(url)
        .filter { request, next ->
            val encodedUri = request.url().toString().replace("%20", "+")
            val filtered = ClientRequest
                .from(request)
                .url(URI.create(encodedUri))
                .build()
            next.exchange(filtered)
        }.build()

    override suspend fun <T : Any> execute(request: GraphQLClientRequest<T>, requestCustomizer: WebClient.RequestBodyUriSpec.() -> Unit): GraphQLClientResponse<T> {
        return if (automaticPersistedQueriesSettings.enabled) {
            val queryId = request.getQueryId()
            val automaticPersistedQueriesExtension = AutomaticPersistedQueriesExtension(
                version = AutomaticPersistedQueriesSettings.VERSION,
                sha256Hash = queryId
            )
            val extensions = request.extensions?.let {
                automaticPersistedQueriesExtension.toExtensionsBodyMap().plus(it)
            } ?: automaticPersistedQueriesExtension.toExtensionsBodyMap()

            val apqRawResultWithoutQuery: String = when (automaticPersistedQueriesSettings.httpMethod) {
                is AutomaticPersistedQueriesSettings.HttpMethod.GET -> {
                    client
                        .get()
                        .uri {
                            it.queryParam("extension", "{extension}").build(automaticPersistedQueriesExtension.toQueryParamString())
                        }
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                        .accept(MediaType.APPLICATION_JSON)
                        .retrieve()
                        .awaitBody()
                }

                is AutomaticPersistedQueriesSettings.HttpMethod.POST -> {
                    val requestWithoutQuery = object : GraphQLClientRequest<T> by request {
                        override val query = null
                        override val extensions = extensions
                    }
                    client
                        .post()
                        .apply(requestCustomizer)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(serializer.serialize(requestWithoutQuery))
                        .retrieve()
                        .awaitBody()
                }
            }

            serializer.deserialize(apqRawResultWithoutQuery, request.responseType()).let {
                if (it.errors.isNullOrEmpty() && it.data != null) return it
            }

            val apqRawResultWithQuery: String = when (automaticPersistedQueriesSettings.httpMethod) {
                is AutomaticPersistedQueriesSettings.HttpMethod.GET -> {
                    client
                        .get()
                        .uri {
                            it
                                .queryParam("query", "{query}")
                                .queryParam("extension", "{extension}")
                                .build(serializer.serialize(request), automaticPersistedQueriesExtension.toQueryParamString())
                        }
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                        .accept(MediaType.APPLICATION_JSON)
                        .retrieve()
                        .awaitBody()
                }

                is AutomaticPersistedQueriesSettings.HttpMethod.POST -> {
                    val requestWithQuery = object : GraphQLClientRequest<T> by request {
                        override val extensions = extensions
                    }
                    client
                        .post()
                        .apply(requestCustomizer)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(serializer.serialize(requestWithQuery))
                        .retrieve()
                        .awaitBody()
                }
            }

            serializer.deserialize(apqRawResultWithQuery, request.responseType())
        } else {
            val rawResult = client.post()
                .apply(requestCustomizer)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(serializer.serialize(request))
                .retrieve()
                .bodyToMono(String::class.java)
                .awaitSingle()

            serializer.deserialize(rawResult, request.responseType())
        }
    }

    override suspend fun execute(requests: List<GraphQLClientRequest<*>>, requestCustomizer: WebClient.RequestBodyUriSpec.() -> Unit): List<GraphQLClientResponse<*>> {
        val rawResult = client.post()
            .apply(requestCustomizer)
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(serializer.serialize(requests))
            .retrieve()
            .bodyToMono(String::class.java)
            .awaitSingle()

        return serializer.deserialize(rawResult, requests.map { it.responseType() })
    }
}
