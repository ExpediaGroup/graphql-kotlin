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

package com.expediagroup.graphql.dataloader.instrumentation.fixture.repository

import com.expediagroup.graphql.dataloader.instrumentation.fixture.domain.Planet
import reactor.core.publisher.Flux
import reactor.kotlin.core.publisher.toFlux
import reactor.kotlin.core.publisher.toMono
import java.time.Duration

object PlanetRepository {
    private val planets = listOf(
        Planet(1, "Mercury", listOf(1, 3, 5, 7, 9, 11)),
        Planet(2, "Venus", listOf(2, 4, 6, 8, 10, 12)),
        Planet(3, "Earth", listOf(13, 14, 15)),
        Planet(4, "Mars", listOf(2, 3, 4, 7)),
        Planet(5, "Jupiter", listOf(9, 10, 12, 13)),
        Planet(6, "Saturn", listOf(11, 14, 15)),
        Planet(7, "Uranus", listOf(4, 5, 7, 10)),
        Planet(8, "Neptune", listOf(4, 8, 12))
    )

    fun getPlanetsByMissionIds(missionIds: List<Int>): Flux<List<Planet>> =
        missionIds.toFlux()
            .flatMapSequential { missionId ->
                planets.filter { planet -> planet.missions.contains(missionId) }
                    .toMono().delayElement(Duration.ofMillis(300))
            }
}
