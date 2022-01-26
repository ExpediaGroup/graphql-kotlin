package com.expediagroup.graphql.transactionbatcher.publisher

import com.expediagroup.graphql.transactionbatcher.transaction.TransactionBatcher
import org.reactivestreams.Publisher
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toFlux
import reactor.kotlin.core.publisher.toMono
import java.time.Duration
import java.util.concurrent.atomic.AtomicInteger

data class AstronautServiceRequest(val id: Int)
data class Astronaut(val id: Int, val name: String)

class AstronautService(
    private val transactionBatcher: TransactionBatcher
) : TriggeredPublisher<AstronautServiceRequest, Astronaut>() {

    val produceArguments: MutableList<List<AstronautServiceRequest>> = mutableListOf()
    val getAstronautCallCount: AtomicInteger = AtomicInteger(0)

    companion object {
        private val astronauts = mapOf(
            1 to Pair(Astronaut(1, "Buzz Aldrin"), Duration.ofMillis(300)),
            2 to Pair(Astronaut(2, "William Anders"), Duration.ofMillis(600)),
            3 to Pair(Astronaut(3, "Neil Armstrong"), Duration.ofMillis(200))
        )
    }

    fun getAstronaut(request: AstronautServiceRequest): Mono<Astronaut> {
        getAstronautCallCount.incrementAndGet()
        val future = this.transactionBatcher.enqueue(request, this)
        return future.toMono()
    }

    override fun produce(input: List<AstronautServiceRequest>): Publisher<Astronaut> {
        produceArguments.add(input)
        return input.toFlux()
            .flatMapSequential { request ->
                { astronauts[request.id] }
                    .toMono()
                    .flatMap { (astronaut, delay) ->
                        astronaut.toMono().delayElement(delay)
                    }
            }
    }
}
