package com.expediagroup.graphql.dataloader.instrumentation.fixture.repository

import com.expediagroup.graphql.dataloader.instrumentation.fixture.domain.Mission
import reactor.core.publisher.Flux
import reactor.kotlin.core.publisher.toFlux
import reactor.kotlin.core.publisher.toMono
import java.time.Duration

object MissionRepository {
    private val missions = listOf(
        Mission(1, "Apollo 1", listOf(14, 30, 7)),
        Mission(2, "Apollo 4", listOf(14, 30, 1)),
        Mission(3, "Apollo 5", listOf()),
        Mission(4, "Apollo 6", listOf()),
        Mission(5, "Apollo 7", listOf(23, 10, 12)),
        Mission(6, "Apollo 8", listOf(5, 18, 2)),
        Mission(7, "Apollo 9", listOf(20, 26, 25)),
        Mission(8, "Apollo 10", listOf(1, 28, 31, 6)),
        Mission(9, "Apollo 11", listOf(3, 1, 8)),
        Mission(10, "Apollo 12", listOf(9, 15, 4)),
        Mission(11, "Apollo 13", listOf(18, 29, 16)),
        Mission(12, "Apollo 14", listOf(27, 22, 21)),
        Mission(13, "Apollo 15", listOf(26, 32, 17)),
        Mission(14, "Apollo 16", listOf(31, 19, 11)),
        Mission(15, "Apollo 17", listOf(6, 13, 24))
    )

    fun getMissions(missionIds: List<Int>): Flux<Mission?> =
        missionIds
            .map { missionId -> missions[missionId] }.toMono()
            .delayElement(Duration.ofMillis(100))
            .flatMapMany { missions -> missions.toFlux() }

    fun getMissionsByAstronautIds(astronautIds: List<Int>): Flux<List<Mission>> =
        astronautIds.toFlux()
            .flatMapSequential { astronautId ->
                missions.filter { mission -> mission.crew.contains(astronautId) }
                    .toMono().delayElement(Duration.ofMillis(300))
            }
}
