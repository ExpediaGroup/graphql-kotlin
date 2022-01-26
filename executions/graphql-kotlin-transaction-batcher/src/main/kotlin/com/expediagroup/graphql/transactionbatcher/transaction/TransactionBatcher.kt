package com.expediagroup.graphql.transactionbatcher.transaction

import com.expediagroup.graphql.transactionbatcher.publisher.TriggeredPublisher
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap

class TransactionBatcher {

    private val queue = ConcurrentHashMap<
        Class<out TriggeredPublisher<Any, Any>>,
        TransactionBatcherQueueValue
        >()

    private val cacheRepository = TransactionBatcherCacheRepository()

    /**
     * deduplication
     * if there is a BatcheableTransaction with the same input (which could be defined by client),
     * return that future
     * else create a new BatcheableTransaction
     */
    @Suppress("UNCHECKED_CAST")
    fun <TInput : Any, TOutput : Any> enqueue(
        input: TInput,
        triggeredPublisher: TriggeredPublisher<TInput, TOutput>,
        key: String = input.toString()
    ): CompletableFuture<TOutput> {
        val queueKey = (triggeredPublisher as TriggeredPublisher<Any, Any>)::class.java
        return queue[queueKey]?.let { (_, batcheableTransactions) ->
            batcheableTransactions
                .find { transaction -> transaction.key == key }
                ?.let { match -> match.future as CompletableFuture<TOutput> }
                ?: run {
                    val future = CompletableFuture<TOutput>()
                    batcheableTransactions.add(
                        BatcheableTransaction(input, future as CompletableFuture<Any>, key)
                    )
                    future
                }
        } ?: run {
            val future = CompletableFuture<TOutput>()
            queue[queueKey] = TransactionBatcherQueueValue(
                triggeredPublisher,
                mutableListOf(
                    BatcheableTransaction(input, future as CompletableFuture<Any>, key)
                )
            )
            future
        }
    }

    fun dispatch() {
        queue.values.forEach { (triggeredPublisher, transactions) ->
            triggeredPublisher.trigger(transactions, cacheRepository)
        }
        queue.clear()
    }
}
