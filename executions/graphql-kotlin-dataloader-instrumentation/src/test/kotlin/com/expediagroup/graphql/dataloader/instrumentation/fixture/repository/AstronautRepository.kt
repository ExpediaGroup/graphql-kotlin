package com.expediagroup.graphql.dataloader.instrumentation.fixture.repository

import com.expediagroup.graphql.dataloader.instrumentation.fixture.domain.Astronaut
import reactor.core.publisher.Flux
import reactor.kotlin.core.publisher.toFlux
import reactor.kotlin.core.publisher.toMono
import java.time.Duration

object AstronautRepository {
    private val astronauts = listOf(
        Astronaut(1, "Buzz Aldrin"),
        Astronaut(2, "William Anders"),
        Astronaut(3, "Neil Armstrong"),
        Astronaut(4, "Alan Bean"),
        Astronaut(5, "Frank Borman"),
        Astronaut(6, "Eugene Cernan"),
        Astronaut(7, "Roger B. Chaffee"),
        Astronaut(8, "Michael Collins"),
        Astronaut(9, "C. 'Pete' Conrad"),
        Astronaut(10, "Walt Cunningham"),
        Astronaut(11, "Charles Duke"),
        Astronaut(12, "Donn Eisele"),
        Astronaut(13, "Ronald Evans"),
        Astronaut(14, "Gus Grissom"),
        Astronaut(15, "Richard Gordon"),
        Astronaut(16, "Fred Haise"),
        Astronaut(17, "James Irwin"),
        Astronaut(18, "James Lovell"),
        Astronaut(19, "T. Kenneth Mattingly"),
        Astronaut(20, "James McDivitt"),
        Astronaut(21, "Edgar Mitchell"),
        Astronaut(22, "Stuart Roosa"),
        Astronaut(23, "Wally Schirra"),
        Astronaut(24, "Harrison Schmitt"),
        Astronaut(25, "Russell Schweickart"),
        Astronaut(26, "David Scott"),
        Astronaut(27, "Alan Shepard"),
        Astronaut(28, "Thomas Stafford"),
        Astronaut(29, "Jack Swigert"),
        Astronaut(30, "Ed White"),
        Astronaut(31, "John Young"),
        Astronaut(32, "Alfred Worden")
    )

    fun getAstronauts(astronautIds: List<Int>): Flux<Astronaut?> =
        astronautIds
            .map { astronautId -> astronauts[astronautId] }.toMono()
            .delayElement(Duration.ofMillis(200))
            .flatMapMany { astronauts -> astronauts.toFlux() }
}
