package com.expediagroup.graphql.transactionbatcher.transaction

import com.expediagroup.graphql.transactionbatcher.publisher.TriggeredPublisher
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap

class TransactionBatcher {

    private val queue = ConcurrentHashMap<
        TriggeredPublisher<Any, Any>,
        MutableList<BatcheableTransaction<Any, Any>>
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
        val triggeredPublisherKey = triggeredPublisher as TriggeredPublisher<Any, Any>
        return queue[triggeredPublisherKey]?.let { batcheableTransactions ->
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
            queue[triggeredPublisherKey] = mutableListOf(
                BatcheableTransaction(input, future as CompletableFuture<Any>, key)
            )
            future
        }
    }

    fun dispatch() {
        queue.forEach { (triggeredPublisher, batcheableTransactions) ->
            triggeredPublisher.trigger(batcheableTransactions, cacheRepository)
        }
        queue.clear()
    }
}

