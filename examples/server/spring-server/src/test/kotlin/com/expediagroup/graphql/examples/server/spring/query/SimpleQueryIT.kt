/*
 * Copyright 2022 Expedia, Inc
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
import com.expediagroup.graphql.examples.server.spring.ERRORS_JSON_PATH
import com.expediagroup.graphql.examples.server.spring.EXTENSIONS_JSON_PATH
import com.expediagroup.graphql.examples.server.spring.GRAPHQL_ENDPOINT
import com.expediagroup.graphql.examples.server.spring.GRAPHQL_MEDIA_TYPE
import com.expediagroup.graphql.examples.server.spring.verifyData
import com.expediagroup.graphql.examples.server.spring.verifyError
import com.expediagroup.graphql.examples.server.spring.verifyOnlyDataExists
import org.hamcrest.Matchers.anyOf
import org.hamcrest.Matchers.hasSize
import org.hamcrest.Matchers.instanceOf
import org.hamcrest.Matchers.nullValue
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
class SimpleQueryIT(@Autowired private val testClient: WebTestClient) {

    @Test
    fun `verify simpleDeprecatedQuery query`() {
        val query = "simpleDeprecatedQuery"
        val expectedData = "false"

        testClient.post()
            .uri(GRAPHQL_ENDPOINT)
            .accept(APPLICATION_JSON)
            .contentType(GRAPHQL_MEDIA_TYPE)
            .bodyValue("query { $query }")
            .exchange()
            .verifyData(query, expectedData)
    }

    @Test
    fun `verify shinyNewQuery query`() {
        val query = "shinyNewQuery"
        val expectedData = "true"

        testClient.post()
            .uri(GRAPHQL_ENDPOINT)
            .accept(APPLICATION_JSON)
            .contentType(GRAPHQL_MEDIA_TYPE)
            .bodyValue("query { $query }")
            .exchange()
            .verifyData(query, expectedData)
    }

    @Test
    fun `verify notPartOfSchema query`() {
        val query = "notPartOfSchema"
        val expectedErrorOne = "Validation error"
        val expectedErrorTwo = "FieldUndefined"
        val expectedErrorThree = "Field 'notPartOfSchema' in type 'Query' is undefined"

        testClient.post()
            .uri(GRAPHQL_ENDPOINT)
            .accept(APPLICATION_JSON)
            .contentType(GRAPHQL_MEDIA_TYPE)
            .bodyValue("query { $query }")
            .exchange()
            .verifyError(expectedErrorOne)
            .verifyError(expectedErrorTwo)
            .verifyError(expectedErrorThree)
    }

    @Test
    fun `verify privateFunctionsAreNotVisible query`() {
        val query = "privateFunctionsAreNotVisible"
        val expectedErrorOne = "Validation error"
        val expectedErrorTwo = "FieldUndefined"
        val expectedErrorThree = "Field 'privateFunctionsAreNotVisible' in type 'Query' is undefined"

        testClient.post()
            .uri(GRAPHQL_ENDPOINT)
            .accept(APPLICATION_JSON)
            .contentType(GRAPHQL_MEDIA_TYPE)
            .bodyValue("query { $query }")
            .exchange()
            .verifyError(expectedErrorOne)
            .verifyError(expectedErrorTwo)
            .verifyError(expectedErrorThree)
    }

    @Test
    fun `verify doSomething query`() {
        val query = "doSomething"
        val expectedData = "true"

        testClient.post()
            .uri(GRAPHQL_ENDPOINT)
            .accept(APPLICATION_JSON)
            .contentType(GRAPHQL_MEDIA_TYPE)
            .bodyValue("query { $query(value: 1) }")
            .exchange()
            .verifyData(query, expectedData)
    }

    @Test
    fun `verify generateNullableNumber query`() {
        val query = "generateNullableNumber"

        testClient.post()
            .uri(GRAPHQL_ENDPOINT)
            .accept(APPLICATION_JSON)
            .contentType(GRAPHQL_MEDIA_TYPE)
            .bodyValue("query { $query }")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$DATA_JSON_PATH.$query").value(anyOf(nullValue(), instanceOf(Integer::class.java)))
            .jsonPath(ERRORS_JSON_PATH).doesNotExist()
            .jsonPath(EXTENSIONS_JSON_PATH).doesNotExist()
    }

    @Test
    fun `verify generateNumber query`() {
        val query = "generateNumber"

        testClient.post()
            .uri(GRAPHQL_ENDPOINT)
            .accept(APPLICATION_JSON)
            .contentType(GRAPHQL_MEDIA_TYPE)
            .bodyValue("query { $query }")
            .exchange()
            .expectStatus().isOk
            .verifyOnlyDataExists(query)
            .jsonPath("$DATA_JSON_PATH.$query").isNumber
    }

    @Test
    fun `verify generateList query`() {
        val query = "generateList"

        testClient.post()
            .uri(GRAPHQL_ENDPOINT)
            .accept(APPLICATION_JSON)
            .contentType(GRAPHQL_MEDIA_TYPE)
            .bodyValue("query { $query }")
            .exchange()
            .expectStatus().isOk
            .verifyOnlyDataExists(query)
            .jsonPath("$DATA_JSON_PATH.$query").isArray
            .jsonPath("$DATA_JSON_PATH.$query").value(hasSize<Int>(10))
    }

    @Test
    fun `verify doSomethingWithOptionalInput query`() {
        val query = "doSomethingWithOptionalInput"
        val expectedData = "required value=0, optional value=1"

        testClient.post()
            .uri(GRAPHQL_ENDPOINT)
            .accept(APPLICATION_JSON)
            .contentType(GRAPHQL_MEDIA_TYPE)
            .bodyValue("query { $query(requiredValue: 0, optionalValue: 1) }")
            .exchange()
            .verifyData(query, expectedData)
    }

    @Test
    fun `verify doSomethingWithOptionalInput query when optional input is not provided`() {
        val query = "doSomethingWithOptionalInput"
        val expectedData = "required value=0, optional value=null"

        testClient.post()
            .uri(GRAPHQL_ENDPOINT)
            .accept(APPLICATION_JSON)
            .contentType(GRAPHQL_MEDIA_TYPE)
            .bodyValue("query { $query(requiredValue: 0) }")
            .exchange()
            .verifyData(query, expectedData)
    }
}
