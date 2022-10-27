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

package com.expediagroup.graphql.dataloader.instrumentation.fixture.datafetcher

import com.expediagroup.graphql.dataloader.KotlinDataLoader
import com.expediagroup.graphql.dataloader.instrumentation.extensions.dispatchIfNeeded
import com.expediagroup.graphql.dataloader.instrumentation.fixture.domain.Astronaut
import com.expediagroup.graphql.dataloader.instrumentation.fixture.domain.Mission
import com.expediagroup.graphql.dataloader.instrumentation.fixture.domain.Planet
import com.expediagroup.graphql.dataloader.instrumentation.fixture.extensions.toListOfNullables
import com.expediagroup.graphql.dataloader.instrumentation.fixture.repository.AstronautRepository
import graphql.schema.DataFetchingEnvironment
import org.dataloader.DataLoader
import org.dataloader.DataLoaderFactory
import org.dataloader.DataLoaderOptions
import org.dataloader.stats.SimpleStatisticsCollector
import reactor.kotlin.core.publisher.toMono
import java.time.Duration
import java.util.Optional
import java.util.concurrent.CompletableFuture

data class AstronautServiceRequest(val id: Int)
data class CreateAstronautServiceRequest(val name: String)

class AstronautDataLoader : KotlinDataLoader<AstronautServiceRequest, Astronaut?> {
    override val dataLoaderName: String = "AstronautDataLoader"
    override fun getDataLoader(): DataLoader<AstronautServiceRequest, Astronaut?> =
        DataLoaderFactory.newDataLoader(
            { keys ->
                AstronautRepository
                    .getAstronauts(keys.map(AstronautServiceRequest::id))
                    .collectList()
                    .map(List<Optional<Astronaut>>::toListOfNullables)
                    .toFuture()
            },
            DataLoaderOptions.newOptions().setStatisticsCollector(::SimpleStatisticsCollector)
        )
}

class AstronautService {
    fun getAstronaut(
        request: AstronautServiceRequest,
        environment: DataFetchingEnvironment
    ): CompletableFuture<Astronaut> =
        environment
            .getDataLoader<AstronautServiceRequest, Astronaut>("AstronautDataLoader")
            .load(request)

    fun createAstronaut(
        request: CreateAstronautServiceRequest
    ): CompletableFuture<Astronaut> =
        Astronaut(100, request.name).toMono().delayElement(Duration.ofMillis(100)).toFuture()

    fun getAstronauts(
        requests: List<AstronautServiceRequest>,
        environment: DataFetchingEnvironment
    ): CompletableFuture<List<Astronaut?>> = when {
        requests.isNotEmpty() -> {
            environment
                .getDataLoader<AstronautServiceRequest, Astronaut>("AstronautDataLoader")
                .loadMany(requests)
        }
        else -> {
            AstronautRepository
                .getAstronauts(emptyList())
                .collectList()
                .map(List<Optional<Astronaut>>::toListOfNullables)
                .toFuture()
        }
    }

    fun getPlanets(
        request: AstronautServiceRequest,
        environment: DataFetchingEnvironment
    ): CompletableFuture<List<Planet>> {
        val missionsByAstronautDataLoader = environment.getDataLoader<MissionServiceRequest, List<Mission>>("MissionsByAstronautDataLoader")
        val planetsByMissionDataLoader = environment.getDataLoader<PlanetServiceRequest, List<Planet>>("PlanetsByMissionDataLoader")
        return missionsByAstronautDataLoader
            .load(MissionServiceRequest(0, astronautId = request.id))
            .thenCompose { missions ->
                planetsByMissionDataLoader
                    .loadMany(missions.map { PlanetServiceRequest(0, it.id) })
                    .dispatchIfNeeded(environment)
            }
            .thenApply { planetsByMission ->
                planetsByMission.flatten().distinct()
            }
    }
}
