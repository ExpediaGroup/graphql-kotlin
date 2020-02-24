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
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.future.future
import java.lang.reflect.InvocationTargetException
import java.util.concurrent.CompletableFuture
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
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
 */
@Suppress("Detekt.SpreadOperator")
open class FunctionDataFetcher(
    private val target: Any?,
    private val fn: KFunction<*>,
    private val objectMapper: ObjectMapper = jacksonObjectMapper()
) : DataFetcher<Any?> {

    /**
     * Invoke a suspend function or blocking function, passing in the [target] if not null or default to using the source from the environment.
     */
    override fun get(environment: DataFetchingEnvironment): Any? {
        val instance = target ?: environment.getSource<Any?>()

        return instance?.let {
            val parameterValues = getParameterValues(fn, environment)

            if (fn.isSuspend) {
                runSuspendingFunction(it, parameterValues)
            } else {
                runBlockingFunction(it, parameterValues)
            }
        }
    }

    /**
     * Iterate over all the function parameters and map them to the proper input values from the environment
     */
    protected open fun getParameterValues(fn: KFunction<*>, environment: DataFetchingEnvironment): Array<Any?> = fn.valueParameters
        .map { param -> mapParameterToValue(param, environment) }
        .toTypedArray()

    /**
     * Retreives the provided parameter value in the operation input to pass to the function to execute.
     * If the parameter is of a special type then we do not read the input and instead just pass on that value.
     *
     * The special values include:
     *   - If the parameter is annotated with [com.expediagroup.graphql.annotations.GraphQLContext],
     *     then return the environment context
     *
     *   - The entire environment is returned if the parameter is of type [DataFetchingEnvironment]
     */
    protected open fun mapParameterToValue(param: KParameter, environment: DataFetchingEnvironment): Any? =
        when {
            param.isGraphQLContext() -> environment.getContext()
            param.isDataFetchingEnvironment() -> environment
            else -> convertParameterValue(param, environment)
        }

    /**
     * Called to convert the generic input object to the parameter class.
     *
     * This is currently achieved by using a Jackson [ObjectMapper].
     */
    protected open fun convertParameterValue(param: KParameter, environment: DataFetchingEnvironment): Any? {
        val name = param.getName()
        val klazz = param.javaTypeClass()
        val argument = environment.arguments[name]

        return objectMapper.convertValue(argument, klazz)
    }

    /**
     * Once all parameters values are properly converted, this function will be called to run a suspendable function.
     * If you need to override the exception handling you can override the entire method.
     * You can also call it from [get] with different values to override the default corotuine context or start parameter.
     */
    protected open fun runSuspendingFunction(
        instance: Any,
        parameterValues: Array<Any?>,
        coroutineContext: CoroutineContext = EmptyCoroutineContext,
        coroutineStart: CoroutineStart = CoroutineStart.DEFAULT
    ): CompletableFuture<Any?> = GlobalScope.future(context = coroutineContext, start = coroutineStart) {
        try {
            fn.callSuspend(instance, *parameterValues)
        } catch (exception: InvocationTargetException) {
            throw exception.cause ?: exception
        }
    }

    /**
     * Once all parameters values are properly converted, this function will be called to run a simple blocking function.
     * If you need to override the exception handling you can override this method.
     */
    protected open fun runBlockingFunction(instance: Any, parameterValues: Array<Any?>): Any? {
        try {
            return fn.call(instance, *parameterValues)
        } catch (exception: InvocationTargetException) {
            throw exception.cause ?: exception
        }
    }
}
