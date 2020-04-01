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

import com.fasterxml.jackson.databind.DeserializationFeature
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.cio.CIO
import io.ktor.client.features.HttpClientFeature
import io.ktor.client.features.json.JacksonSerializer
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.request.accept
import io.ktor.client.request.post
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.util.KtorExperimentalAPI
import java.net.URL

/**
 * A lightweight typesafe GraphQL HTTP client.
 */
@KtorExperimentalAPI
class GraphQLClient(private val url: URL, engine: HttpClientEngineFactory<*> = CIO, vararg features: HttpClientFeature<*, *>) {

    private val client = HttpClient(engineFactory = engine) {
        for (feature in features) {
            install(feature)
        }
        // install default serializer
        install(JsonFeature) {
            serializer = JacksonSerializer {
                this.enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
            }
        }
    }

    /**
     * Executes specified GraphQL query or mutation.
     */
    suspend fun <T> executeOperation(query: String, operationName: String? = null, variables: Any? = null): GraphQLResult<T> {
        // variables are data classes
        // by using map instead of typed object we can eliminate the need to convert variables to map
        val graphQLRequest = mapOf(
            "query" to query,
            "operationName" to operationName,
            "variables" to variables
        )

        return client.post(url) {
            accept(ContentType.Application.Json)
            contentType(ContentType.Application.Json)
            body = graphQLRequest
        }
    }
}
