/*
 * Copyright 2022 Expedia, Inc
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
package com.expediagroup.graphql.generator.federation.extensions

import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionException

/**
 * Returns a [CompletableFuture] that completes when all the input futures have completed,
 * with a list of the resolved values obtained from the completed futures.
 * If any of the input futures complete exceptionally, then the returned [CompletableFuture] also completes exceptionally
 * with a [java.util.concurrent.CompletionException] holding the exception as its cause.
 *
 * @return a [CompletableFuture] that completes with a list of resolved values.
 */
internal fun <T : Any?> List<CompletableFuture<out T>>.joinAll(): CompletableFuture<List<T>> =
    CompletableFuture.allOf(
        *toTypedArray()
    ).thenApply {
        map(CompletableFuture<out T>::join)
    }

/**
 * Returns a [CompletableFuture] that completes when all the input futures have completed,
 * with a list of [Result] objects that indicate whether each future completed successfully or with an error.
 * If a future completed with an error, the corresponding [Result] object will contain the exception that was thrown.
 *
 * @return a [CompletableFuture] that completes with a list of [Result] objects.
 */
@Suppress("TooGenericExceptionCaught")
internal fun <T : Any?> List<CompletableFuture<out T>>.allSettled(): CompletableFuture<List<Result<T>>> {
    val resultFutures = map { future ->
        CompletableFuture.supplyAsync {
            try {
                Result.success(future.get())
            } catch (e: Exception) {
                Result.failure<T>(e)
            }
        }
    }

    return CompletableFuture.allOf(
        *resultFutures.toTypedArray()
    ).thenApply {
        resultFutures.map(CompletableFuture<Result<T>>::join)
    }
}
