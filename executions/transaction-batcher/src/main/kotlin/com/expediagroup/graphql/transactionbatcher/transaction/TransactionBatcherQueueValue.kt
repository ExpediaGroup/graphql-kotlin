package com.expediagroup.graphql.transactionbatcher.transaction

import com.expediagroup.graphql.transactionbatcher.publisher.TriggeredPublisher

internal data class TransactionBatcherQueueValue(
    val triggeredPublisher: TriggeredPublisher<Any, Any>,
    val transactions: MutableList<BatcheableTransaction<Any, Any>>
)
