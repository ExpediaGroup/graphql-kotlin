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

package com.expediagroup.graphql.server.spring.instrumentation

import com.expediagroup.graphql.server.operations.Query
import com.expediagroup.graphql.server.spring.DEFAULT_INSTRUMENTATION_ORDER
import com.expediagroup.graphql.server.types.GraphQLRequest
import graphql.ExecutionResult
import graphql.ExecutionResultImpl
import graphql.execution.instrumentation.Instrumentation
import graphql.execution.instrumentation.SimpleInstrumentation
import graphql.execution.instrumentation.parameters.InstrumentationExecutionParameters
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.Ordered
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import java.util.concurrent.CompletableFuture
import java.util.concurrent.atomic.AtomicInteger

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = ["graphql.packages=com.expediagroup.graphql.server.spring.instrumentation"])
@EnableAutoConfiguration
class InstrumentationIT(@Autowired private val testClient: WebTestClient) {

    @Configuration
    class TestConfiguration {
        private val atomicCounter = AtomicInteger()

        @Bean
        fun query(): Query = BasicQuery()

        @Bean
        fun firstInstrumentation(): Instrumentation = OrderedInstrumentation(DEFAULT_INSTRUMENTATION_ORDER, atomicCounter)

        @Bean
        fun secondInstrumentation(): Instrumentation = OrderedInstrumentation(DEFAULT_INSTRUMENTATION_ORDER + 1, atomicCounter)
    }

    @Suppress("unused")
    class BasicQuery : Query {
        fun helloWorld(name: String) = "Hello $name!"
    }

    class OrderedInstrumentation(private val instrumentationOrder: Int, private val counter: AtomicInteger) : SimpleInstrumentation(), Ordered {
        override fun instrumentExecutionResult(executionResult: ExecutionResult, parameters: InstrumentationExecutionParameters): CompletableFuture<ExecutionResult> {
            val currentExt: MutableMap<Any, Any> = executionResult.extensions?.toMutableMap() ?: mutableMapOf()
            currentExt[instrumentationOrder] = counter.getAndIncrement()

            return CompletableFuture.completedFuture(ExecutionResultImpl(executionResult.getData(), executionResult.errors, currentExt))
        }

        override fun getOrder(): Int = instrumentationOrder
    }

    @Test
    fun `verify instrumentations are applied in the specified order`() {
        testClient.post()
            .uri("/graphql")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(GraphQLRequest("query { helloWorld(name: \"World\") }"))
            .exchange()
            .expectBody()
            .jsonPath("$.data.helloWorld").isEqualTo("Hello World!")
            .jsonPath("$.errors").doesNotExist()
            .jsonPath("$.extensions").exists()
            .jsonPath("$.extensions.0").isEqualTo(0)
            .jsonPath("$.extensions.1").isEqualTo(1)
    }
}
