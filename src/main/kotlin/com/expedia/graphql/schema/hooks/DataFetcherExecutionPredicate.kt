package com.expedia.graphql.schema.hooks

interface DataFetcherExecutionPredicate {

    /**
     * The test to perform on the argument.
     *
     * With this test, runtime evaluation can be performed such as validation
     */
    fun <T> test(value: T): Boolean

    /**
     * If the test is unsuccessful, this function will be invoked.
     *
     * An exception can then be thrown to block the data fetcher execution
     */
    fun onFailure(name: String, klazz: Class<*>): Nothing
}
