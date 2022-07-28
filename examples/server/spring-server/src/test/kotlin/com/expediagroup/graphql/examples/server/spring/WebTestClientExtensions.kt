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

package com.expediagroup.graphql.examples.server.spring

import org.hamcrest.core.StringContains
import org.springframework.test.web.reactive.server.WebTestClient

fun WebTestClient.ResponseSpec.verifyOnlyDataExists(expectedQuery: String): WebTestClient.BodyContentSpec {
    return this.expectBody()
        .jsonPath("$DATA_JSON_PATH.$expectedQuery").exists()
        .jsonPath(ERRORS_JSON_PATH).doesNotExist()
        .jsonPath(EXTENSIONS_JSON_PATH).doesNotExist()
}

fun WebTestClient.ResponseSpec.verifyData(
    expectedQuery: String,
    expectedData: String
): WebTestClient.BodyContentSpec {
    return this.expectStatus().isOk
        .verifyOnlyDataExists(expectedQuery)
        .jsonPath("$DATA_JSON_PATH.$expectedQuery").isEqualTo(expectedData)
}

fun WebTestClient.ResponseSpec.verifyError(expectedErrorSubString: String): WebTestClient.BodyContentSpec {
    return this.expectStatus().isOk
        .expectBody()
        .verifyError(expectedErrorSubString)
}

fun WebTestClient.BodyContentSpec.verifyError(expectedErrorSubString: String): WebTestClient.BodyContentSpec {
    return this.jsonPath(DATA_JSON_PATH).doesNotExist()
        .jsonPath("$ERRORS_JSON_PATH.[0].message").value(StringContains.containsString(expectedErrorSubString))
        .jsonPath(EXTENSIONS_JSON_PATH).doesNotExist()
}
