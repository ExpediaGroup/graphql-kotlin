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
import com.expediagroup.graphql.examples.server.spring.verifyError
import com.expediagroup.graphql.examples.server.spring.verifyOnlyDataExists
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
class PolymorphicQueryIT(@Autowired private val testClient: WebTestClient) {

    @Test
    fun `verify animal query with CAT`() {
        val query = "animal"

        testClient.post()
            .uri(GRAPHQL_ENDPOINT)
            .accept(APPLICATION_JSON)
            .contentType(GRAPHQL_MEDIA_TYPE)
            .bodyValue("query { $query(type: CAT) { type, sound, ... on Cat { ignoreEveryone } } }")
            .exchange()
            .expectStatus().isOk
            .verifyOnlyDataExists(query)
            .jsonPath("$DATA_JSON_PATH.$query.type").isEqualTo("CAT")
            .jsonPath("$DATA_JSON_PATH.$query.sound").isEqualTo("meow")
            .jsonPath("$DATA_JSON_PATH.$query.ignoreEveryone").isEqualTo("ignore everyone")
    }

    @Test
    fun `verify animal query with DOG`() {
        val query = "animal"

        testClient.post()
            .uri(GRAPHQL_ENDPOINT)
            .accept(APPLICATION_JSON)
            .contentType(GRAPHQL_MEDIA_TYPE)
            .bodyValue("query { $query(type: DOG) { type, sound, ... on Dog { doSomethingUseful } } }")
            .exchange()
            .expectStatus().isOk
            .verifyOnlyDataExists(query)
            .jsonPath("$DATA_JSON_PATH.$query.type").isEqualTo("DOG")
            .jsonPath("$DATA_JSON_PATH.$query.sound").isEqualTo("bark")
            .jsonPath("$DATA_JSON_PATH.$query.doSomethingUseful").isEqualTo("something useful")
    }

    @Test
    fun `verify animal query with unknown type`() {
        val query = "animal"
        val unknownType = "HELLO"

        testClient.post()
            .uri(GRAPHQL_ENDPOINT)
            .accept(APPLICATION_JSON)
            .contentType(GRAPHQL_MEDIA_TYPE)
            .bodyValue("query { $query(type: $unknownType) { type, sound } }")
            .exchange()
            .expectStatus().isOk
            .verifyError("Validation error")
            .verifyError("WrongType")
            .verifyError(
                "argument 'type' with value 'EnumValue{name='$unknownType'}' is not a valid 'AnimalType' - " +
                    "Expected enum literal value not in allowable values -  'EnumValue{name='HELLO'}'"
            )
    }

    @Test
    fun `verify dog query`() {
        val query = "dog"

        testClient.post()
            .uri(GRAPHQL_ENDPOINT)
            .accept(APPLICATION_JSON)
            .contentType(GRAPHQL_MEDIA_TYPE)
            .bodyValue("query { $query { type, sound, doSomethingUseful } }")
            .exchange()
            .expectStatus().isOk
            .verifyOnlyDataExists(query)
            .jsonPath("$DATA_JSON_PATH.$query.type").isEqualTo("DOG")
            .jsonPath("$DATA_JSON_PATH.$query.sound").isEqualTo("bark")
            .jsonPath("$DATA_JSON_PATH.$query.doSomethingUseful").isEqualTo("something useful")
    }

    @Test
    fun `verify whichHand query`() {
        val query = "whichHand"

        testClient.post()
            .uri(GRAPHQL_ENDPOINT)
            .accept(APPLICATION_JSON)
            .contentType(GRAPHQL_MEDIA_TYPE)
            .bodyValue("query { $query(whichHand: \"right\") { __typename ... on RightHand { property } ... on LeftHand { field } } }")
            .exchange()
            .expectStatus().isOk
            .verifyOnlyDataExists(query)
            .jsonPath("$DATA_JSON_PATH.$query.__typename").isEqualTo("RightHand")
            .jsonPath("$DATA_JSON_PATH.$query.property").isEqualTo("12")
    }

    @Test
    fun `verify whichHand query with another union type`() {
        val query = "whichHand"

        testClient.post()
            .uri(GRAPHQL_ENDPOINT)
            .accept(APPLICATION_JSON)
            .contentType(GRAPHQL_MEDIA_TYPE)
            .bodyValue("query { $query(whichHand: \"hello\") { __typename ... on RightHand { property } ... on LeftHand { field } } }")
            .exchange()
            .expectStatus().isOk
            .verifyOnlyDataExists(query)
            .jsonPath("$DATA_JSON_PATH.$query.__typename").isEqualTo("LeftHand")
            .jsonPath("$DATA_JSON_PATH.$query.field").isEqualTo("hello world")
    }
}
