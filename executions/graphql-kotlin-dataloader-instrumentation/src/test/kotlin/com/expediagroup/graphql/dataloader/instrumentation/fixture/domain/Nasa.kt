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

package com.expediagroup.graphql.dataloader.instrumentation.fixture.domain

import reactor.kotlin.core.publisher.toMono
import java.time.Duration
import java.util.concurrent.CompletableFuture

data class Address(
    val street: String = "300 E Street SW",
    val zipCode: String = "98004"
)

class Nasa(
    val address: Address = Address(),
    val phoneNumber: String = "+1 123-456-7890"
) {
    fun getTwitter(): CompletableFuture<String> =
        "https://twitter.com/NASA".toMono().delayElement(Duration.ofMillis(100)).toFuture()
}
