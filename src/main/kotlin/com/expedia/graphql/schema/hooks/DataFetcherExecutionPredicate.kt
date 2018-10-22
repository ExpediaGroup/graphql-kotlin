package com.expedia.graphql.schema.hooks

import com.expedia.graphql.schema.Parameter
import graphql.schema.DataFetchingEnvironment

/**
 * Perform runtime evaluations of each parameter passed to any KotlinDataFetcher.
 *
 * The DataFetcherExecutionPredicate is declared globally for all the datafetchers instances and all the parameters.
 * However a more precise logic (at the field level) is possible depending on the implement of `evaluate` and `test`
 *
 * The predicate logic is split into two parts (evaluate and test) so the result of the evaluation like a list of errors
 * can be passed to the onFailure method and added to an exception
 *
 * Because the DataFetcherExecutionPredicate is global, it's not possible to have methods where the type is inferred.
 *
 * It's recommended to check the type of the different arguments.
 */
abstract class DataFetcherExecutionPredicate {

    /**
     * Perform the predicate logic by evaluating the argument and its value
     * Then depending on the result either returning the value itself to continue the datafetcher invocation
     * or break the data fetching execution.
     *
     * @param parameter the function argument reference containing the KClass and the argument annotations
     * @param argumentName the name of the argument as declared in the query / kotlin function
     * @param environment the DataFetchingEnvironment in which the data fetcher is executed (gives access to field info, execution context etc)
     * @param value the value to execute the predicate against.
     */
    fun <T> execute(value: T, parameter: Parameter, argumentName: String, environment: DataFetchingEnvironment): T {
        val evaluationResult = evaluate(value, parameter, argumentName, environment)

        return if (test(evaluationResult)) {
            value
        } else {
            onFailure(evaluationResult, parameter, argumentName, environment)
        }
    }

    /**
     * Evaluate if the value passed respects some constraints.
     *
     * @param parameter the function argument reference containing the KClass and the argument annotations
     * @param argumentName the name of the argument as declared in the query / kotlin function
     * @param environment the DataFetchingEnvironment in which the data fetcher is executed (gives access to field info, execution context etc)
     * @param value the value to execute the predicate against.
     *
     * @return the result of the evaluation eg: List of errors
     */
    abstract fun <T> evaluate(value: T, parameter: Parameter, argumentName: String, environment: DataFetchingEnvironment): Any

    /**
     * Assert that the result of the {@link #evaluate(T, Parameter, String, DataFetchingEnvironment)} method is as expected eg: the list of errors is empty
     *
     * @param evaluationResult the result of the evaluation
     *
     * @return whether the parameter passes the predicate
     */
    abstract fun test(evaluationResult: Any): Boolean

    /**
     * If the test is unsuccessful, this function will be invoked.
     *
     * An exception can then be thrown to block the data fetcher execution
     *
     * @param evaluationResult the object return by the `evaluate` function
     * @param parameter the function argument reference containing the KClass and the argument annotations
     * @param argumentName the name of the argument as declared in the query / kotlin function
     * @param environment the DataFetchingEnvironment in which the data fetcher is executed (gives access to field info, execution context etc)
     */
    abstract fun onFailure(evaluationResult: Any, parameter: Parameter, argumentName: String, environment: DataFetchingEnvironment): Nothing
}
