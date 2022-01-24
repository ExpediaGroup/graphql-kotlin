package com.expediagroup.graphql.transactionbatcher.publisher

import com.expediagroup.graphql.transactionbatcher.transaction.TransactionBatcher
import org.reactivestreams.Publisher
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toFlux
import reactor.kotlin.core.publisher.toMono
import java.time.Duration
import java.util.concurrent.atomic.AtomicInteger

data class MissionServiceRequest(val id: Int)
data class Mission(
    val id: Int,
    val designation: String,
    val crew: List<Int>
)

class MissionService(
    private val transactionBatcher: TransactionBatcher
) : TriggeredPublisher<MissionServiceRequest, Mission>() {

    val produceArguments: MutableList<List<MissionServiceRequest>> = mutableListOf()
    val getMissionCallCount: AtomicInteger = AtomicInteger(0)

    companion object {
        private val missions = mapOf(
            2 to Pair(Mission(2, "Apollo 4", listOf(14, 30, 7)), Duration.ofMillis(100)),
            3 to Pair(Mission(3, "Apollo 5", listOf(23, 10, 12)), Duration.ofMillis(400)),
            4 to Pair(Mission(4, "Apollo 6", listOf(1, 28, 31, 6)), Duration.ofMillis(300))
        )
    }

    fun getMission(request: MissionServiceRequest): Mono<Mission> {
        getMissionCallCount.incrementAndGet()
        val future = this.transactionBatcher.enqueue(request, this)
        return future.toMono()
    }

    override fun produce(input: List<MissionServiceRequest>): Publisher<Mission> {
        produceArguments.add(input)
        return input.toFlux()
            .flatMapSequential { request ->
                { missions[request.id] }
                    .toMono()
                    .flatMap { (astronaut, delay) ->
                        astronaut.toMono().delayElement(delay)
                    }
            }
    }

}
