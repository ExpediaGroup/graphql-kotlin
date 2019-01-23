package com.expedia.graphql.execution

import com.expedia.graphql.generator.extensions.getName
import com.expedia.graphql.generator.extensions.isGraphQLContext
import com.expedia.graphql.generator.extensions.javaTypeClass
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import graphql.schema.DataFetcher
import graphql.schema.DataFetchingEnvironment
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.future.asCompletableFuture
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.full.callSuspend
import kotlin.reflect.full.valueParameters

/**
 * Simple DataFetcher that invokes function on the target object.
 *
 * @param target The target object that performs the data fetching
 * @param fn The Kotlin function being invoked
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
            val parameterValues = fn.valueParameters.map { param -> mapParameterToValue(param, environment) }.toTypedArray()

            if (fn.isSuspend) {
                GlobalScope.async {
                    fn.callSuspend(it, *parameterValues)
                }.asCompletableFuture()
            } else {
                fn.call(it, *parameterValues)
            }
        }
    }

    private fun mapParameterToValue(param: KParameter, environment: DataFetchingEnvironment): Any? =
        if (param.isGraphQLContext()) {
            environment.getContext()
        } else {
            val name = param.getName()
            val klazz = param.type.javaTypeClass
            val value = objectMapper.convertValue(environment.arguments[name], klazz)
            val predicateResult = executionPredicate?.evaluate(value = value, parameter = param, environment = environment)

            predicateResult ?: value
        }
}
