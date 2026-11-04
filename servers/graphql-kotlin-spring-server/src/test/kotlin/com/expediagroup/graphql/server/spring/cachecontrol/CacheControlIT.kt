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

package com.expediagroup.graphql.server.spring.cachecontrol

import com.expediagroup.graphql.generator.federation.directives.CacheControlDirective
import com.expediagroup.graphql.generator.federation.directives.CacheControlScope
import com.expediagroup.graphql.generator.federation.directives.FieldSet
import com.expediagroup.graphql.generator.federation.directives.KeyDirective
import com.expediagroup.graphql.generator.federation.execution.FederatedTypeResolver
import com.expediagroup.graphql.generator.federation.execution.FederatedTypeSuspendResolver
import com.expediagroup.graphql.server.operations.Query
import com.expediagroup.graphql.server.spring.execution.REQUEST_PARAM_QUERY
import com.expediagroup.graphql.server.types.GraphQLBatchRequest
import com.expediagroup.graphql.server.types.GraphQLRequest
import graphql.schema.DataFetchingEnvironment
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = [
        "graphql.packages=com.expediagroup.graphql.server.spring.cachecontrol",
        "graphql.federation.enabled=true",
        "graphql.federation.cacheControl.enabled=true"
    ]
)
@EnableAutoConfiguration
class CacheControlIT {

    @Configuration
    class TestConfiguration {
        @Bean
        fun query(): Query = CacheControlQuery()

        @Bean
        fun productResolver(): FederatedTypeResolver = ProductResolver()
    }

    private lateinit var testClient: WebTestClient

    @BeforeEach
    fun setup(@LocalServerPort port: Int) {
        testClient = WebTestClient.bindToServer()
            .baseUrl("http://localhost:$port")
            .build()
    }

    @Test
    fun `verify most restrictive max age is returned in the Cache-Control header`() {
        post(GraphQLRequest("query { product(id: \"1\") { id name price } }"))
            .expectHeader().valueEquals(HttpHeaders.CACHE_CONTROL, "max-age=30, public")
            .expectBody().jsonPath("$.data.product.price").isEqualTo(9.99)
    }

    @Test
    fun `verify private scope is returned in the Cache-Control header`() {
        post(GraphQLRequest("query { product(id: \"1\") { id viewerNote } }"))
            .expectHeader().valueEquals(HttpHeaders.CACHE_CONTROL, "max-age=240, private")
    }

    @Test
    fun `verify GET requests return the Cache-Control header`() {
        testClient.get()
            .uri { builder ->
                builder.path("/graphql")
                    .queryParam(REQUEST_PARAM_QUERY, "{query}")
                    .build("query { product(id: \"1\") { name } }")
            }
            .exchange()
            .expectStatus().isOk
            .expectHeader().valueEquals(HttpHeaders.CACHE_CONTROL, "max-age=240, public")
    }

    @Test
    fun `verify entity hints are used for _entities requests`() {
        post(
            GraphQLRequest(
                query = "query(\$r: [_Any!]!) { _entities(representations: \$r) { ... on Product { name } } }",
                variables = mapOf("r" to listOf(mapOf("__typename" to "Product", "id" to "1")))
            )
        )
            .expectHeader().valueEquals(HttpHeaders.CACHE_CONTROL, "max-age=240, public")
            .expectBody().jsonPath("$.data._entities[0].name").isEqualTo("product 1")
    }

    @Test
    fun `verify responses without a cacheable policy are not stored`() {
        post(GraphQLRequest("query { uncached }"))
            .expectHeader().valueEquals(HttpHeaders.CACHE_CONTROL, "no-store")
    }

    @Test
    fun `verify responses with errors are not stored`() {
        post(GraphQLRequest("query { product(id: \"1\") { name } failing }"))
            .expectHeader().valueEquals(HttpHeaders.CACHE_CONTROL, "no-store")
            .expectBody().jsonPath("$.errors").isNotEmpty
    }

    @Test
    fun `verify batched requests return the most restrictive policy`() {
        postBatch(
            GraphQLRequest("query { product(id: \"1\") { name } }"),
            GraphQLRequest("query { product(id: \"2\") { name price } }"),
            GraphQLRequest("query { product(id: \"3\") { viewerNote } }")
        )
            .expectHeader().valueEquals(HttpHeaders.CACHE_CONTROL, "max-age=30, private")
            .expectBody().jsonPath("$[1].data.product.name").isEqualTo("product 2")
    }

    @Test
    fun `verify batched requests with an uncacheable operation are not stored`() {
        postBatch(
            GraphQLRequest("query { product(id: \"1\") { name } }"),
            GraphQLRequest("query { uncached }")
        )
            .expectHeader().valueEquals(HttpHeaders.CACHE_CONTROL, "no-store")
    }

    private fun postBatch(vararg requests: GraphQLRequest): WebTestClient.ResponseSpec =
        testClient.post()
            .uri("/graphql")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(GraphQLBatchRequest(*requests))
            .exchange()
            .expectStatus().isOk

    private fun post(request: GraphQLRequest): WebTestClient.ResponseSpec =
        testClient.post()
            .uri("/graphql")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isOk
}

class CacheControlQuery : Query {
    fun product(id: String): Product = Product(id, "product $id")

    fun uncached(): String = "uncached"

    @CacheControlDirective(maxAge = 60)
    fun failing(): String = throw IllegalStateException("boom")
}

@KeyDirective(fields = FieldSet("id"))
@CacheControlDirective(maxAge = 240)
data class Product(val id: String, val name: String) {
    @CacheControlDirective(maxAge = 30)
    fun price(): Double = 9.99

    @CacheControlDirective(scope = CacheControlScope.PRIVATE)
    fun viewerNote(): String = "only for you"
}

class ProductResolver : FederatedTypeSuspendResolver<Product> {
    override val typeName: String = "Product"

    override suspend fun resolve(environment: DataFetchingEnvironment, representation: Map<String, Any>): Product =
        Product(representation["id"].toString(), "product ${representation["id"]}")
}
