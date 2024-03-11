package com.expediagroup.graphql.server

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
@Warmup(iterations = 2, time = 5, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 5, timeUnit = TimeUnit.SECONDS)
open class GraphQLServerResponseSerializationBenchmark {
    private val objectMapper = jacksonObjectMapper()
    private var response: GraphQLResponse = GraphQLResponse()

    @Setup
    fun setUp() {
        response = GraphQLResponse(
            objectMapper.readValue<Map<String, Any?>>(
                this::class.java.classLoader.getResourceAsStream("StarWarsDetailsResponse.json")!!
            )
        )
    }

    @Benchmark
    fun JacksonSerializeGraphQLResponse(): String {
        return objectMapper.writeValueAsString(response)
    }

    @Benchmark
    fun KSerializationSerializeGraphQLResponse(): String {
        return Json.encodeToString(response)
    }
}
