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

package com.expediagroup.graphql.transactionbatcher.instrumentation.datafetcher

import com.expediagroup.graphql.server.execution.dataloader.KotlinDataLoader
import com.expediagroup.graphql.transactionbatcher.instrumentation.extensions.getDataLoaderFromContext
import graphql.schema.DataFetchingEnvironment
import org.dataloader.BatchLoader
import reactor.kotlin.core.publisher.toFlux
import reactor.kotlin.core.publisher.toMono
import java.time.Duration
import java.util.concurrent.CompletableFuture

data class AstronautServiceRequest(val id: Int)
data class Astronaut(val id: Int, val name: String)

class AstronautDataLoader : KotlinDataLoader<AstronautServiceRequest, Astronaut> {
    override val dataLoaderName: String = "AstronautDataLoader"
    override fun getBatchLoader(): BatchLoader<AstronautServiceRequest, Astronaut> =
        BatchLoader<AstronautServiceRequest, Astronaut> { requests ->
            requests.toFlux().flatMapSequential { request ->
                AstronautService.astronauts[request.id].toMono().flatMap { (astronaut, delay) ->
                    astronaut.toMono().delayElement(delay)
                }
            }.collectList().toFuture()
        }
}

class AstronautService {
    fun getAstronaut(
        request: AstronautServiceRequest,
        environment: DataFetchingEnvironment
    ): CompletableFuture<Astronaut> =
        environment
            .getDataLoaderFromContext<AstronautServiceRequest, Astronaut>("AstronautDataLoader")
            .load(request)

    companion object {
        val astronauts = mapOf(
            1 to Pair(Astronaut(1, "Buzz Aldrin"), Duration.ofMillis(300)),
            2 to Pair(Astronaut(2, "William Anders"), Duration.ofMillis(600)),
            3 to Pair(Astronaut(3, "Neil Armstrong"), Duration.ofMillis(200))
        )
    }
}
