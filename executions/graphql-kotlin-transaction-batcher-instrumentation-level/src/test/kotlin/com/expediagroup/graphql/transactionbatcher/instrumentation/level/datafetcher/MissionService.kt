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

data class MissionServiceRequest(val id: Int)
data class Mission(val id: Int, val designation: String, val crew: List<Int>)

class MissionService {

    val batchArguments: MutableList<List<MissionServiceRequest>> = mutableListOf()
    val getMissionCallCount: AtomicInteger = AtomicInteger(0)

    fun getMission(
        request: MissionServiceRequest,
        environment: DataFetchingEnvironment
    ): CompletableFuture<Mission> {
        getMissionCallCount.incrementAndGet()
        return environment
            .graphQlContext
            .get<TransactionBatcher>(TransactionBatcher::class)
            .batch(request) { requests: List<MissionServiceRequest> ->
                batchArguments += requests
                requests.toFlux().flatMapSequential { request ->
                    missions[request.id].toMono().flatMap { (astronaut, delay) ->
                        astronaut.toMono().delayElement(delay)
                    }
                }
            }
    }

    companion object {
        private val missions = mapOf(
            2 to Pair(Mission(2, "Apollo 4", listOf(14, 30, 7)), Duration.ofMillis(100)),
            3 to Pair(Mission(3, "Apollo 5", listOf(23, 10, 12)), Duration.ofMillis(400)),
            4 to Pair(Mission(4, "Apollo 6", listOf(1, 28, 31, 6)), Duration.ofMillis(300))
        )
    }
}
