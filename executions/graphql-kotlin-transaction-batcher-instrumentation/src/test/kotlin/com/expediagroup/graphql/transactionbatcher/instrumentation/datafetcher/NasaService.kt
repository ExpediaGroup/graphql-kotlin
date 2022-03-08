package com.expediagroup.graphql.transactionbatcher.instrumentation.datafetcher

import graphql.schema.DataFetchingEnvironment
import java.util.concurrent.CompletableFuture

class NasaService(
    private val astronautService: AstronautService,
    private val missionService: MissionService
) {
    fun getAstronaut(environment: DataFetchingEnvironment): CompletableFuture<Astronaut> =
        astronautService
            .getAstronaut(
                AstronautServiceRequest(environment.getArgument<String>("id").toInt()),
                environment
            )

    fun getMission(environment: DataFetchingEnvironment): CompletableFuture<Mission> =
        missionService
            .getMission(
                MissionServiceRequest(environment.getArgument<String>("id").toInt()),
                environment
            )
}
