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

package com.expediagroup.graphql.client.spring

import com.expediagroup.graphql.client.GraphQLClient
import com.expediagroup.graphql.client.serializer.GraphQLClientSerializer
import com.expediagroup.graphql.client.serializer.defaultGraphQLSerializer
import com.expediagroup.graphql.client.types.GraphQLClientRequest
import com.expediagroup.graphql.client.types.GraphQLClientResponse
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient

/**
 * A lightweight typesafe GraphQL HTTP client using Spring WebClient engine.
 */
open class GraphQLWebClient(
    url: String,
    private val serializer: GraphQLClientSerializer = defaultGraphQLSerializer(),
    builder: WebClient.Builder = WebClient.builder()
) : GraphQLClient<WebClient.RequestBodyUriSpec> {

    private val client: WebClient = builder.baseUrl(url).build()

    override suspend fun <T : Any> execute(request: GraphQLClientRequest<T>, requestCustomizer: WebClient.RequestBodyUriSpec.() -> Unit): GraphQLClientResponse<T> {
        val rawResult = client.post()
            .apply(requestCustomizer)
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(serializer.serialize(request))
            .retrieve()
            .bodyToMono(String::class.java)
            .awaitSingle()
        return serializer.deserialize(rawResult, request.responseType())
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
