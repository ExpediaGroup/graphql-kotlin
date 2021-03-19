/*
 * Copyright 2021 Expedia, Inc
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

@file:Suppress("unused")

package com.expediagroup.graphql.examples.server.spring.query

import com.expediagroup.graphql.examples.server.spring.directives.TrackTimesInvoked
import com.expediagroup.graphql.server.operations.Query
import io.netty.util.internal.ThreadLocalRandom
import org.springframework.stereotype.Component
import java.util.UUID

/**
 * Used to stress test the performance of running many data fetchers.
 * Tests properties vs functions vs suspend functions.
 */
@Component
class StressQuery : Query {

    fun stressNode(traceId: String?, count: Int?): List<StressNode> {
        val id = traceId ?: getRandomStringFromThread()
        return (1..(count ?: 1)).map { StressNode(id) }
    }
}

@Suppress("MemberVisibilityCanBePrivate", "RedundantSuspendModifier")
class StressNode(val traceId: String) {

    val valueId: String = getRandomStringFromThread()

    fun functionId(): String = getRandomStringFromThread()

    suspend fun suspendId(): String = getRandomStringFromThread()

    @TrackTimesInvoked
    fun loggingFunctionId(): String = getRandomStringFromThread()

    @TrackTimesInvoked
    suspend fun suspendLoggingFunctionId(): String = getRandomStringFromThread()
}

private fun getRandomStringFromThread(): String {
    val random = ThreadLocalRandom.current()
    return UUID(random.nextLong(), random.nextLong()).toString()
}
