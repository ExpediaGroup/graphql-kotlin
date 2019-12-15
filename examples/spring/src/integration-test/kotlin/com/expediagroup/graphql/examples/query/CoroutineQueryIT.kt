/*
 * Copyright 2019 Expedia, Inc
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

package com.expediagroup.graphql.examples.query

import com.expediagroup.graphql.examples.Constants.GRAPHQL_ENDPOINT
import com.expediagroup.graphql.examples.Constants.GRAPHQL_MEDIA_TYPE
import com.expediagroup.graphql.examples.IntegrationTest
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
class CoroutineQueryIT(@Autowired private val testClient: WebTestClient) : IntegrationTest {

    @Test
    fun `verify exampleCoroutineQuery query`() {
        val query = "exampleCoroutineQuery"
        val expectedData = "hello:olleh"

        testClient.post()
            .uri(GRAPHQL_ENDPOINT)
            .accept(APPLICATION_JSON)
            .contentType(GRAPHQL_MEDIA_TYPE)
            .bodyValue("query { $query(msg: \"hello\") }")
            .exchange()
            .verifyData(query, expectedData)
    }

    @Test
    fun `verify slowFunction query`() {
        val query = "slowFunction"
        val expectedData = "olleh"

        testClient.post()
            .uri(GRAPHQL_ENDPOINT)
            .accept(APPLICATION_JSON)
            .contentType(GRAPHQL_MEDIA_TYPE)
            .bodyValue("query { $query(msg: \"hello\") }")
            .exchange()
            .verifyData(query, expectedData)
    }

    @Test
    fun `verify fastFunction query`() {
        val query = "fastFunction"
        val data = "hello"

        testClient.post()
            .uri(GRAPHQL_ENDPOINT)
            .accept(APPLICATION_JSON)
            .contentType(GRAPHQL_MEDIA_TYPE)
            .bodyValue("query { $query(msg: \"$data\") }")
            .exchange()
            .verifyData(query, data)
    }
}
