package com.expediagroup.graphql.transactionbatcher.instrumentation

/**
 * Interface that allows to wrap any loader implementation.
 */
interface TransactionLoader<T> {
    /**
     * loader implementation.
     */
    val loader: T
    /**
     * method that will dispatch transactions stored in the loader implementation.
     */
    fun dispatch()
    /**
     * method that signals if some transactions are still being completed.
     */
    fun isDispatchCompleted(): Boolean
}
