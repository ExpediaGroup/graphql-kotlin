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

package com.expediagroup.graphql.examples.server.spring.query

import com.expediagroup.graphql.examples.server.spring.DATA_JSON_PATH
import com.expediagroup.graphql.examples.server.spring.GRAPHQL_ENDPOINT
import com.expediagroup.graphql.examples.server.spring.GRAPHQL_MEDIA_TYPE
import com.expediagroup.graphql.examples.server.spring.verifyData
import com.expediagroup.graphql.examples.server.spring.verifyError
import com.expediagroup.graphql.examples.server.spring.verifyOnlyDataExists
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.web.reactive.server.WebTestClient

@SpringBootTest
@TestInstance(PER_CLASS)
class OneOfQueryIT {

    private lateinit var testClient: WebTestClient

    @BeforeEach
    fun setup(@Autowired context: ApplicationContext) {
        testClient = WebTestClient.bindToApplicationContext(context).build()
    }

    @Test
    fun `verify describeContentBlock query with wrapped object input`() {
        val query = "describeContentBlock"
        val expectedData = "paragraph: Hello @oneOf"

        testClient.post()
            .uri(GRAPHQL_ENDPOINT)
            .accept(APPLICATION_JSON)
            .contentType(GRAPHQL_MEDIA_TYPE)
            .bodyValue("query { $query(input: { paragraph: { text: \"Hello @oneOf\" } }) }")
            .exchange()
            .verifyData(query, expectedData)
    }

    @Test
    fun `verify describeContentBlocks query with list of oneOf inputs`() {
        val query = "describeContentBlocks"

        testClient.post()
            .uri(GRAPHQL_ENDPOINT)
            .accept(APPLICATION_JSON)
            .contentType(GRAPHQL_MEDIA_TYPE)
            .bodyValue(
                """
                query {
                  $query(input: [
                    { paragraph: { text: "Hello" } },
                    { image: { url: "https://example.com/logo.png", altText: "Logo" } }
                  ])
                }
                """.trimIndent()
            )
            .exchange()
            .verifyOnlyDataExists(query)
            .jsonPath("$DATA_JSON_PATH.$query[0]").isEqualTo("paragraph: Hello")
            .jsonPath("$DATA_JSON_PATH.$query[1]").isEqualTo("image: Logo at https://example.com/logo.png")
    }

    @Test
    fun `verify findUserBy query with unwrapped scalar input`() {
        val query = "findUserBy"
        val expectedData = "user id=user-123"

        testClient.post()
            .uri(GRAPHQL_ENDPOINT)
            .accept(APPLICATION_JSON)
            .contentType(GRAPHQL_MEDIA_TYPE)
            .bodyValue("query { $query(input: { id: \"user-123\" }) }")
            .exchange()
            .verifyData(query, expectedData)
    }

    @Test
    fun `verify findUserBy query with wrapped object input`() {
        val query = "findUserBy"
        val expectedData = "user criteria name=Sam address=Seattle"

        testClient.post()
            .uri(GRAPHQL_ENDPOINT)
            .accept(APPLICATION_JSON)
            .contentType(GRAPHQL_MEDIA_TYPE)
            .bodyValue("query { $query(input: { criteria: { name: \"Sam\", address: \"Seattle\" } }) }")
            .exchange()
            .verifyData(query, expectedData)
    }

    @Test
    fun `verify resolveEntity query with nested oneOf input`() {
        val query = "resolveEntity"
        val expectedData = "organization slug=expedia"

        testClient.post()
            .uri(GRAPHQL_ENDPOINT)
            .accept(APPLICATION_JSON)
            .contentType(GRAPHQL_MEDIA_TYPE)
            .bodyValue("query { $query(input: { organization: { slug: \"expedia\" } }) }")
            .exchange()
            .verifyData(query, expectedData)
    }

    @Test
    fun `verify oneOf input rejects multiple fields`() {
        val query = "describeContentBlock"
        val expectedError = "Exactly one key must be specified for OneOf type 'ContentBlockInput'."

        testClient.post()
            .uri(GRAPHQL_ENDPOINT)
            .accept(APPLICATION_JSON)
            .contentType(GRAPHQL_MEDIA_TYPE)
            .bodyValue(
                """
                query {
                  $query(input: {
                    paragraph: { text: "Hello" },
                    image: { url: "https://example.com/logo.png", altText: "Logo" }
                  })
                }
                """.trimIndent()
            )
            .exchange()
            .verifyError(expectedError)
    }
}
