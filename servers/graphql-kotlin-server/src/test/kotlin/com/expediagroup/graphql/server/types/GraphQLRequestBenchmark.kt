package com.expediagroup.graphql.server.types

import com.expediagroup.graphql.server.extensions.isMutation
import org.openjdk.jmh.annotations.*
import org.openjdk.jmh.runner.Runner
import org.openjdk.jmh.runner.options.OptionsBuilder
import java.util.concurrent.TimeUnit

@State(Scope.Benchmark)
@Fork(1)
@Warmup(iterations = 0)
@Measurement(iterations = 1, time = 1, timeUnit = TimeUnit.SECONDS)
open class GraphQLRequestBenchmark {
    private val requests = mutableListOf<GraphQLRequest>()

    @Setup
    fun setUp() {
        val query = """query HeroNameAndFriends("\$"episode: Episode) {
          hero(episode: "\$"episode) {
            name
            friends {
              name
            }
          }
        }"""
        val mutation = """mutation AddNewPet ("\$"name: String!,"\$"petType: PetType) {
              addPet(name:"\$"name,petType:"\$"petType) {
                name
                petType
              }
            }
        """
        requests.add(GraphQLRequest(query))
        requests.add(GraphQLRequest(query))
        requests.add(GraphQLRequest(query))
        requests.add(GraphQLRequest(query))
        requests.add(GraphQLRequest(query))
        requests.add(GraphQLRequest(query))
        requests.add(GraphQLRequest(query))
        requests.add(GraphQLRequest(query))
        requests.add(GraphQLRequest(query))
        requests.add(GraphQLRequest(query))
        requests.add(GraphQLRequest(query))
        requests.add(GraphQLRequest(query))
        requests.add(GraphQLRequest(query))
        requests.add(GraphQLRequest(mutation))
    }

    @Benchmark
    fun isMutationBenchMark(): Boolean {
        return requests.any(GraphQLRequest::isMutation)
    }
}

fun main() {
    val options = OptionsBuilder()
        .include(GraphQLRequestBenchmark::class.java.simpleName)
        .output("benchmark_sequence.log")
        .build()
    Runner(options).run()
}
