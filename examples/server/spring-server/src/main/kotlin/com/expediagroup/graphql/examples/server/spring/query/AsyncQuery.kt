/*
 * Copyright 2021 Expedia, Inc
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

package com.expediagroup.graphql.examples.server.spring.query

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.server.operations.Query
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.time.delay
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.time.Duration
import java.util.concurrent.CompletableFuture

/**
 * Example async queries.
 */
@Component
class AsyncQuery : Query {

    @GraphQLDescription(
        "Delays for given amount of time using CompletableFuture and then echoes the string back." +
            " The default async executor will work with CompletableFuture." +
            " To use other rx frameworks you'll need to install a custom one to handle the types correctly."
    )
    fun delayedEchoUsingCompletableFuture(msg: String, delayMilliseconds: Int): CompletableFuture<String> {
        val future = CompletableFuture<String>()
        Thread {
            Thread.sleep(delayMilliseconds.toLong())
            future.complete(msg)
        }.start()
        return future
    }

    @GraphQLDescription("Delays for given amount of time using Reactor Mono and then echoes the string back.")
    fun delayedEchoUsingReactorMono(msg: String, delayMilliseconds: Int): Mono<String> =
        Mono.just(msg).delayElement(Duration.ofMillis(delayMilliseconds.toLong()))

    @GraphQLDescription("Delays for given amount of time using Coroutine and then echoes the string back.")
    suspend fun delayedEchoUsingCoroutine(msg: String, delayMilliseconds: Int): String = coroutineScope {
        delay(Duration.ofMillis(delayMilliseconds.toLong()))
        msg
    }
}
