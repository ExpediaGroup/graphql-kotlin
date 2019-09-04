package com.expedia.graphql.sample.subscriptions

import com.expedia.graphql.annotations.GraphQLDescription
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Duration
import kotlin.random.Random

@Component
class SimpleSubscription : Subscription {

    @GraphQLDescription("Returns a single value")
    fun singleValueSubscription(): Mono<Int> = Mono.just(1)

    @GraphQLDescription("Returns a random number every second")
    fun counter(): Flux<Int> = Flux.interval(Duration.ofSeconds(1)).map { Random.nextInt() }
}
