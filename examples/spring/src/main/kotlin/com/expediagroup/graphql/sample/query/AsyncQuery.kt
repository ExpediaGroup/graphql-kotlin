package com.expediagroup.graphql.sample.query

import com.expediagroup.graphql.annotations.GraphQLDescription
import org.springframework.stereotype.Component
import java.util.concurrent.CompletableFuture

/**
 * Example async queries.
 */
@Component
class AsyncQuery : Query {

    @GraphQLDescription("Delays for given amount and then echos the string back."
            + " The default async executor will work with CompletableFuture."
            + " To use other rx frameworks you'll need to install a custom one to handle the types correctly.")
    fun delayedEchoUsingCompletableFuture(msg: String, delaySeconds: Int): CompletableFuture<String> {
        val future = CompletableFuture<String>()
        Thread {
            Thread.sleep(delaySeconds * 1000L)
            future.complete(msg)
        }.start()
        return future
    }
}
