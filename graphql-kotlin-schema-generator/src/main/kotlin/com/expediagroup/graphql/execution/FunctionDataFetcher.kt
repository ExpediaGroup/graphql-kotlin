/*
 * Copyright 2019 Expedia, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.expediagroup.graphql.execution

import com.expediagroup.graphql.generator.extensions.getName
import com.expediagroup.graphql.generator.extensions.isDataFetchingEnvironment
import com.expediagroup.graphql.generator.extensions.isGraphQLContext
import com.expediagroup.graphql.generator.extensions.javaTypeClass
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
