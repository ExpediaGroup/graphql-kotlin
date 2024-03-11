package com.expediagroup.graphql.server

import com.expediagroup.graphql.server.testtypes.GraphQLServerRequest
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
@Warmup(iterations = 2, time = 5, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 5, timeUnit = TimeUnit.SECONDS)
open class GraphQLServerRequestSerializationBenchmark {
    private val objectMapper = jacksonObjectMapper()
    private var request: String = ""

    @Setup
    fun setUp() {
        val loader = this::class.java.classLoader
        val operation = loader.getResource("StarWarsDetails.graphql")?.readText()?.replace("\n", "\\n")
        val variables = loader.getResource("StarWarsDetailsVariables.json")?.readText()
        request = """
            {
                "operationName": "StarWarsDetails",
                "query": "$operation",
                "variables": $variables
            }
        """.trimIndent()
    }

    @Benchmark
    fun JacksonDeserializeGraphQLRequest(): GraphQLServerRequest {
        return objectMapper.readValue<GraphQLServerRequest>(request)
    }

    @Benchmark
    fun KSerializationDeserializeGraphQLRequest(): GraphQLServerRequest {
        return Json.decodeFromString<GraphQLServerRequest>(request)
    }

}
