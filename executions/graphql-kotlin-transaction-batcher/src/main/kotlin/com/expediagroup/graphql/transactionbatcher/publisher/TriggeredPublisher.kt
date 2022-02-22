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

package com.expediagroup.graphql.transactionbatcher.publisher

import com.expediagroup.graphql.transactionbatcher.transaction.BatchableTransaction
import com.expediagroup.graphql.transactionbatcher.transaction.cache.TransactionBatcherCache
import org.reactivestreams.Publisher
import org.reactivestreams.Subscriber
import org.reactivestreams.Subscription
import org.slf4j.LoggerFactory

/**
 * Interface representing a publisher with input [TInput] type and output [TOutput] type
 */
@Suppress(
    "ReactiveStreamsSubscriberImplementation",
    "UNCHECKED_CAST"
)
fun interface TriggeredPublisher<TInput, TOutput> {
    /**
     * Given an input of type [TInput] create a cold [Publisher] that will produce a [Publisher] of type [TOutput] of n elements
     * that maps to the size of the input [List] of [TInput]
     * order is important so make sure to produce elements in the same order of [input]
     */
    fun produce(input: List<TInput>): Publisher<TOutput>

    /**
     * Attempts to collect values from [cache] first and then [produce]
     *
     * Example:
     * if [TriggeredPublisher] is of type <Int, Int> and [cache] resolves [1, null, 3, null, 5, 6]
     * we will attempt to produce elements for index 1 and 3
     * when [produce] stream completes we will complete futures from either values resolved from [cache] or from [produce]
     */
    fun trigger(
        batchableTransactions: List<BatchableTransaction<TInput, TOutput>>,
        cache: TransactionBatcherCache
    ) {
        val transactionsNotInCache = batchableTransactions
            .filterNot { transaction -> cache.contains(transaction.key) }
            .map(BatchableTransaction<TInput, TOutput>::input)

        produce(
            transactionsNotInCache
        ).subscribe(
            object : Subscriber<TOutput> {
                private lateinit var subscription: Subscription
                private val transactionResults = mutableListOf<TOutput>()

                override fun onSubscribe(subscription: Subscription) {
                    this.subscription = subscription
                    this.subscription.request(1)
                }

                override fun onNext(result: TOutput) {
                    transactionResults += result
                    this.subscription.request(1)
                }

                override fun onError(throwable: Throwable) {
                    logger.error("Error while producing data", throwable)
                }

                override fun onComplete() {
                    val resultsIterator = transactionResults.iterator()
                    batchableTransactions.forEach { transaction ->
                        val result = cache.get(transaction.key) as? TOutput ?: resultsIterator.next().also { result ->
                            cache.set(transaction.key, result as Any)
                        }
                        transaction.future.complete(result)
                    }
                }
            }
        )
    }

    companion object {
        private val logger = LoggerFactory.getLogger(TriggeredPublisher::class.java)
    }
}
