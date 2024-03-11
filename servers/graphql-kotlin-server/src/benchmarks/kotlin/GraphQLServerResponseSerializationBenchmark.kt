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

import com.expediagroup.graphql.server.testtypes.GraphQLBatchResponse
import com.expediagroup.graphql.server.testtypes.GraphQLResponse
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import kotlinx.serialization.encodeToString
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
@Measurement(iterations = 5, time = 5, timeUnit = TimeUnit.SECONDS)
open class GraphQLServerResponseSerializationBenchmark {
    private val mapper = jacksonObjectMapper()
    private lateinit var response: GraphQLResponse
    private lateinit var batchResponse: GraphQLBatchResponse

    @Setup
    fun setUp() {
        val data = mapper.readValue<Map<String, Any?>>(
            this::class.java.classLoader.getResourceAsStream("StarWarsDetailsResponse.json")!!
        )
        response = GraphQLResponse(
            mapper.readValue<Map<String, Any?>>(
                this::class.java.classLoader.getResourceAsStream("StarWarsDetailsResponse.json")!!
            )
        )
        batchResponse = GraphQLBatchResponse(
            GraphQLResponse(data),
            GraphQLResponse(data),
            GraphQLResponse(data),
            GraphQLResponse(data)
        )
    }

    @Benchmark
    fun JacksonSerializeGraphQLResponse(): String = mapper.writeValueAsString(response)

    @Benchmark
    fun JacksonSerializeGraphQLBatchResponse(): String = mapper.writeValueAsString(batchResponse)

    @Benchmark
    fun KSerializationSerializeGraphQLResponse(): String = Json.encodeToString(response)

    @Benchmark
    fun KSerializationSerializeGraphQLBatchResponse(): String = Json.encodeToString(batchResponse)
}
