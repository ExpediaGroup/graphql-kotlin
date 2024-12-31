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

@SpringBootTest(
    properties = ["graphql.automaticPersistedQueries.enabled=true"]
)
@AutoConfigureWebTestClient
@TestInstance(PER_CLASS)
class APQQueryIT(@Autowired private val testClient: WebTestClient) {

    @Test
    fun `verify GET persisted query with hash only followed by POST with hash`() {
        val query = "simpleDeprecatedQuery"

        testClient.get()
            .uri { builder ->
                builder.path("/graphql")
                    .queryParam("extensions", "{extension}")
                    .build("""{"persistedQuery":{"version":1,"sha256Hash":"aee64e0a941589ff06b717d4930405f3eafb089e687bef6ece5719ea6a4e7f35"}}""")
            }
            .exchange()
            .expectBody().json(
                """
                    {
                      errors: [
                        {
                          message: "PersistedQueryNotFound"
                        }
                      ]
                    }
                """.trimIndent()
            )

        val expectedData = "false"

        testClient.post()
            .uri { builder ->
                builder.path("/graphql")
                    .queryParam("extensions", "{extension}")
                    .build("""{"persistedQuery":{"version":1,"sha256Hash":"aee64e0a941589ff06b717d4930405f3eafb089e687bef6ece5719ea6a4e7f35"}}""")
            }
            .accept(APPLICATION_JSON)
            .contentType(GRAPHQL_MEDIA_TYPE)
            .bodyValue("query { $query }")
            .exchange()
            .verifyData(query, expectedData)

        testClient.get()
            .uri { builder ->
                builder.path("/graphql")
                    .queryParam("extensions", "{extension}")
                    .build("""{"persistedQuery":{"version":1,"sha256Hash":"aee64e0a941589ff06b717d4930405f3eafb089e687bef6ece5719ea6a4e7f35"}}""")
            }
            .exchange()
            .verifyData(query, expectedData)
    }
}
