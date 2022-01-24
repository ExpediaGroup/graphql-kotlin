package com.expediagroup.graphql.transactionbatcher.publisher

import com.expediagroup.graphql.transactionbatcher.transaction.BatcheableTransaction
import com.expediagroup.graphql.transactionbatcher.transaction.TransactionBatcherCacheRepository
import org.reactivestreams.Publisher
import org.reactivestreams.Subscriber
import org.reactivestreams.Subscription

@Suppress(
    "ReactiveStreamsSubscriberImplementation",
    "UNCHECKED_CAST"
)
abstract class TriggeredPublisher<TInput, TOutput> {
    abstract fun produce(input: List<TInput>): Publisher<TOutput>

    /**
     * attempt to collect values from cache resolving say [1, null, 3, null, 5, 6]
     * so we will only need to produce values for index 1 and 3
     * when onComplete complete futures from either values from cache or from produce
     * order is important
     */
    fun trigger(
        batcheableTransactions: List<BatcheableTransaction<TInput, TOutput>>,
        cacheRepository: TransactionBatcherCacheRepository
    ) {

        val values = batcheableTransactions.map { batcheableTransaction ->
            cacheRepository.get(batcheableTransaction.key)
        }

        val transactionsNotInCache = values.mapIndexedNotNull { index, value ->
            when (value) {
                null -> batcheableTransactions.getOrNull(index)
                else -> null
            }
        }

        produce(
            transactionsNotInCache.map(BatcheableTransaction<TInput, TOutput>::input)
        ).subscribe(
            object : Subscriber<TOutput> {
                private lateinit var subscription: Subscription
                private val results = mutableListOf<TOutput>()

                override fun onSubscribe(subscription: Subscription) {
                    this.subscription = subscription
                    this.subscription.request(1)
                }

                override fun onNext(result: TOutput) {
                    results += result
                    this.subscription.request(1)
                }

                override fun onError(throwable: Throwable) {
                    throwable.printStackTrace()
                }

                override fun onComplete() {
                    var resultsCounter = 0
                    values.forEachIndexed { index, value ->
                        value?.let {
                            batcheableTransactions[index].future.complete(value as TOutput)
                        } ?: run {
                            val result = results[resultsCounter++]
                            cacheRepository.set(batcheableTransactions[index].key, result as Any)
                            batcheableTransactions[index].future.complete(result)
                        }
                    }
                }
            }
        )
    }
}
