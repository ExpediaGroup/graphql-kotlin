package com.expedia.graphql.execution

import com.expedia.graphql.generator.extensions.getName
import com.expedia.graphql.generator.extensions.isDataFetchingEnvironment
import com.expedia.graphql.generator.extensions.isGraphQLContext
import com.expedia.graphql.generator.extensions.javaTypeClass
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import graphql.schema.DataFetcher
import graphql.schema.DataFetchingEnvironment
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.future.asCompletableFuture
import java.lang.reflect.InvocationTargetException
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.full.callSuspend
import kotlin.reflect.full.valueParameters
import java.util.concurrent.ExecutionException as ExecutionException1

/**
 * Simple DataFetcher that invokes target function on the given object.
 *
 * @param target The target object that performs the data fetching, if not specified then this data fetcher will attempt
 *   to use source object from the environment
 * @param fn The Kotlin function being invoked
 * @param objectMapper Jackson ObjectMapper that will be used to deserialize environment arguments to the expected function arguments
 * @param executionPredicate Predicate to run to map the value to a new result
 */
class FunctionDataFetcher(
    private val target: Any?,
    private val fn: KFunction<*>,
    private val objectMapper: ObjectMapper = jacksonObjectMapper(),
    private val executionPredicate: DataFetcherExecutionPredicate? = null
) : DataFetcher<Any> {

    @Suppress("Detekt.SpreadOperator")
    override fun get(environment: DataFetchingEnvironment): Any? {
        val instance = target ?: environment.getSource<Any>()

        return instance?.let {
            val parameterValues = fn.valueParameters
                .map { param -> mapParameterToValue(param, environment) }
                .toTypedArray()

            if (fn.isSuspend) {
                GlobalScope.async {
                    fn.callSuspend(it, *parameterValues)
                }.asCompletableFuture()
            } else {
                try {
                    return fn.call(it, *parameterValues)
                } catch (exception: InvocationTargetException) {
                    throw exception.cause ?: exception
                }
            }
        }
    }

    private fun mapParameterToValue(param: KParameter, environment: DataFetchingEnvironment): Any? =
        when {
            param.isGraphQLContext() -> environment.getContext()
            param.isDataFetchingEnvironment() -> environment
            else -> convertParameterValue(param, environment)
        }

    private fun convertParameterValue(param: KParameter, environment: DataFetchingEnvironment): Any? {
        val name = param.getName()
        val klazz = param.javaTypeClass()
        val value = objectMapper.convertValue(environment.arguments[name], klazz)
        val predicateResult = executionPredicate?.evaluate(value = value, parameter = param, environment = environment)

        return predicateResult ?: value
    }
}
