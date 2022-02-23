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

package com.expediagroup.graphql.transactionbatcher.transaction

import com.expediagroup.graphql.transactionbatcher.publisher.Astronaut
import com.expediagroup.graphql.transactionbatcher.publisher.AstronautService
import com.expediagroup.graphql.transactionbatcher.publisher.AstronautServiceRequest
import com.expediagroup.graphql.transactionbatcher.publisher.Mission
import com.expediagroup.graphql.transactionbatcher.publisher.MissionService
import com.expediagroup.graphql.transactionbatcher.publisher.MissionServiceRequest
import org.junit.jupiter.api.Test
import reactor.kotlin.core.publisher.toFlux
import kotlin.test.assertEquals

class TransactionBatcherTest {
    @Test
    fun `TransactionBatcher should batch transactions`() {
        val transactionBatcher = TransactionBatcher()

        val astronautService = AstronautService(transactionBatcher)
        val astronautRequest1 = AstronautServiceRequest(1)
        val astronautRequest2 = AstronautServiceRequest(2)
        val astronauts = mutableListOf<Astronaut>()

        listOf(astronautRequest1, astronautRequest2).toFlux()
            .flatMapSequential(astronautService::getAstronaut)
            .collectList()
            .subscribe(astronauts::addAll)

        transactionBatcher.dispatch()
        Thread.sleep(1000)

        assertEquals("Buzz Aldrin", astronauts[0].name)
        assertEquals("William Anders", astronauts[1].name)
    }

    @Test
    fun `TransactionBatcher should batch multiple transaction types`() {
        val transactionBatcher = TransactionBatcher()

        val astronautService = AstronautService(transactionBatcher)
        val astronautRequest1 = AstronautServiceRequest(3)
        val astronautRequest2 = AstronautServiceRequest(2)
        val astronautRequest3 = AstronautServiceRequest(1)
        val astronauts = mutableListOf<Astronaut>()

        val missionService = MissionService(transactionBatcher)
        val missionRequest1 = MissionServiceRequest(4)
        val missionRequest2 = MissionServiceRequest(3)
        val missionRequest3 = MissionServiceRequest(2)
        val missions = mutableListOf<Mission>()

        listOf(astronautRequest1, astronautRequest2, astronautRequest3).toFlux()
            .flatMapSequential(astronautService::getAstronaut)
            .collectList()
            .subscribe(astronauts::addAll)

        listOf(missionRequest1, missionRequest2, missionRequest3).toFlux()
            .flatMapSequential(missionService::getMission)
            .collectList()
            .subscribe(missions::addAll)

        transactionBatcher.dispatch()
        Thread.sleep(1000)

        assertEquals(3, astronautService.getAstronautCallCount.get())
        assertEquals(3, astronautService.produceArguments[0][0].id)
        assertEquals(2, astronautService.produceArguments[0][1].id)
        assertEquals(1, astronautService.produceArguments[0][2].id)
        assertEquals("Neil Armstrong", astronauts[0].name)
        assertEquals("William Anders", astronauts[1].name)
        assertEquals("Buzz Aldrin", astronauts[2].name)

        assertEquals(3, missionService.getMissionCallCount.get())
        assertEquals(4, missionService.produceArguments[0][0].id)
        assertEquals(3, missionService.produceArguments[0][1].id)
        assertEquals(2, missionService.produceArguments[0][2].id)
        assertEquals("Apollo 6", missions[0].designation)
        assertEquals("Apollo 5", missions[1].designation)
        assertEquals("Apollo 4", missions[2].designation)
    }

