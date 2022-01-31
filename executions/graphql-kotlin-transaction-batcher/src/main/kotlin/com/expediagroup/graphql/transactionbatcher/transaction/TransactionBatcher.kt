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

package com.expediagroup.graphql.transactionbatcher.transaction

import com.expediagroup.graphql.transactionbatcher.publisher.TriggeredPublisher
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap

/**
 * Type for [TransactionBatcher.queue] value, storing the [triggeredPublisher] instance
 * and list of [transactions] that need to be executed by it
 */
data class TransactionBatcherQueueValue(
    val triggeredPublisher: TriggeredPublisher<Any, Any>,
    val transactions: MutableList<BatcheableTransaction<Any, Any>>
)

/**
 * Holds logic to apply batching, deduplication and caching of [BatcheableTransaction]
 * if no [TransactionBatcherCache] implementation is provided it will use [DefaultTransactionBatcherCache]
 */
class TransactionBatcher(
    private val cache: TransactionBatcherCache = DefaultTransactionBatcherCache()
) {

    private val queue = ConcurrentHashMap<
        Class<out TriggeredPublisher<Any, Any>>,
        TransactionBatcherQueueValue
        >()

    /**
     * enqueue a transaction [input] along with the [triggeredPublisher] instance that will receive the [BatcheableTransaction]
     * deduplication will be based on [key] which by default is the toString() representation of [input]
     * batching will be based on the implementation of [TriggeredPublisher]
     * this method returns a reference to a [CompletableFuture] which is a field of the [BatcheableTransaction] that was just
     * added into the queue
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

    /**
     * Trigger concurrently and asynchronously the instances of [TriggeredPublisher] that the [queue] holds
     * at the end clear the queue
     */
    fun dispatch() {
        queue.values.forEach { (triggeredPublisher, transactions) ->
            triggeredPublisher.trigger(transactions, cache)
        }
        queue.clear()
    }
}
