/*
 * Copyright 2026 Expedia, Inc
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

package com.expediagroup.graphql.server.spring.cachecontrol

import com.apollographql.federation.graphqljava.caching.CacheControlInstrumentation
import com.expediagroup.graphql.server.operations.Query
import com.expediagroup.graphql.server.types.GraphQLRequest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.ObjectProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import kotlin.test.assertNull

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = [
        "graphql.packages=com.expediagroup.graphql.server.spring.cachecontrol",
        "graphql.federation.enabled=true"
    ]
)
@EnableAutoConfiguration
class CacheControlDisabledIT {

    @Configuration
    class TestConfiguration {
        @Bean
        fun query(): Query = CacheControlQuery()
    }

    @Autowired
    private lateinit var cacheControlInstrumentation: ObjectProvider<CacheControlInstrumentation>

    private lateinit var testClient: WebTestClient

    @BeforeEach
    fun setup(@LocalServerPort port: Int) {
        testClient = WebTestClient.bindToServer()
            .baseUrl("http://localhost:$port")
            .build()
    }

    @Test
    fun `verify Cache-Control header is not set by default`() {
        assertNull(cacheControlInstrumentation.ifAvailable)

        testClient.post()
            .uri("/graphql")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(GraphQLRequest("query { product(id: \"1\") { id name price } }"))
            .exchange()
            .expectStatus().isOk
            .expectHeader().doesNotExist(HttpHeaders.CACHE_CONTROL)
            .expectBody().jsonPath("$.data.product.name").isEqualTo("product 1")
    }
}
