package com.expedia.graphql.execution

import graphql.schema.DataFetchingEnvironment
import kotlin.reflect.KParameter

/**
 * Perform runtime evaluations of each parameter passed to any FunctionDataFetcher.
 *
 * The DataFetcherExecutionPredicate is declared globally for all the datafetchers instances and all the parameters.
 * However a more precise logic (at the field level) is possible depending on the implement of `evaluate`
 *
 * Because the DataFetcherExecutionPredicate is global, it's not possible to have methods where the type is inferred.
 *
 * It's recommended to check the type of the different arguments.
 */
interface DataFetcherExecutionPredicate {

    /**
     * Perform the predicate logic by evaluating the argument and its value.
     * Then depending on the result either:
     *   - Return the value itself to continue the datafetcher invocation
     *   - Throw an exception
     *
     * @param value the value to evaluate the predicate against
     * @param parameter the function argument reference containing the KClass and the argument annotations
     * @param environment the DataFetchingEnvironment in which the data fetcher is executed (gives access to field info, execution context etc)
     */
    fun <T> evaluate(value: T, parameter: KParameter, environment: DataFetchingEnvironment): T
}
