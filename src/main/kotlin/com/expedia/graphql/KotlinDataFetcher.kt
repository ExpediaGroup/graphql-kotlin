package com.expedia.graphql

import com.expedia.graphql.generator.extensions.getName
import com.expedia.graphql.generator.extensions.isGraphQLContext
import com.expedia.graphql.generator.extensions.javaTypeClass
import com.expedia.graphql.hooks.DataFetcherExecutionPredicate
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import graphql.schema.DataFetcher
import graphql.schema.DataFetchingEnvironment
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.full.valueParameters

/**
 * Simple DataFetcher that invokes function on the target object.
 *
 * @param target The target object that performs the data fetching
 * @param fn The Kotlin function being invoked
 * @param executionPredicate Predicate to run to map the value to a new result
 */
class KotlinDataFetcher(
    private val target: Any?,
    private val fn: KFunction<*>,
    private val executionPredicate: DataFetcherExecutionPredicate?
) : DataFetcher<Any> {

    override fun get(environment: DataFetchingEnvironment): Any? {
        val instance = target ?: environment.getSource<Any>()

        return instance?.let {
            val parameterValues = fn.valueParameters.map { param -> mapParameterToValue(param, environment) }.toTypedArray()

            @Suppress("Detekt.SpreadOperator")
            fn.call(it, *parameterValues)
        }
    }

    private fun mapParameterToValue(param: KParameter, environment: DataFetchingEnvironment): Any? =
        if (param.isGraphQLContext()) {
            environment.getContext()
        } else {
            val name = param.getName()
            val klazz = param.type.javaTypeClass
            val value = mapper.convertValue(environment.arguments[name], klazz)
            val predicateResult = executionPredicate?.execute(value = value, parameter = param, environment = environment)

            predicateResult ?: value
        }

    private companion object {
        private val mapper = jacksonObjectMapper()
    }
}
