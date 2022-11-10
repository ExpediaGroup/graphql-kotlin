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

package com.expediagroup.graphql.examples.federation.reviews

import com.expediagroup.graphql.server.types.GraphQLRequest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient

@SpringBootTest
@AutoConfigureWebTestClient
class ReviewsApplicationTest(@Autowired val testClient: WebTestClient) {

    @Test
    fun `verifies product query`() {
        val query = """
          query Entities(${"$"}representations: [_Any!]!) {
            _entities(representations: ${"$"}representations) {
              ...on Product {
                id
                reviews {
                  id
                  text
                  starRating
                }
              }
            }
          }
        """.trimIndent()

        testClient.post()
            .uri("/graphql")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(
                GraphQLRequest(
                    operationName = "Entities",
                    query = query,
                    variables = mapOf("representations" to mapOf("__typename" to "Product", "id" to "5"))
                )
            )
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("\$.data._entities").exists()
            .jsonPath("\$.data._entities").isArray
            .jsonPath("\$.errors").doesNotExist()
            .jsonPath("\$.data._entities[0]").exists()
            .jsonPath("\$.data._entities[0].id").isEqualTo("5")
            .jsonPath("\$.data._entities[0].reviews").exists()
            .jsonPath("\$.data._entities[0].reviews").isArray
            .jsonPath("\$.data._entities[0].reviews[0]").exists()
            .jsonPath("\$.data._entities[0].reviews[0].text").isEqualTo("Amazing! Would Fly Again!")
            .jsonPath("\$.data._entities[0].reviews[0].starRating").isEqualTo(5)
    }
}
