package com.expediagroup.graphql.dataloader.instrumentation.fixture.datafetcher

import com.expediagroup.graphql.dataloader.KotlinDataLoader
import com.expediagroup.graphql.dataloader.instrumentation.extensions.getDataLoaderFromContext
import com.expediagroup.graphql.dataloader.instrumentation.fixture.domain.Planet
import com.expediagroup.graphql.dataloader.instrumentation.fixture.repository.PlanetRepository
import graphql.schema.DataFetchingEnvironment
import org.dataloader.BatchLoader
import java.util.concurrent.CompletableFuture

data class PlanetServiceRequest(val id: Int, val missionId: Int = -1)

class PlanetsByMissionDataLoader : KotlinDataLoader<PlanetServiceRequest, List<Planet>> {
    override val dataLoaderName: String = "PlanetsByMissionDataLoader"
    override fun getBatchLoader(): BatchLoader<PlanetServiceRequest, List<Planet>> =
        BatchLoader<PlanetServiceRequest, List<Planet>> { keys ->
            PlanetRepository
                .getPlanetsByMissionIds(keys.map(PlanetServiceRequest::missionId))
                .collectList()
                .toFuture()
        }
}

class PlanetService {
    fun getPlanets(
        request: PlanetServiceRequest,
        environment: DataFetchingEnvironment
    ): CompletableFuture<List<Planet>> =
        environment
            .getDataLoaderFromContext<PlanetServiceRequest, List<Planet>>("PlanetsByMissionDataLoader")
            .load(request)
}
