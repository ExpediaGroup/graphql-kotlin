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

package com.expediagroup.graphql.transactionbatcher.publisher

import com.expediagroup.graphql.transactionbatcher.transaction.TransactionBatcher
import org.reactivestreams.Publisher
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toFlux
import reactor.kotlin.core.publisher.toMono
import java.time.Duration
import java.util.concurrent.atomic.AtomicInteger

data class AstronautServiceRequest(val id: Int)
data class Astronaut(val id: Int, val name: String)

class AstronautService(
    private val transactionBatcher: TransactionBatcher
) {

    val produceArguments: MutableList<List<AstronautServiceRequest>> = mutableListOf()
    val getAstronautCallCount: AtomicInteger = AtomicInteger(0)
    private val astronautsPublisher = object : TriggeredPublisher<AstronautServiceRequest, Astronaut> {
        override fun produce(input: List<AstronautServiceRequest>): Publisher<Astronaut> {
            produceArguments.add(input)
            return this@AstronautService.getAstronauts(input)
        }
    }

    companion object {
        private val astronauts = mapOf(
            1 to Pair(Astronaut(1, "Buzz Aldrin"), Duration.ofMillis(300)),
            2 to Pair(Astronaut(2, "William Anders"), Duration.ofMillis(600)),
            3 to Pair(Astronaut(3, "Neil Armstrong"), Duration.ofMillis(200))
        )
    }

    fun getAstronaut(request: AstronautServiceRequest): Mono<Astronaut> {
        getAstronautCallCount.incrementAndGet()
        val future = this.transactionBatcher.enqueue(request, astronautsPublisher)
        return future.toMono()
    }

    fun getAstronauts(input: List<AstronautServiceRequest>): Publisher<Astronaut> =
        input.toFlux()
            .flatMapSequential { request ->
                { astronauts[request.id] }
                    .toMono()
                    .flatMap { (astronaut, delay) ->
                        astronaut.toMono().delayElement(delay)
                    }
            }
}
