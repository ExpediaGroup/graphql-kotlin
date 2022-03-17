package com.expediagroup.graphql.transactionbatcher.instrumentation

/**
 * Interface that allows to wrap any loader implementation that would be used by the [TransactionLoaderLevelInstrumentation]
 */
interface TransactionLoader<T> {
    /**
     * loader implementation
     */
    val loader: T
    /**
     * method that will dispatch transactions stored in the loader implementation
     */
    fun load()
}
