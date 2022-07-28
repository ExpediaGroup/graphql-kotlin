/*
 * Copyright 2021 Expedia, Inc
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
package com.expediagroup.graphql.server.spring.execution

import com.expediagroup.graphql.server.operations.Query
import com.expediagroup.graphql.server.types.GraphQLRequest
import graphql.introspection.IntrospectionQuery
import org.hamcrest.core.StringContains
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import kotlin.random.Random

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = [
        "graphql.packages=com.expediagroup.graphql.server.spring.execution",
        "graphql.introspection.enabled=false"
    ]
)
@EnableAutoConfiguration
class IntrospectionIT(@Autowired private val testClient: WebTestClient) {

    @Test
    fun `verify custom jackson bindings work with function data fetcher`() {
        val request = GraphQLRequest(query = IntrospectionQuery.INTROSPECTION_QUERY)
        testClient.post()
            .uri("/graphql")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectBody()
            .jsonPath("$.data").doesNotExist()
            .jsonPath("$.errors").isArray
            .jsonPath("$.errors[0].message").value(StringContains.containsString("Validation error"))
            .jsonPath("$.errors[0].message").value(StringContains.containsString("Field 'queryType' in type '__Schema' is undefined"))
    }

    @Configuration
    class TestConfiguration {
        @Bean
        fun query(): Query = RandomQuery()
    }

    class RandomQuery : Query {
        fun randomBoolean(): Boolean = Random.nextBoolean()
    }
}
