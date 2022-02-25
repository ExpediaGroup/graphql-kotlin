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

package com.expediagroup.graphql.transactionbatcher.instrumentation.level.datafetcher

import com.expediagroup.graphql.transactionbatcher.transaction.TransactionBatcher
import graphql.schema.DataFetchingEnvironment
import reactor.kotlin.core.publisher.toFlux
import reactor.kotlin.core.publisher.toMono
import java.time.Duration
import java.util.concurrent.CompletableFuture
import java.util.concurrent.atomic.AtomicInteger

data class AstronautServiceRequest(val id: Int)
data class Astronaut(val id: Int, val name: String)

class AstronautService {

    val produceArguments: MutableList<List<AstronautServiceRequest>> = mutableListOf()
    val getAstronautCallCount: AtomicInteger = AtomicInteger(0)

    fun getAstronaut(
        request: AstronautServiceRequest,
        environment: DataFetchingEnvironment
    ): CompletableFuture<Astronaut> {
        getAstronautCallCount.incrementAndGet()
        return environment
            .graphQlContext
            .get<TransactionBatcher>(TransactionBatcher::class)
            .batch(request) { requests: List<AstronautServiceRequest> ->
                produceArguments += requests
                requests.toFlux().flatMapSequential { request ->
                    astronauts[request.id].toMono().flatMap { (astronaut, delay) ->
                        astronaut.toMono().delayElement(delay)
                    }
                }
            }
    }

    companion object {
        private val astronauts = mapOf(
            1 to Pair(Astronaut(1, "Buzz Aldrin"), Duration.ofMillis(300)),
            2 to Pair(Astronaut(2, "William Anders"), Duration.ofMillis(600)),
            3 to Pair(Astronaut(3, "Neil Armstrong"), Duration.ofMillis(200))
        )
    }
}
