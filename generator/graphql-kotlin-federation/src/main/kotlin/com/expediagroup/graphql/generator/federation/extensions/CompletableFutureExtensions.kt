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

/**
 * Returns a new CompletableFuture of a list with the resolved values of the given CompletableFutures.
 * the returned completableFuture will complete when all given CompletableFutures complete.
 * If any of the given CompletableFutures complete exceptionally, then the returned CompletableFuture also does so,
 * with a CompletionException holding this exception as its cause.
 */
internal fun <T : Any?> List<CompletableFuture<out T>>.joinAll(): CompletableFuture<List<T>> =
    CompletableFuture.allOf(
        *toTypedArray()
    ).thenApply {
        map(CompletableFuture<out T>::join)
    }

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

