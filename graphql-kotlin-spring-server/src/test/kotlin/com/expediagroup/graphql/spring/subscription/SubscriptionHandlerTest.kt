package com.expediagroup.graphql.spring.subscription

import com.expediagroup.graphql.annotations.GraphQLDescription
import com.expediagroup.graphql.spring.model.GraphQLRequest
import com.expediagroup.graphql.spring.operations.Subscription
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.junit.jupiter.api.Test
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.socket.WebSocketMessage
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.publisher.ReplayProcessor
import reactor.test.StepVerifier
import java.net.URI
import java.time.Duration
import kotlin.random.Random

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = ["graphql.packages=com.expediagroup.graphql.spring.subscription"])
@EnableAutoConfiguration
class SubscriptionHandlerTest(@LocalServerPort private var port: Int) {

    @Test
    fun `verify subscription`() {
        val request = jacksonObjectMapper().writeValueAsString(GraphQLRequest(query = "subscription { characters }"))
        val output = ReplayProcessor.create<String>()

        val client = ReactorNettyWebSocketClient()
        val sessionMono = client.execute(URI.create("ws://localhost:$port/subscriptions")) { session ->
            session.send(Mono.just(session.textMessage(request)))
                .thenMany(session.receive()
                    .map(WebSocketMessage::getPayloadAsText)
                ).doOnEach { println("\tRECEIVED: ${it.get()}") }
                .doOnTerminate {
                    println("\tTERMINATING")
                    session.close()
                }
                .subscribeWith(output)
                .then()
        }

        StepVerifier.create(output.doOnSubscribe { sessionMono.subscribe() }.timeout(Duration.ofSeconds(1)))
            .expectNext("{\"data\":{\"characters\":\"Alice\"}}")
            .expectNext("{\"data\":{\"characters\":\"Bob\"}}")
            .expectNext("{\"data\":{\"characters\":\"Chuck\"}}")
            .expectNext("{\"data\":{\"characters\":\"Dave\"}}")
            .expectNext("{\"data\":{\"characters\":\"Eve\"}}")
            .expectComplete()
            .verifyThenAssertThat()
            .tookLessThan(Duration.ofSeconds(1))
    }

    @Configuration
    class TestConfiguration {
        @Bean
        fun subscription(): Subscription {
            println("creating sample subscription")
            return SimpleSubscription()
        }
    }

    class SimpleSubscription : Subscription {

        private val characters = listOf("Alice", "Bob", "Chuck", "Dave", "Eve")

        @GraphQLDescription("Returns a random number every second")
        fun characters(): Flux<String> = Flux.interval(Duration.ofMillis(100))
            .zipWithIterable(characters)
            .map { it.t2 }

        fun counter(): Flux<Int> = Flux.range(1, 5)
            .delayElements(Duration.ofMillis(100))
            .map { Random.nextInt() }
    }
}
