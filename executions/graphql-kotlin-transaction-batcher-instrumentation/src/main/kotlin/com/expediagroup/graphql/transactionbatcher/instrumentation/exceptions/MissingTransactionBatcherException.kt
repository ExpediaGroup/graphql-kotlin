package com.expediagroup.graphql.transactionbatcher.instrumentation.exceptions

import com.expediagroup.graphql.transactionbatcher.transaction.TransactionBatcher

/**
 * Thrown when an instance of [TransactionBatcher] does not exists in the GraphQLContext
 */
class MissingTransactionBatcherException() :
    RuntimeException("TransactionBatcher instance not found in the GraphQLContext")
