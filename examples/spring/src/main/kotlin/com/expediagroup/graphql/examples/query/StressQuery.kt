/*
 * Copyright 2020 Expedia, Inc
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

package com.expediagroup.graphql.examples.query

import com.expediagroup.graphql.annotations.GraphQLIgnore
import com.expediagroup.graphql.spring.operations.Query
import io.netty.util.internal.ThreadLocalRandom
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class StressQuery : Query {

    fun lazyStressQuery(traceId: String?, count: Int?): List<LazyStressNode> {
        val id = generateId(traceId)
        return (1..(count ?: 1)).map { LazyStressNode(id) }
    }

    fun eagerStressQuery(traceId: String?, count: Int?): List<EagerStressNode> {
        val id = generateId(traceId)
        return (1..(count ?: 1)).map { EagerStressNode(id) }
    }

    @GraphQLIgnore
    fun generateId(traceId: String?): String {
        if (traceId == null) {
            val random = ThreadLocalRandom.current()
            return UUID(random.nextLong(), random.nextLong()).toString()
        }
        return traceId
    }
}

@Suppress("MemberVisibilityCanBePrivate")
class LazyStressNode(val traceId: String) {

    fun functionId(): String {
        val random = ThreadLocalRandom.current()
        return UUID(random.nextLong(), random.nextLong()).toString()
    }

    fun ignoredId(): String {
        val random = ThreadLocalRandom.current()
        return UUID(random.nextLong(), random.nextLong()).toString()
    }

    suspend fun suspendId(): String {
        val random = ThreadLocalRandom.current()
        return UUID(random.nextLong(), random.nextLong()).toString()
    }

    suspend fun suspendIgnoredId(): String {
        val random = ThreadLocalRandom.current()
        return UUID(random.nextLong(), random.nextLong()).toString()
    }

    fun stressNode(count: Int?): List<LazyStressNode> {
        return (1..(count ?: 1)).map { LazyStressNode(traceId) }
    }
}

@Suppress("MemberVisibilityCanBePrivate")
class EagerStressNode(val traceId: String) {

    val valueId: String

    init {
        val random = ThreadLocalRandom.current()
        valueId = UUID(random.nextLong(), random.nextLong()).toString()
    }

    fun stressNode(count: Int?): List<EagerStressNode> {
        return (1..(count ?: 1)).map { EagerStressNode(traceId) }
    }
}
