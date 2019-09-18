package com.expediagroup.graphql.spring.execution

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

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = ["graphql.packages=com.expediagroup.graphql.spring.execution"])
@EnableAutoConfiguration
class SubscriptionHandlerIT(@LocalServerPort private var port: Int) {

    @Test
    fun `verify subscription`() {
        val request = getRequest("subscription { characters }")
        val output = ReplayProcessor.create<String>()

        val client = ReactorNettyWebSocketClient()
        val uri = URI.create("ws://localhost:$port/subscriptions")

        val sessionMono = client.execute(uri) { session ->
            session.send(Mono.just(session.textMessage(request)))
                .thenMany(session.receive().map(WebSocketMessage::getPayloadAsText))
                .subscribeWith(output)
                .take(5)
                .then()
        }

        StepVerifier.create(output.doOnSubscribe { sessionMono.subscribe() })
            .expectNext("{\"data\":{\"characters\":\"Alice\"}}")
            .expectNext("{\"data\":{\"characters\":\"Bob\"}}")
            .expectNext("{\"data\":{\"characters\":\"Chuck\"}}")
            .expectNext("{\"data\":{\"characters\":\"Dave\"}}")
            .expectNext("{\"data\":{\"characters\":\"Eve\"}}")
            .expectComplete()
            .verify()
    }

    @Test
    fun `verify subscription to counter`() {
        val request = getRequest("subscription { counter }")
        val output = ReplayProcessor.create<String>()

        val client = ReactorNettyWebSocketClient()
        val uri = URI.create("ws://localhost:$port/subscriptions")

        val sessionMono = client.execute(uri) { session ->
            session.send(Mono.just(session.textMessage(request)))
                .thenMany(session.receive().map(WebSocketMessage::getPayloadAsText))
                .subscribeWith(output)
                .take(5)
                .then()
        }

        StepVerifier.create(output.doOnSubscribe { sessionMono.subscribe() })
            .expectNextCount(5)
            .expectComplete()
            .verify()
    }

    private fun getRequest(query: String) = jacksonObjectMapper().writeValueAsString(GraphQLRequest(query))

    @Configuration
    class TestConfiguration {
        @Bean
        fun subscription(): Subscription = SimpleSubscription()
    }

    class SimpleSubscription : Subscription {

        private val characters = listOf("Alice", "Bob", "Chuck", "Dave", "Eve")

        fun characters(): Flux<String> = Flux.interval(Duration.ofMillis(100))
            .zipWithIterable(characters)
            .map { it.t2 }

        fun counter(): Flux<Int> = Flux.range(1, 5)
            .delayElements(Duration.ofMillis(100))
            .map { Random.nextInt() }
    }
}
