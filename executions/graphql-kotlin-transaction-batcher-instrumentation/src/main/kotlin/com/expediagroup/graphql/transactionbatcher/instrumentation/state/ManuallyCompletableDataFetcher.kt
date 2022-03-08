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
