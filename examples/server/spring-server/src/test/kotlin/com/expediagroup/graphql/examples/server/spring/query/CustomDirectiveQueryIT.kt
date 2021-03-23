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
import com.expediagroup.graphql.examples.server.spring.verifyError
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.web.reactive.server.WebTestClient
import java.util.stream.Stream

@SpringBootTest
@AutoConfigureWebTestClient
@TestInstance(PER_CLASS)
class CustomDirectiveQueryIT(@Autowired private val testClient: WebTestClient) {

    @Test
    fun `verify justWhisper query`() {
        val query = "justWhisper"
        val expectedData = "hello"

        testClient.post()
            .uri(GRAPHQL_ENDPOINT)
            .accept(APPLICATION_JSON)
            .contentType(GRAPHQL_MEDIA_TYPE)
            .bodyValue("query { $query(msg: \"HELLO\") }")
            .exchange()
            .verifyData(query, expectedData)
    }

    @Test
    fun `verify justWhisper query without message`() {
        val query = "justWhisper"
        val expectedData = "default string"

        testClient.post()
            .uri(GRAPHQL_ENDPOINT)
            .accept(APPLICATION_JSON)
            .contentType(GRAPHQL_MEDIA_TYPE)
            .bodyValue("query { $query }")
            .exchange()
            .verifyData(query, expectedData)
    }

    @ParameterizedTest
    @MethodSource("specificValueOnlyQueries")
    fun `verify specific value only queries`(query: String, msg: String) {
        val expectedData = "<3"

        testClient.post()
            .uri(GRAPHQL_ENDPOINT)
            .accept(APPLICATION_JSON)
            .contentType(GRAPHQL_MEDIA_TYPE)
            .bodyValue("query { $query(msg: \"$msg\") }")
            .exchange()
            .verifyData(query, expectedData)
    }

    @ParameterizedTest
    @MethodSource("specificValueOnlyQueries")
    fun `verify specific value only queries with another value`(query: String, expectedValue: String) {
        val anotherValue = "hello"
        val expectedError = "Unsupported value, expected=$expectedValue actual=$anotherValue"

        testClient.post()
            .uri(GRAPHQL_ENDPOINT)
            .accept(APPLICATION_JSON)
            .contentType(GRAPHQL_MEDIA_TYPE)
            .bodyValue("query { $query(msg: \"$anotherValue\") }")
            .exchange()
            .verifyError(expectedError)
    }

    @Test
    fun `verify forceLowercaseEcho query`() {
        val query = "forceLowercaseEcho"
        val expectedData = "hello"

        testClient.post()
            .uri(GRAPHQL_ENDPOINT)
            .accept(APPLICATION_JSON)
            .contentType(GRAPHQL_MEDIA_TYPE)
            .bodyValue("query { $query(msg: \"HELLO\") }")
            .exchange()
            .verifyData(query, expectedData)
    }

    @Suppress("UnusedPrivateMember")
    private fun specificValueOnlyQueries() = Stream.of(
        Arguments.of("onlyCake", "cake"),
        Arguments.of("onlyIceCream", "icecream")
    )
}
