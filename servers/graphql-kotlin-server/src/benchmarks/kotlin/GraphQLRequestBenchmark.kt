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

package com.expediagroup.graphql.server

import com.expediagroup.graphql.server.extensions.isMutation
import com.expediagroup.graphql.server.types.GraphQLRequest
import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.annotations.Setup
import org.openjdk.jmh.annotations.State
import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.Fork
import org.openjdk.jmh.annotations.Warmup
import org.openjdk.jmh.annotations.Measurement
import java.util.concurrent.TimeUnit
import kotlin.random.Random

@State(Scope.Benchmark)
@Fork(1)
@Warmup(iterations = 2)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
open class GraphQLRequestBenchmark {
    private val requests = mutableListOf<GraphQLRequest>()

    @Setup
    fun setUp() {
        val charPool = ('a'..'z') + ('A'..'Z') + ('0'..'9')
        val range = (1..3072)
        repeat(50) {
            val randomStringForQuery = range
                .map { Random.nextInt(0, charPool.size) }
                .map(charPool::get)
                .joinToString("")
            val query = """$randomStringForQuery query HeroNameAndFriends(${"$"}episode: Episode) {
              hero(episode: ${"$"}episode) {
                name
                friends {
                  name
                }
              }
            }"""
            requests.add(GraphQLRequest(query))
        }
        val randomStringForMutation = range
            .map { Random.nextInt(0, charPool.size) }
            .map(charPool::get)
            .joinToString("")

        val mutation = """$randomStringForMutation mutation AddNewPet (${"$"}name: String!,${"$"}petType: PetType) {
              addPet(name:${"$"}name,petType:${"$"}petType) {
                name
                petType
              }
            }"""
        requests.add(GraphQLRequest(mutation))
    }

    @Benchmark
    fun isMutationBenchmark(): Boolean {
        return requests.any(GraphQLRequest::isMutation)
    }
}
