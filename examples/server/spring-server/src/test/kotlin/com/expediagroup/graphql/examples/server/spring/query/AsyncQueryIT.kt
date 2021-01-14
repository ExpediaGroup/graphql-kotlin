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

import com.expediagroup.graphql.examples.server.spring.GRAPHQL_ENDPOINT
import com.expediagroup.graphql.examples.server.spring.GRAPHQL_MEDIA_TYPE
import com.expediagroup.graphql.examples.server.spring.verifyData
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
class AsyncQueryIT(@Autowired private val testClient: WebTestClient) {

    @Test
    fun `verify delayedEchoUsingCompletableFuture query`() {
        val query = "delayedEchoUsingCompletableFuture"
        val data = "helloUsingCompletableFuture"

        testClient.post()
            .uri(GRAPHQL_ENDPOINT)
            .accept(APPLICATION_JSON)
            .contentType(GRAPHQL_MEDIA_TYPE)
            .bodyValue("query { $query(msg: \"$data\", delayMilliseconds: 200) }")
            .exchange()
            .verifyData(query, data)
    }

    @Test
    fun `verify delayedEchoUsingReactorMono query`() {
        val query = "delayedEchoUsingReactorMono"
        val data = "helloUsingMono"

        testClient.post()
            .uri(GRAPHQL_ENDPOINT)
            .accept(APPLICATION_JSON)
            .contentType(GRAPHQL_MEDIA_TYPE)
            .bodyValue("query { $query(msg: \"$data\", delayMilliseconds: 200) }")
            .exchange()
            .verifyData(query, data)
    }

    @Test
    fun `verify delayedEchoUsingCoroutine query`() {
        val query = "delayedEchoUsingCoroutine"
        val data = "helloUsingCoroutine"

        testClient.post()
            .uri(GRAPHQL_ENDPOINT)
            .accept(APPLICATION_JSON)
            .contentType(GRAPHQL_MEDIA_TYPE)
            .bodyValue("query { $query(msg: \"$data\", delayMilliseconds: 200) }")
            .exchange()
            .verifyData(query, data)
    }
}
