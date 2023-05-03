/*
 * Copyright 2023 Expedia, Inc
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
package com.expediagroup.graalvm.spring.schema

import com.expediagroup.graalvm.schema.model.InputOnly
import com.expediagroup.graphql.server.types.GraphQLRequest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient

@SpringBootTest
@AutoConfigureWebTestClient
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TypesQueryTest(@Autowired private val testClient: WebTestClient) {

    @Test
    fun `verify input only query`() {
        val request = GraphQLRequest(
            query = "query InputOnlyQuery(\$inputArg: InputOnlyInput){ inputTypeQuery(arg: \$inputArg) }",
            operationName = "InputOnlyQuery",
            variables = mapOf("inputArg" to InputOnly(id = 123))
        )

        testClient.post()
            .uri("/graphql")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.data.inputTypeQuery").exists()
            .jsonPath("$.data.inputTypeQuery").isEqualTo("InputOnly(id=123)")
            .jsonPath("$.errors").doesNotExist()
            .jsonPath("$.extensions").doesNotExist()
    }
}
