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
import com.expediagroup.graphql.transactionbatcher.transaction.cache.DefaultTransactionBatcherCache
import com.expediagroup.graphql.transactionbatcher.transaction.cache.TransactionBatcherCache
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass

/**
 * Holds logic to apply batching, deduplication and caching of [BatchableTransaction]
 * if no [TransactionBatcherCache] implementation is provided it will use [DefaultTransactionBatcherCache]
 */
class TransactionBatcher(
    private val cache: TransactionBatcherCache = DefaultTransactionBatcherCache()
) {

    val batch = ConcurrentHashMap<
        KClass<out TriggeredPublisher<Any, Any>>,
        TriggeredPublisherTransactions
        >()

    /**
     * adds a transaction [input] to the batch along with the [triggeredPublisher] instance that will receive the [BatchableTransaction]
     * deduplication will be based on [transactionKey] which by default is the toString() representation of [input]
     * batching will be based on the implementation of [TriggeredPublisher]
     * this method returns a reference to a [CompletableFuture] which is a field of the [BatchableTransaction] that was just
     * added into the queue
     */
    @Suppress("UNCHECKED_CAST")
    fun <TInput : Any, TOutput : Any> batch(
        input: TInput,
        transactionKey: String = input.toString(),
        triggeredPublisher: TriggeredPublisher<TInput, TOutput>
    ): CompletableFuture<TOutput> {
        val publisherClass = (triggeredPublisher as TriggeredPublisher<Any, Any>)::class
        var future = CompletableFuture<TOutput>()
        batch.computeIfPresent(publisherClass) { _, publisherTransactions ->
            publisherTransactions.transactions.computeIfAbsent(transactionKey) {
                BatchableTransaction(
                    input,
                    future as CompletableFuture<Any>,
                    transactionKey
                )
            }.also { transaction ->
                future = transaction.future as CompletableFuture<TOutput>
            }
            publisherTransactions
        }
        batch.computeIfAbsent(publisherClass) {
            TriggeredPublisherTransactions(
                triggeredPublisher,
                linkedMapOf(
                    transactionKey to BatchableTransaction(
                        input,
                        future as CompletableFuture<Any>,
                        transactionKey
                    )
                )
            )
        }
        return future
    }

    /**
     * Trigger concurrently and asynchronously the instances of [TriggeredPublisher] that the [batch] holds
     * at the end clear the queue
     */
    @Synchronized fun dispatch() {
        batch.values.forEach { (triggeredPublisher, transactions) ->
            triggeredPublisher.trigger(transactions.values.toList(), cache)
        }
        batch.clear()
    }
}
