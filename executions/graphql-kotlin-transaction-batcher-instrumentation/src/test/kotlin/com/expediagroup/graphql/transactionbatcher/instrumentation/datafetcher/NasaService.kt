package com.expediagroup.graphql.transactionbatcher.instrumentation.datafetcher

import graphql.schema.DataFetchingEnvironment
import java.util.concurrent.CompletableFuture

class NasaService(
    private val astronautService: AstronautService,
    private val missionService: MissionService
) {
    fun getAstronaut(env: DataFetchingEnvironment): CompletableFuture<Astronaut> =
        astronautService
            .getAstronaut(
                AstronautServiceRequest(env.getArgument<String>("id").toInt()),
                env
            )

    fun getMission(env: DataFetchingEnvironment): CompletableFuture<Mission> =
        missionService
            .getMission(
                MissionServiceRequest(env.getArgument<String>("id").toInt()),
                env
            )
}
