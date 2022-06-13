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
import com.expediagroup.graphql.dataloader.instrumentation.fixture.domain.Mission
import com.expediagroup.graphql.dataloader.instrumentation.fixture.extensions.toListOfNullables
import com.expediagroup.graphql.dataloader.instrumentation.fixture.repository.MissionRepository
import graphql.schema.DataFetchingEnvironment
import org.dataloader.DataLoader
import org.dataloader.DataLoaderFactory
import org.dataloader.DataLoaderOptions
import org.dataloader.stats.SimpleStatisticsCollector
import java.util.Optional
import java.util.concurrent.CompletableFuture

data class MissionServiceRequest(val id: Int, val astronautId: Int = -1)

class MissionDataLoader : KotlinDataLoader<MissionServiceRequest, Mission?> {
    override val dataLoaderName: String = "MissionDataLoader"
    override fun getDataLoader(): DataLoader<MissionServiceRequest, Mission?> = DataLoaderFactory.newDataLoader(
        { keys ->
            MissionRepository
                .getMissions(keys.map(MissionServiceRequest::id))
                .collectList()
                .map(List<Optional<Mission>>::toListOfNullables)
                .toFuture()
        },
        DataLoaderOptions.newOptions().setStatisticsCollector(::SimpleStatisticsCollector)
    )
}

class MissionsByAstronautDataLoader : KotlinDataLoader<MissionServiceRequest, List<Mission>> {
    override val dataLoaderName: String = "MissionsByAstronautDataLoader"
    override fun getDataLoader(): DataLoader<MissionServiceRequest, List<Mission>> = DataLoaderFactory.newDataLoader(
        { keys ->
            MissionRepository
                .getMissionsByAstronautIds(keys.map(MissionServiceRequest::astronautId))
                .collectList().toFuture()
        },
        DataLoaderOptions.newOptions().setStatisticsCollector(::SimpleStatisticsCollector)
    )
}

class MissionService {
    fun getMission(
        request: MissionServiceRequest,
        environment: DataFetchingEnvironment
    ): CompletableFuture<Mission> =
        environment
            .getDataLoader<MissionServiceRequest, Mission>("MissionDataLoader")
            .load(request)

    fun getMissions(
        requests: List<MissionServiceRequest>,
        environment: DataFetchingEnvironment
    ): CompletableFuture<List<Mission?>> = when {
        requests.isNotEmpty() -> {
            environment
                .getDataLoader<MissionServiceRequest, Mission>("MissionDataLoader")
                .loadMany(requests)
        }
        else -> {
            MissionRepository
                .getMissions(emptyList())
                .collectList()
                .map(List<Optional<Mission>>::toListOfNullables)
                .toFuture()
        }
    }

    fun getMissionsByAstronaut(
        request: MissionServiceRequest,
        environment: DataFetchingEnvironment
    ): CompletableFuture<List<Mission>> =
        environment
            .getDataLoader<MissionServiceRequest, List<Mission>>("MissionsByAstronautDataLoader")
            .load(request)
}
