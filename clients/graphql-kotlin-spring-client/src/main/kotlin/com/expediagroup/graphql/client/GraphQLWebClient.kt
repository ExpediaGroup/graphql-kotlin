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
) : GraphQLClient {

    private val typeCache = ConcurrentHashMap<Class<*>, JavaType>()

    init {
        mapper.enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)

        builder.codecs { codecConfigurer ->
            codecConfigurer.defaultCodecs().jackson2JsonEncoder(Jackson2JsonEncoder(mapper, MediaType.APPLICATION_JSON))
            codecConfigurer.defaultCodecs().jackson2JsonDecoder(Jackson2JsonDecoder(mapper, MediaType.APPLICATION_JSON))
        }
    }

    private val client: WebClient = builder.baseUrl(url).build()

    /**
     * Executes specified GraphQL query or mutation.
     *
     * NOTE: explicit result type Class parameter is required due to the type erasure at runtime, i.e. since generic type is erased at runtime our
     * default serialization would attempt to serialize results back to Any object. As a workaround we get raw results as String which we then
     * manually deserialize using passed in result type Class information.
     */
    open suspend fun <T> execute(
        query: String,
        operationName: String? = null,
        variables: Any? = null,
        resultType: Class<T>,
        requestBuilder: WebClient.RequestBodyUriSpec.() -> Unit
    ): GraphQLResponse<T> {
        // Variables are simple data classes which will be serialized as map.
        // By using map instead of typed object we can eliminate the need to explicitly convert variables to a map
        val graphQLRequest = mapOf(
            "query" to query,
            "operationName" to operationName,
            "variables" to variables
        )

        val rawResult = client.post()
            .apply(requestBuilder)
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(graphQLRequest)
            .retrieve()
            .bodyToMono(String::class.java)
            .awaitSingle()

        @Suppress("BlockingMethodInNonBlockingContext")
        return mapper.readValue(rawResult, parameterizedType(resultType))
    }

    override suspend fun <T> execute(query: String, operationName: String?, variables: Any?, resultType: Class<T>): GraphQLResponse<T> =
        execute(query, operationName, variables, resultType, {})

    /**
     * Executes specified GraphQL query or mutation operation.
     */
    suspend inline fun <reified T> execute(query: String, operationName: String? = null, variables: Any? = null, noinline requestBuilder: WebClient.RequestBodyUriSpec.() -> Unit): GraphQLResponse<T> =
        execute(query, operationName, variables, T::class.java, requestBuilder)

    private fun <T> parameterizedType(resultType: Class<T>): JavaType =
        typeCache.computeIfAbsent(resultType) {
            mapper.typeFactory.constructParametricType(GraphQLResponse::class.java, resultType)
        }
}
