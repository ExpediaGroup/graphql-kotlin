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
        val randomString = (1..3072)
            .map { Random.nextInt(0, charPool.size) }
            .map(charPool::get)
            .joinToString("")
        val query = """$randomString query HeroNameAndFriends(${"$"}episode: Episode) {
          hero(episode: ${"$"}episode) {
            name
            friends {
              name
            }
          }
        }"""
        println("query: $query")
        val mutation = """$randomString mutation AddNewPet (${"$"}name: String!,${"$"}petType: PetType) {
          addPet(name:${"$"}name,petType:${"$"}petType) {
            name
            petType
          }
        }"""

        repeat(1000) {
            requests.add(GraphQLRequest(query))
        }
        requests.add(GraphQLRequest(mutation))
    }

    @Benchmark
    fun isMutationBenchmark(): Boolean {
        return requests.any(GraphQLRequest::isMutation)
    }
}