    @Test
    fun `TransactionBatcher should deduplicate transactions`() {
        val transactionBatcher = TransactionBatcher()

        val astronautService = AstronautService(transactionBatcher)
        val astronautRequest1 = AstronautServiceRequest(3)
        val astronautRequest1Repeated = AstronautServiceRequest(3)
        val astronautRequest2 = AstronautServiceRequest(1)
        val astronauts = mutableListOf<Astronaut>()

        val missionService = MissionService(transactionBatcher)
        val missionRequest1 = MissionServiceRequest(4)
        val missionRequest1Repeated = MissionServiceRequest(4)
        val missionRequest2 = MissionServiceRequest(2)
        val missions = mutableListOf<Mission>()

        listOf(astronautRequest1, astronautRequest1Repeated, astronautRequest2).toFlux()
            .flatMapSequential(astronautService::getAstronaut)
            .collectList()
            .subscribe(astronauts::addAll)

        listOf(missionRequest1, missionRequest1Repeated, missionRequest2).toFlux()
            .flatMapSequential(missionService::getMission)
            .collectList()
            .subscribe(missions::addAll)

        transactionBatcher.dispatch()
        Thread.sleep(1000)

        assertEquals(3, astronautService.getAstronautCallCount.get())
        assertEquals(2, astronautService.produceArguments[0].size)
        assertEquals(3, astronautService.produceArguments[0][0].id)
        assertEquals(1, astronautService.produceArguments[0][1].id)
        assertEquals("Neil Armstrong", astronauts[0].name)
        assertEquals("Neil Armstrong", astronauts[1].name)
        assertEquals("Buzz Aldrin", astronauts[2].name)

        assertEquals(3, missionService.getMissionCallCount.get())
        assertEquals(2, missionService.produceArguments[0].size)
        assertEquals(4, missionService.produceArguments[0][0].id)
        assertEquals(2, missionService.produceArguments[0][1].id)
        assertEquals("Apollo 6", missions[0].designation)
        assertEquals("Apollo 6", missions[1].designation)
        assertEquals("Apollo 4", missions[2].designation)
    }

    @Test
    fun `TransactionBatcher should cache transaction results`() {
        val transactionBatcher = TransactionBatcher()

        val astronautService = AstronautService(transactionBatcher)
        val astronautRequest1 = AstronautServiceRequest(3)
        val astronautRequest2 = AstronautServiceRequest(2)
        val astronautRequest3 = AstronautServiceRequest(1)
        val astronauts = mutableListOf<Astronaut>()

        listOf(astronautRequest1, astronautRequest2, astronautRequest3).toFlux()
            .flatMapSequential(astronautService::getAstronaut)
            .collectList()
            .subscribe(astronauts::addAll)

        transactionBatcher.dispatch()
        Thread.sleep(1000)

        assertEquals(3, astronautService.getAstronautCallCount.get())
        assertEquals(3, astronautService.produceArguments[0][0].id)
        assertEquals(2, astronautService.produceArguments[0][1].id)
        assertEquals(1, astronautService.produceArguments[0][2].id)
        assertEquals("Neil Armstrong", astronauts[0].name)
        assertEquals("William Anders", astronauts[1].name)
        assertEquals("Buzz Aldrin", astronauts[2].name)
        astronauts.clear()

        listOf(astronautRequest3, astronautRequest1, astronautRequest2).toFlux()
            .flatMapSequential(astronautService::getAstronaut)
            .collectList()
            .subscribe(astronauts::addAll)

        transactionBatcher.dispatch()
        Thread.sleep(1000)
        assertEquals(6, astronautService.getAstronautCallCount.get())
        assertEquals(0, astronautService.produceArguments[1].size)
        assertEquals("Buzz Aldrin", astronauts[0].name)
        assertEquals("Neil Armstrong", astronauts[1].name)
        assertEquals("William Anders", astronauts[2].name)
    }

    @Test
    fun `TransactionBatcher should resolve from cache and apply transaction`() {
        val transactionBatcher = TransactionBatcher()

        val astronautService = AstronautService(transactionBatcher)
        val astronautRequest1 = AstronautServiceRequest(3)
        val astronautRequest2 = AstronautServiceRequest(2)
        val astronauts = mutableListOf<Astronaut>()

        listOf(astronautRequest1, astronautRequest2).toFlux()
            .flatMapSequential(astronautService::getAstronaut)
            .collectList()
            .subscribe(astronauts::addAll)

        transactionBatcher.dispatch()
        Thread.sleep(1000)

        assertEquals(2, astronautService.getAstronautCallCount.get())
        assertEquals(3, astronautService.produceArguments[0][0].id)
        assertEquals(2, astronautService.produceArguments[0][1].id)
        assertEquals("Neil Armstrong", astronauts[0].name)
        assertEquals("William Anders", astronauts[1].name)
        astronauts.clear()

        val astronautRequest3 = AstronautServiceRequest(1)

        listOf(astronautRequest3, astronautRequest1, astronautRequest2).toFlux()
            .flatMapSequential(astronautService::getAstronaut)
            .collectList()
            .subscribe(astronauts::addAll)

        transactionBatcher.dispatch()
        Thread.sleep(1000)
        assertEquals(5, astronautService.getAstronautCallCount.get())
        assertEquals(1, astronautService.produceArguments[1].size)
        assertEquals("Buzz Aldrin", astronauts[0].name)
        assertEquals("Neil Armstrong", astronauts[1].name)
        assertEquals("William Anders", astronauts[2].name)
    }
}
