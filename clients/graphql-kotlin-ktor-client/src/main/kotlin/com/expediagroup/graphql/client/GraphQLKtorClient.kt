/*
 * Copyright 2020 Expedia, Inc
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

package com.expediagroup.graphql.client

import com.expediagroup.graphql.types.GraphQLResponse
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.JavaType
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
import io.ktor.client.request.accept
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
open class GraphQLKtorClient<in T : HttpClientEngineConfig>(
    private val url: URL,
    engineFactory: HttpClientEngineFactory<T>,
    private val mapper: ObjectMapper = jacksonObjectMapper(),
    configuration: HttpClientConfig<T>.() -> Unit = {}
) : GraphQLClient, Closeable {

    private val typeCache = mutableMapOf<Class<*>, JavaType>()

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

    /**
     * Executes specified GraphQL query or mutation.
     *
     * NOTE: explicit result type Class parameter is required due to the type erasure at runtime, i.e. since generic type is erased at runtime our
     * default serialization would attempt to serialize results back to Any object. As a workaround we get raw results as String which we then
     * manually deserialize using passed in result type Class information.
     */
    open suspend fun <T> execute(query: String, operationName: String? = null, variables: Any? = null, resultType: Class<T>, requestBuilder: HttpRequestBuilder.() -> Unit): GraphQLResponse<T> {
        // Variables are simple data classes which will be serialized as map.
        // By using map instead of typed object we can eliminate the need to explicitly convert variables to a map
        val graphQLRequest = mapOf(
            "query" to query,
            "operationName" to operationName,
            "variables" to variables
        )

        val rawResult = client.post<String>(url) {
            apply(requestBuilder)
            contentType(ContentType.Application.Json)
            body = graphQLRequest
        }

        @Suppress("BlockingMethodInNonBlockingContext")
        return mapper.readValue(rawResult, parameterizedType(resultType))
    }

    override suspend fun <T> execute(query: String, operationName: String?, variables: Any?, resultType: Class<T>): GraphQLResponse<T> =
        execute(query, operationName, variables, resultType, {})

    /**
     * Executes specified GraphQL query or mutation operation.
     */
    suspend inline fun <reified T> execute(query: String, operationName: String? = null, variables: Any? = null, noinline requestBuilder: HttpRequestBuilder.() -> Unit): GraphQLResponse<T> =
        execute(query, operationName, variables, T::class.java, requestBuilder)

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
