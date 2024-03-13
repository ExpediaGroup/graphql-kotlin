/*
 * Copyright 2024 Expedia, Inc
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

package com.expediagroup.graphql.server

import com.expediagroup.graphql.server.testtypes.GraphQLServerResponse
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import kotlinx.serialization.json.Json
import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.annotations.Fork
import org.openjdk.jmh.annotations.Measurement
import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.Setup
import org.openjdk.jmh.annotations.State
import org.openjdk.jmh.annotations.Warmup
import java.util.concurrent.TimeUnit

@State(Scope.Benchmark)
@Fork(5)
@Warmup(iterations = 1, time = 5, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 10, timeUnit = TimeUnit.SECONDS)
open class GraphQLServerResponseDeserializationBenchmark {
    private val mapper = jacksonObjectMapper()
    private lateinit var response: String
    private lateinit var batchResponse: String

    @Setup
    fun setUp() {
        response = this::class.java.classLoader.getResource("StarWarsDetailsResponse.json")!!.readText()
        batchResponse = """
            [
                $response,
                $response,
                $response,
                $response
            ]
        """.trimIndent()
    }

    @Benchmark
    fun JacksonDeserializeGraphQLResponse(): GraphQLServerResponse = mapper.readValue(response)

    @Benchmark
    fun JacksonDeserializeGraphQLBatchResponse(): GraphQLServerResponse = mapper.readValue(batchResponse)

    @Benchmark
    fun KSerializationDeserializeGraphQLResponse(): GraphQLServerResponse = Json.decodeFromString(response)

    @Benchmark
    fun KSerializationDeserializeGraphQLBatchResponse(): GraphQLServerResponse = Json.decodeFromString(batchResponse)
}
