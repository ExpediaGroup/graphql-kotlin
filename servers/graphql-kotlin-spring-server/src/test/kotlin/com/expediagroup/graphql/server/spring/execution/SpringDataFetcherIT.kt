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

import com.expediagroup.graphql.generator.annotations.GraphQLIgnore
import com.expediagroup.graphql.server.operations.Query
import com.expediagroup.graphql.server.types.GraphQLRequest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import java.util.UUID

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = ["graphql.packages=com.expediagroup.graphql.server.spring.execution"]
)
@EnableAutoConfiguration
class SpringDataFetcherIT(@Autowired private val testClient: WebTestClient) {

    @Test
    fun `verify spring data fetcher autowires spring beans`() {
        val request = GraphQLRequest(query = "query { getWidget(id: 1) { id value } }")
        testClient.post()
            .uri("/graphql")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectBody()
            .jsonPath("$.data.getWidget").exists()
            .jsonPath("$.data.getWidget.id").isEqualTo("1")
            .jsonPath("$.data.getWidget.value").exists()
    }

    @Configuration
    class TestConfiguration {

        @Bean
        fun query(): Query = SpringQuery()

        @Bean
        fun repository(): WidgetRepository = WidgetRepository()
    }

    class SpringQuery : Query {
        fun getWidget(@GraphQLIgnore @Autowired repository: WidgetRepository, id: Int): Widget = repository.findWidget(id)
    }

    class WidgetRepository {
        fun findWidget(id: Int) = Widget(id = id, value = UUID.randomUUID().toString())
    }

    data class Widget(
        val id: Int,
        val value: String
    )
}
