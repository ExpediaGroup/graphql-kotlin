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
import com.expediagroup.graphql.dataloader.instrumentation.fixture.domain.Planet
import com.expediagroup.graphql.dataloader.instrumentation.fixture.repository.PlanetRepository
import graphql.schema.DataFetchingEnvironment
import org.dataloader.DataLoader
import org.dataloader.DataLoaderFactory
import org.dataloader.DataLoaderOptions
import org.dataloader.stats.SimpleStatisticsCollector
import java.util.concurrent.CompletableFuture

data class PlanetServiceRequest(val id: Int, val missionId: Int = -1)

class PlanetsByMissionDataLoader : KotlinDataLoader<PlanetServiceRequest, List<Planet>> {
    override val dataLoaderName: String = "PlanetsByMissionDataLoader"
    override fun getDataLoader(): DataLoader<PlanetServiceRequest, List<Planet>> = DataLoaderFactory.newDataLoader(
        { keys ->
            PlanetRepository
                .getPlanetsByMissionIds(keys.map(PlanetServiceRequest::missionId))
                .collectList()
                .toFuture()
        },
        DataLoaderOptions.newOptions().setStatisticsCollector(::SimpleStatisticsCollector)
    )
}

class PlanetService {
    fun getPlanets(
        request: PlanetServiceRequest,
        environment: DataFetchingEnvironment
    ): CompletableFuture<List<Planet>> =
        environment
            .getDataLoader<PlanetServiceRequest, List<Planet>>("PlanetsByMissionDataLoader")
            .load(request)
}
