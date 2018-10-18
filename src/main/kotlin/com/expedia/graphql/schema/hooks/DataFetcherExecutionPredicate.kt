package com.expedia.graphql.schema.hooks

import com.expedia.graphql.schema.Parameter

/**
 * Perform runtime evaluations of each parameter passed to any datafetcher.
 *
 * The DataFetcherExecutionPredicate is declared globally for all the datafetchers instances and all the parameters.
 * However a more precise logic (at the field level) is possible depending on the implement of `evaluate` and `test`
 *
 * The predicate logic is split into two parts (evaluate and test) so the result of the evaluation like a list of errors
 * can be passed to the onFailure method and added to an exception
 *
 * Because the DataFetcherExecutionPredicate, it's not possible to have type inferred methods.
 *
 * It's recommended to check the type of the different arguments.
 */
abstract class DataFetcherExecutionPredicate {

    /**
     * Perform the predicate logic by evaluating the argument and its value
     * Then depending on the result either returning the value itself to continue the datafetcher invocation
     * or call break the data fetching execution.
     *
     * @param parameter the function argument reference
     * @param argumentName the name of the argument as declared in the query / kotlin function
     * @param value the value passed in the query by the user
     */
    fun execute(parameter: Parameter, argumentName: String, value: Any): Any {
        val result = evaluate(parameter, argumentName, value)

        return when {
            test(result) -> value
            else -> onFailure(parameter, argumentName, result)
        }
    }

    /**
     * Evaluate if the given data fetcher parameter.
     *
     * @param parameter the parameter under evaluation
     *
     * @return the result of the evaluation eg: List of errors
     */
    abstract fun <T> evaluate(parameter: Parameter, argumentName: String, value: T): Any

    /**
     * Assert than the result of the {@link #evaluate(Parameter)} method is as expected eg: the list of errors is empty
     *
     * @param result the result of the evaluation
     *
     * @return whether the parameter passed the predicate
     */
    abstract fun test(result: Any): Boolean

    /**
     * If the test is unsuccessful, this function will be invoked.
     *
     * An exception can then be thrown to block the data fetcher execution
     */
    abstract fun onFailure(parameter: Parameter, argumentName: String, result: Any): Nothing
}
