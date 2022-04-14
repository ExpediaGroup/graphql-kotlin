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
import com.expediagroup.graphql.dataloader.instrumentation.extensions.getDataLoaderFromContext
import com.expediagroup.graphql.dataloader.instrumentation.fixture.domain.Astronaut
import com.expediagroup.graphql.dataloader.instrumentation.fixture.extensions.toListOfNullables
import com.expediagroup.graphql.dataloader.instrumentation.fixture.repository.AstronautRepository
import graphql.schema.DataFetchingEnvironment
import org.dataloader.BatchLoader
import java.util.Optional
import java.util.concurrent.CompletableFuture

data class AstronautServiceRequest(val id: Int)

class AstronautDataLoader : KotlinDataLoader<AstronautServiceRequest, Astronaut?> {
    override val dataLoaderName: String = "AstronautDataLoader"
    override fun getBatchLoader(): BatchLoader<AstronautServiceRequest, Astronaut?> =
        BatchLoader<AstronautServiceRequest, Astronaut?> { keys ->
            AstronautRepository
                .getAstronauts(keys.map(AstronautServiceRequest::id))
                .collectList()
                .map(List<Optional<Astronaut>>::toListOfNullables)
                .toFuture()
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

    fun getAstronauts(
        requests: List<AstronautServiceRequest>,
        environment: DataFetchingEnvironment
    ): CompletableFuture<List<Astronaut?>> = when {
        requests.isNotEmpty() -> {
            environment
                .getDataLoaderFromContext<AstronautServiceRequest, Astronaut>("AstronautDataLoader")
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
}
