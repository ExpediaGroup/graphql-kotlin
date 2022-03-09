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

package com.expediagroup.graphql.transactionbatcher.instrumentation.state

import graphql.execution.Async
import graphql.schema.DataFetcher
import graphql.schema.DataFetchingEnvironment
import java.util.concurrent.CompletableFuture

/**
 * decorator that stores the original dataFetcher result (it's always a completable future)
 * and returns an uncompleted future
 * once a certain level of all operations was dispatched
 * call complete of previously returned future with original future results to let graphql-java handle all futures
 */
class ManuallyCompletableDataFetcher(
    private val originalDataFetcher: DataFetcher<*>
) : DataFetcher<CompletableFuture<Any?>> {

    private val manualFuture: CompletableFuture<Any?> = CompletableFuture()
    private var originalFuture: CompletableFuture<Any?>? = null
    private var originalExpressionException: Exception? = null

    override fun get(environment: DataFetchingEnvironment): CompletableFuture<Any?> {
        try {
            val fetchedValueRaw = originalDataFetcher.get(environment)
            originalFuture = Async.toCompletableFuture(fetchedValueRaw)
        } catch (e: Exception) {
            originalExpressionException = e
        }
        return manualFuture
    }

    fun complete() {
        when {
            originalExpressionException != null -> manualFuture.completeExceptionally(originalExpressionException)
            else -> originalFuture?.handle { result, exception ->
                when {
                    exception != null -> manualFuture.completeExceptionally(exception)
                    else -> manualFuture.complete(result)
                }
            }
        }
    }
}
