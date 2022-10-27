/*
 * Copyright 2022 Expedia, Inc
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

import com.expediagroup.graphql.generator.extensions.getOrDefault
import com.expediagroup.graphql.generator.internal.extensions.getName
import com.expediagroup.graphql.generator.internal.extensions.isDataFetchingEnvironment
import com.expediagroup.graphql.generator.internal.extensions.isOptionalInputType
import graphql.schema.DataFetcher
import graphql.schema.DataFetchingEnvironment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.future.future
import java.lang.reflect.InvocationTargetException
import java.util.concurrent.CompletableFuture
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.full.callSuspendBy
import kotlin.reflect.full.instanceParameter
import kotlin.reflect.full.valueParameters

/**
 * Simple DataFetcher that invokes target function on the given object.
 *
 * @param target The target object that performs the data fetching, if not specified then this data fetcher will attempt
 *   to use source object from the environment
 * @param fn The Kotlin function being invoked
 */
@Suppress("Detekt.SpreadOperator")
open class FunctionDataFetcher(
    private val target: Any?,
    private val fn: KFunction<*>,
) : DataFetcher<Any?> {

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
                runSuspendingFunction(environment, parameterValues)
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
     * in a value for this optional parameter. This allows for the default Kotlin values to be used if no mapping is added.
     * You can override this behaviour by providing a value for a specific [KParameter] when [mapParameterToValue] is called.
     */
    protected open fun getParameters(fn: KFunction<*>, environment: DataFetchingEnvironment): Map<KParameter, Any?> =
        fn.valueParameters
            .mapNotNull { mapParameterToValue(it, environment) }
            .toMap()

    /**
     * Retrieves the provided parameter value in the operation input to pass to the function to execute.
     *
     * If the argument is missing in the input, and the type is not an [OptionalInput], do not return a mapping.
     * This allows for default Kotlin values to be used when executing the function. If you need logic when a value
     * is missing, use the [OptionalInput] wrapper instead.
     *
     * If the parameter is of a special type then we do not read the input and instead just pass on that value.
     * The special values include:
     *   - The entire environment is returned if the parameter is of type [DataFetchingEnvironment]
     */
    protected open fun mapParameterToValue(param: KParameter, environment: DataFetchingEnvironment): Pair<KParameter, Any?>? =
        when {
            param.isDataFetchingEnvironment() -> param to environment
            else -> {
                val name = param.getName()
                if (environment.containsArgument(name) || param.type.isOptionalInputType()) {
                    param to convertArgumentValue(name, param, environment.arguments)
                } else {
                    null
                }
            }
        }

    /**
     * Once all parameters values are properly converted, this function will be called to run a suspendable function using
     * a scope provided in the GraphQLContext map or default to a new CoroutineScope with EmptyCoroutineContext.
     * If you need to override the exception handling you can override the entire method.
     */
    protected open fun runSuspendingFunction(
        environment: DataFetchingEnvironment,
        parameterValues: Map<KParameter, Any?>
    ): CompletableFuture<Any?> =
        environment.graphQlContext.getOrDefault(CoroutineScope(EmptyCoroutineContext)).future {
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
    protected open fun runBlockingFunction(parameterValues: Map<KParameter, Any?>): Any? = try {
        fn.callBy(parameterValues)
    } catch (exception: InvocationTargetException) {
        throw exception.cause ?: exception
    }
}
