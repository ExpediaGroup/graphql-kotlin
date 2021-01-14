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

package com.expediagroup.graphql.examples.server.spring.query

import com.expediagroup.graphql.examples.server.spring.DATA_JSON_PATH
import com.expediagroup.graphql.examples.server.spring.GRAPHQL_ENDPOINT
import com.expediagroup.graphql.examples.server.spring.GRAPHQL_MEDIA_TYPE
import com.expediagroup.graphql.examples.server.spring.verifyOnlyDataExists
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.web.reactive.server.WebTestClient

@SpringBootTest
@AutoConfigureWebTestClient
@TestInstance(PER_CLASS)
class RecursiveQueryIT(@Autowired private val testClient: WebTestClient) {

    @Test
    fun `verify nodeGraph query`() {
        val query = "nodeGraph"

        testClient.post()
            .uri(GRAPHQL_ENDPOINT)
            .accept(APPLICATION_JSON)
            .contentType(GRAPHQL_MEDIA_TYPE)
            .bodyValue("query { $query { id, value, children { id, value, parent { id, value }, children { id, value } } } }")
            .exchange()
            .expectStatus().isOk
            .verifyOnlyDataExists(query)
            .verifyRoot(query, "0", "root", 2)
            .verifyChild(query, "0", "1", "A", "0", "root", 0)
            .verifyChild(query, "1", "2", "B", "0", "root", 1)
            .verifySubChild(query, "1", "0", "3", "C")
    }

    private fun WebTestClient.BodyContentSpec.verifyRoot(
        query: String,
        id: String,
        value: String,
        childrenSize: Int
    ): WebTestClient.BodyContentSpec {
        return this.jsonPath("$DATA_JSON_PATH.$query.id").isEqualTo(id)
            .jsonPath("$DATA_JSON_PATH.$query.value").isEqualTo(value)
            .jsonPath("$DATA_JSON_PATH.$query.children").isArray
            .jsonPath("$DATA_JSON_PATH.$query.children").value(hasSize<Int>(childrenSize))
    }

    private fun WebTestClient.BodyContentSpec.verifyChild(
        query: String,
        index: String,
        id: String,
        value: String,
        parentId: String,
        parentValue: String,
        childrenSize: Int
    ): WebTestClient.BodyContentSpec {
        return this.jsonPath("$DATA_JSON_PATH.$query.children.[$index].id").isEqualTo(id)
            .jsonPath("$DATA_JSON_PATH.$query.children.[$index].value").isEqualTo(value)
            .jsonPath("$DATA_JSON_PATH.$query.children.[$index].parent").exists()
            .jsonPath("$DATA_JSON_PATH.$query.children.[$index].parent.id").isEqualTo(parentId)
            .jsonPath("$DATA_JSON_PATH.$query.children.[$index].parent.value").isEqualTo(parentValue)
            .jsonPath("$DATA_JSON_PATH.$query.children.[$index].children").isArray
            .jsonPath("$DATA_JSON_PATH.$query.children.[$index].children").value(hasSize<Int>(childrenSize))
    }

    private fun WebTestClient.BodyContentSpec.verifySubChild(
        query: String,
        parentIndex: String,
        index: String,
        id: String,
        value: String
    ): WebTestClient.BodyContentSpec {
        return this.jsonPath("$DATA_JSON_PATH.$query.children.[$parentIndex].children.[$index].id").isEqualTo(id)
            .jsonPath("$DATA_JSON_PATH.$query.children.[$parentIndex].children.[$index].value").isEqualTo(value)
    }
}
