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
import com.expediagroup.graphql.client.GraphQLClientRequest
import com.expediagroup.graphql.types.GraphQLResponse
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.http.MediaType
import org.springframework.http.codec.json.Jackson2JsonDecoder
import org.springframework.http.codec.json.Jackson2JsonEncoder
import org.springframework.web.reactive.function.client.WebClient
import java.util.concurrent.ConcurrentHashMap

/**
 * A lightweight typesafe GraphQL HTTP client using Spring WebClient engine.
 */
open class GraphQLWebClient(
    url: String,
    private val mapper: ObjectMapper = jacksonObjectMapper(),
    builder: WebClient.Builder = WebClient.builder()
) : GraphQLClient<WebClient.RequestBodyUriSpec> {

    private val typeCache = ConcurrentHashMap<Class<*>, JavaType>()

    init {
        mapper.enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)

        builder.codecs { codecConfigurer ->
            codecConfigurer.defaultCodecs().jackson2JsonEncoder(Jackson2JsonEncoder(mapper, MediaType.APPLICATION_JSON))
            codecConfigurer.defaultCodecs().jackson2JsonDecoder(Jackson2JsonDecoder(mapper, MediaType.APPLICATION_JSON))
        }
    }

    private val client: WebClient = builder.baseUrl(url).build()

    override suspend fun <T> execute(request: GraphQLClientRequest, requestCustomizer: WebClient.RequestBodyUriSpec.() -> Unit): GraphQLResponse<T> {
        val rawRequest = mapOf(
            "query" to request.query,
            "operationName" to request.operationName,
            "variables" to request.variables
        )

        val rawResult = client.post()
            .apply(requestCustomizer)
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(rawRequest)
            .retrieve()
            .bodyToMono(String::class.java)
            .awaitSingle()

        @Suppress("BlockingMethodInNonBlockingContext")
        return mapper.readValue(rawResult, parameterizedType(request.responseType()))
    }

    override suspend fun execute(requests: List<GraphQLClientRequest>, requestCustomizer: WebClient.RequestBodyUriSpec.() -> Unit): List<GraphQLResponse<*>> {
        val rawRequests = requests.map { request ->
            mapOf(
                "query" to request.query,
                "operationName" to request.operationName,
                "variables" to request.variables
            )
        }

        val rawResult = client.post()
            .apply(requestCustomizer)
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(rawRequests)
            .retrieve()
            .bodyToMono(JsonNode::class.java)
            .awaitSingle()

        return if (rawResult.isArray) {
            rawResult.withIndex().map { (index, element) ->
                val singleResponse: GraphQLResponse<*> = mapper.convertValue(element, parameterizedType(requests[index].responseType()))
                singleResponse
            }
        } else {
            listOf(mapper.convertValue(rawResult, parameterizedType(requests.first().responseType())))
        }
    }

    private fun <T> parameterizedType(resultType: Class<T>): JavaType =
        typeCache.computeIfAbsent(resultType) {
            mapper.typeFactory.constructParametricType(GraphQLResponse::class.java, resultType)
        }
}
