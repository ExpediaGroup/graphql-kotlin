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

package com.expediagroup.graphql.examples.federation.products

import com.expediagroup.graphql.server.types.GraphQLRequest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient

@SpringBootTest
@AutoConfigureWebTestClient
class ProductsApplicationTest(@Autowired val testClient: WebTestClient) {

    @Test
    fun `verifies product query`() {
        val query = """
          query ProductById(${"$"}productId: ID!) {
            product(id: ${"$"}productId) {
              id
              name
              description
            }
          }
        """.trimIndent()

        testClient.post()
            .uri("/graphql")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(
                GraphQLRequest(
                    operationName = "ProductById",
                    query = query,
                    variables = mapOf("productId" to 1)
                )
            )
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("\$.data.product").exists()
            .jsonPath("\$.errors").doesNotExist()
            .jsonPath("\$.data.product.name").isEqualTo("Saturn V")
            .jsonPath("\$.data.product.description").isEqualTo("The Original Super Heavy-Lift Rocket!")
    }
}
