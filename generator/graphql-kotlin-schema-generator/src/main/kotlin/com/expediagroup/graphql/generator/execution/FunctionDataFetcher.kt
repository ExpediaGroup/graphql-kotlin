/*
 * Copyright 2020 Expedia, Inc
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

package com.expediagroup.graphql.generator.execution

import com.expediagroup.graphql.generator.internal.extensions.getJavaClass
import com.expediagroup.graphql.generator.internal.extensions.getName
import com.expediagroup.graphql.generator.internal.extensions.getTypeOfFirstArgument
import com.expediagroup.graphql.generator.internal.extensions.getWrappedType
import com.expediagroup.graphql.generator.internal.extensions.isArray
import com.expediagroup.graphql.generator.internal.extensions.isDataFetchingEnvironment
import com.expediagroup.graphql.generator.internal.extensions.isGraphQLContext
import com.expediagroup.graphql.generator.internal.extensions.isList
import com.expediagroup.graphql.generator.internal.extensions.isOptionalInputType
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import graphql.schema.DataFetcher
import graphql.schema.DataFetchingEnvironment
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.future.future
import org.slf4j.LoggerFactory
import java.lang.reflect.InvocationTargetException
import java.util.concurrent.CompletableFuture
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.KType
import kotlin.reflect.full.callSuspendBy
import kotlin.reflect.full.instanceParameter
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
    private val objectMapper: ObjectMapper = jacksonObjectMapper(),
    private val defaultCoroutineContext: CoroutineContext = EmptyCoroutineContext
) : DataFetcher<Any?> {
    private val logger = LoggerFactory.getLogger(FunctionDataFetcher::class.java)

    /**
     * Invoke a suspend function or blocking function, passing in the [target] if not null or default to using the source from the environment.
     */
    override fun get(environment: DataFetchingEnvironment): Any? {
        val instance: Any? = target ?: environment.getSource<Any?>()
        val instanceParameter = fn.instanceParameter

        return if (instance != null && instanceParameter != null) {
            val parameterValues = getParameters(fn, environment)
                .plus(instanceParameter to instance)

            if (fn.isSuspend) {
                runSuspendingFunction(parameterValues)
            } else {
                runBlockingFunction(parameterValues)
            }
        } else {
            null
        }
    }

    /**
     * Iterate over all the input values from the environment and the [KParameter]s for the function and, using the environment,
     * map them all to a value to pass to the function. If there is a missing environment argument, that means the client did not pass
     * in a value for this optional paramter. This allows for the default Kotlin values to be used if no mapping is added.
     * You can override this behaviour by providing a value for a specific [KParameter] when [mapParameterToValue] is called.
     */
    protected open fun getParameters(fn: KFunction<*>, environment: DataFetchingEnvironment): Map<KParameter, Any?> {
        return fn.valueParameters
            .mapNotNull { mapParameterToValue(it, environment) }
            .toMap()
    }

    /**
     * Retreives the provided parameter value in the operation input to pass to the function to execute.
     *
     * If the arugment is missing in the input, and the type is not an [OptionalInput], do not return a mapping.
     * This allows for default Kotlin values to be used when executing the function. Otherwise if you need logic when a value
     * is missing, use the [OptionalInput] wrapper instead.
     *
     * If the parameter is of a special type then we do not read the input and instead just pass on that value.
     * The special values include:
     *   - If the parameter is marked as a [com.expediagroup.graphql.generator.execution.GraphQLContext], then return the environment context
     *   - The entire environment is returned if the parameter is of type [DataFetchingEnvironment]
     */
    protected open fun mapParameterToValue(param: KParameter, environment: DataFetchingEnvironment): Pair<KParameter, Any?>? =
        when {
            param.isGraphQLContext() -> {
                logger.warn("GraphQLContext interface injection is deprecated. Please use DataFetchingEnvironment to retrieve ${param.getName()}.")
                param to environment.getContext()
            }
            param.isDataFetchingEnvironment() -> param to environment
            else -> {
                val name = param.getName()
                if (environment.containsArgument(name) || param.type.isOptionalInputType()) {
                    val value: Any? = environment.arguments[name]
                    param to convertArgumentToObject(param, environment, name, value)
                } else {
                    null
                }
            }
        }

    /**
     * Convert the generic arument value from JSON input to the paramter class.
     * This is currently achieved by using a Jackson ObjectMapper.
     */
    private fun convertArgumentToObject(
        param: KParameter,
        environment: DataFetchingEnvironment,
        argumentName: String,
        argumentValue: Any?
    ): Any? = when {
        param.type.isOptionalInputType() -> {
            when {
                !environment.containsArgument(argumentName) -> OptionalInput.Undefined
                argumentValue == null -> OptionalInput.Defined(null)
                else -> {
                    val paramType = param.type.getTypeOfFirstArgument()
                    val value = convertValue(paramType, argumentValue)
                    OptionalInput.Defined(value)
                }
            }
        }
        else -> convertValue(param.type, argumentValue)
    }

    private fun convertValue(
        paramType: KType,
        argumentValue: Any?
    ): Any? = when {
        paramType.isList() -> {
            val argumentClass = paramType.getTypeOfFirstArgument().getJavaClass()
            val jacksonCollectionType = objectMapper.typeFactory.constructCollectionType(List::class.java, argumentClass)
            objectMapper.convertValue(argumentValue, jacksonCollectionType)
        }
        paramType.isArray() -> {
            val argumentClass = paramType.getWrappedType().getJavaClass()
            val jacksonCollectionType = objectMapper.typeFactory.constructArrayType(argumentClass)
            objectMapper.convertValue(argumentValue, jacksonCollectionType)
        }
        else -> {
            val javaClass = paramType.getJavaClass()
            objectMapper.convertValue(argumentValue, javaClass)
        }
    }

    /**
     * Once all parameters values are properly converted, this function will be called to run a suspendable function.
     * If you need to override the exception handling you can override the entire method.
     * You can also call it from [get] with different values to override the default coroutine context or start parameter.
     */
    protected open fun runSuspendingFunction(
        parameterValues: Map<KParameter, Any?>,
        coroutineContext: CoroutineContext = defaultCoroutineContext,
        coroutineStart: CoroutineStart = CoroutineStart.DEFAULT
    ): CompletableFuture<Any?> = GlobalScope.future(context = coroutineContext, start = coroutineStart) {
        try {
            fn.callSuspendBy(parameterValues)
        } catch (exception: InvocationTargetException) {
            throw exception.cause ?: exception
        }
    }

    /**
     * Once all parameters values are properly converted, this function will be called to run a simple blocking function.
     * If you need to override the exception handling you can override this method.
     */
    protected open fun runBlockingFunction(parameterValues: Map<KParameter, Any?>): Any? {
        try {
            return fn.callBy(parameterValues)
        } catch (exception: InvocationTargetException) {
            throw exception.cause ?: exception
        }
    }
}
