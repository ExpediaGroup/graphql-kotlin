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

package com.expediagroup.graphql.plugin

import com.expediagroup.graphql.plugin.config.TimeoutConfig
import graphql.schema.idl.SchemaParser
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.engine.cio.endpoint
import io.ktor.client.features.ClientRequestException
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.util.KtorExperimentalAPI
import kotlinx.coroutines.TimeoutCancellationException

/**
 * Downloads GraphQL SDL from the specified endpoint and verifies whether the result is a valid GraphQL schema.
 */
@KtorExperimentalAPI
suspend fun downloadSchema(endpoint: String, httpHeaders: Map<String, Any> = emptyMap(), timeoutConfig: TimeoutConfig = TimeoutConfig()): String = HttpClient(engineFactory = CIO) {
    engine {
        requestTimeout = timeoutConfig.read
        endpoint {
            connectTimeout = timeoutConfig.connect
        }
    }
}.use { client ->
    val sdl = try {
        client.get<String>(urlString = endpoint) {
            httpHeaders.forEach { (name, value) ->
                header(name, value)
            }
        }
    } catch (e: Throwable) {
        when (e) {
            is ClientRequestException, is TimeoutCancellationException -> throw e
            else -> throw RuntimeException("Unable to download SDL from specified endpoint=$endpoint", e)
        }
    }
    SchemaParser().parse(sdl)
    sdl
}
