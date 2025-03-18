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

import com.alibaba.fastjson2.JSON
import com.alibaba.fastjson2.JSONWriter
import com.alibaba.fastjson2.to
import com.expediagroup.graphql.server.types.GraphQLServerRequest
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.annotations.Fork
import org.openjdk.jmh.annotations.Measurement
import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.Setup
import org.openjdk.jmh.annotations.State
import org.openjdk.jmh.annotations.Warmup
import java.util.concurrent.TimeUnit

@State(Scope.Benchmark)
@Fork(value = 5, jvmArgsAppend = ["--add-modules=jdk.incubator.vector"])
@Warmup(iterations = 1, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 4, time = 5, timeUnit = TimeUnit.SECONDS)
open class GraphQLServerRequestBatchDeserializationBenchmark {
    private val mapper = jacksonObjectMapper()
    private lateinit var batchRequest: String

    @Setup
    fun setUp() {
        JSON.config(JSONWriter.Feature.WriteNulls)
        val loader = this::class.java.classLoader
        val operation1 = loader.getResource("StarWarsDetails.graphql")!!.readText().replace("\n", "\\n")
        val operation2 = loader.getResource("StarWarsDetails.graphql")!!.readText().replace("\n", "\\n")
        val operation3 = loader.getResource("StarWarsDetails.graphql")!!.readText().replace("\n", "\\n")
        val operation4 = loader.getResource("StarWarsDetails.graphql")!!.readText().replace("\n", "\\n")
        val variables1 = loader.getResource("StarWarsDetailsVariables.json")!!.readText()
        val variables2 = loader.getResource("StarWarsDetailsVariables.json")!!.readText()
        val variables3 = loader.getResource("StarWarsDetailsVariables.json")!!.readText()
        val variables4 = loader.getResource("StarWarsDetailsVariables.json")!!.readText()
        batchRequest = """
            [
                { "operationName": "StarWarsDetails", "query": "$operation1", "variables": $variables1 },
                { "operationName": "StarWarsDetails", "query": "$operation2", "variables": $variables2 },
                { "operationName": "StarWarsDetails", "query": "$operation3", "variables": $variables3 },
                { "operationName": "StarWarsDetails", "query": "$operation4", "variables": $variables4 }
            ]
        """.trimIndent()
    }

    @Benchmark
    fun jackson(): GraphQLServerRequest = mapper.readValue(batchRequest)

    @Benchmark
    fun fastjson2(): GraphQLServerRequest = batchRequest.to<GraphQLServerRequest>()
}
