/*
 * Copyright 2019 Expedia, Inc
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

package com.expediagroup.graphql.sample.query

import com.expediagroup.graphql.annotations.GraphQLDescription
import com.expediagroup.graphql.spring.annotation.Query
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
