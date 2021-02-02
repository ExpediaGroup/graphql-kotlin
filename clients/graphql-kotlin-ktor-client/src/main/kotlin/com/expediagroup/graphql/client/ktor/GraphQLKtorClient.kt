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
import com.expediagroup.graphql.client.GraphQLClientRequest
import com.expediagroup.graphql.types.GraphQLResponse
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.HttpClientEngineConfig
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.cio.CIO
import io.ktor.client.engine.cio.CIOEngineConfig
import io.ktor.client.features.json.JacksonSerializer
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.post
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.util.KtorExperimentalAPI
import java.io.Closeable
import java.net.URL
import java.util.concurrent.ConcurrentHashMap

/**
 * A lightweight typesafe GraphQL HTTP client using Ktor HTTP client engine.
 */
@KtorExperimentalAPI
open class GraphQLKtorClient<in E : HttpClientEngineConfig>(
    private val url: URL,
    engineFactory: HttpClientEngineFactory<E>,
    private val mapper: ObjectMapper = jacksonObjectMapper(),
    configuration: HttpClientConfig<E>.() -> Unit = {}
) : GraphQLClient<HttpRequestBuilder>, Closeable {

    private val typeCache = ConcurrentHashMap<Class<*>, JavaType>()

    init {
        mapper.enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
    }

    private val client = HttpClient(engineFactory = engineFactory) {
        apply(configuration)

        // install default JSON serializer
        install(JsonFeature) {
            serializer = JacksonSerializer(mapper)
        }
    }

    override suspend fun <T> execute(request: GraphQLClientRequest, requestCustomizer: HttpRequestBuilder.() -> Unit): GraphQLResponse<T> {
        val rawRequest = mapOf(
            "query" to request.query,
            "operationName" to request.operationName,
            "variables" to request.variables
        )

        val rawResult = client.post<String>(url) {
            apply(requestCustomizer)
            contentType(ContentType.Application.Json)
            body = rawRequest
        }

        @Suppress("BlockingMethodInNonBlockingContext")
        return mapper.readValue(rawResult, parameterizedType(request.responseType()))
    }

    override suspend fun execute(requests: List<GraphQLClientRequest>, requestCustomizer: HttpRequestBuilder.() -> Unit): List<GraphQLResponse<*>> {
        val rawRequests = requests.map { request ->
            mapOf(
                "query" to request.query,
                "operationName" to request.operationName,
                "variables" to request.variables
            )
        }
        val rawResult = client.post<JsonNode>(url) {
            apply(requestCustomizer)
            contentType(ContentType.Application.Json)
            body = rawRequests
        }

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

    override fun close() {
        client.close()
    }

    companion object {
        operator fun invoke(url: URL, mapper: ObjectMapper = jacksonObjectMapper(), config: HttpClientConfig<CIOEngineConfig>.() -> Unit = {}) =
            GraphQLKtorClient(url = url, engineFactory = CIO, mapper = mapper, configuration = config)
    }
}
