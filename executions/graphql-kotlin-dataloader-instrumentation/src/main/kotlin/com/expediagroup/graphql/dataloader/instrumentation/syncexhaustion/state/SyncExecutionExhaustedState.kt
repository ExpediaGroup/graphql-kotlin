/*
 * Copyright 2024 Expedia, Inc
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

package com.expediagroup.graphql.dataloader.instrumentation.syncexhaustion.state

import com.expediagroup.graphql.dataloader.KotlinDataLoaderRegistry
import com.expediagroup.graphql.dataloader.instrumentation.syncexhaustion.execution.OnSyncExecutionExhaustedCallback
import graphql.ExecutionInput
import graphql.ExecutionResult
import graphql.GraphQLContext
import graphql.execution.ExecutionId
import graphql.execution.MergedField
import graphql.execution.instrumentation.ExecutionStrategyInstrumentationContext
import graphql.execution.instrumentation.FieldFetchingInstrumentationContext
import graphql.execution.instrumentation.InstrumentationContext
import graphql.execution.instrumentation.SimpleInstrumentationContext
import graphql.execution.instrumentation.parameters.InstrumentationExecutionParameters
import graphql.execution.instrumentation.parameters.InstrumentationExecutionStrategyParameters
import graphql.execution.instrumentation.parameters.InstrumentationFieldFetchParameters
import graphql.schema.DataFetcher
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

/**
 * Orchestrate the [ExecutionBatchState] of all [ExecutionInput] sharing the same [GraphQLContext],
 * when a certain state is reached will invoke [OnSyncExecutionExhaustedCallback]
 */
class SyncExecutionExhaustedState(
    totalOperations: Int,
    private val dataLoaderRegistry: KotlinDataLoaderRegistry
) {
    private val totalExecutions: AtomicInteger = AtomicInteger(totalOperations)
    val executions = ConcurrentHashMap<ExecutionId, ExecutionBatchState>()

    /**
     * Create the [ExecutionBatchState] When a specific [ExecutionInput] starts his execution
     *
     * @param parameters contains information of which [ExecutionInput] will start his execution
     * @return a non null [InstrumentationContext] object
     */
    fun beginExecution(
        parameters: InstrumentationExecutionParameters,
        onSyncExecutionExhausted: OnSyncExecutionExhaustedCallback
    ): InstrumentationContext<ExecutionResult> {
        executions.computeIfAbsent(parameters.executionInput.executionId) {
            ExecutionBatchState()
        }
        return object : SimpleInstrumentationContext<ExecutionResult>() {
            /**
             * Remove an [ExecutionBatchState] from the state in case operation does not qualify for starting or completing execution,
             * for example:
             * - parsing, validation errors
             * - persisted query errors
             * - an exception during execution was thrown
             */
            override fun onCompleted(result: ExecutionResult?, t: Throwable?) {
                if ((result != null && result.errors.size > 0) || t != null) {
                    if (executions.containsKey(parameters.executionInput.executionId)) {
                        synchronized(executions) {
                            executions.remove(parameters.executionInput.executionId)
                            totalExecutions.set(totalExecutions.get() - 1)
                        }
                        ifAllSyncExecutionsExhausted { executionIds ->
                            onSyncExecutionExhausted(executionIds)
                        }
                    }
                }
            }
        }
    }

    /**
     * Add [ExecutionStrategyState] into operation [ExecutionBatchState] for the object that
     * just started an ExecutionStrategy
     *
     * @param parameters contains information of which [ExecutionInput] started an executionStrategy
     * @return a noop [ExecutionStrategyInstrumentationContext]
     */
    fun beginRecursiveExecution(
        parameters: InstrumentationExecutionStrategyParameters
    ) {
        val executionId = parameters.executionContext.executionInput.executionId
        executions.computeIfPresent(executionId) { _, executionState ->
            val executionStrategyParameters = parameters.executionStrategyParameters

            val field = executionStrategyParameters.field?.singleField
            val path = executionStrategyParameters.path
            val selectionFields = executionStrategyParameters.fields.subFieldsList.map(MergedField::getSingleField)
            val parentGraphQLType = executionStrategyParameters.executionStepInfo.parent?.unwrappedNonNullType

            executionState.addExecutionStrategyState(field, path, selectionFields, parentGraphQLType)
            executionState
        }
    }

    /**
     * This is called just before a field [DataFetcher] is invoked
     *
     * @param parameters contains information of which field will start the fetching
     * @param onSyncExecutionExhausted invoke when the sync execution fo all operations is exhausted
     * @return a [InstrumentationContext] object that will be called back when the [DataFetcher]
     * dispatches and completes
     */
    fun beginFieldFetching(
        parameters: InstrumentationFieldFetchParameters,
        onSyncExecutionExhausted: OnSyncExecutionExhaustedCallback
    ): FieldFetchingInstrumentationContext {
        val executionId = parameters.executionContext.executionInput.executionId
        val field = parameters.executionStepInfo.field.singleField
        val fieldExecutionStrategyPath = parameters.executionStepInfo.path.parent
        val fieldName = field.name
        val path = fieldExecutionStrategyPath.toString()
        val fieldGraphQLType = parameters.executionStepInfo.unwrappedNonNullType

        return object : FieldFetchingInstrumentationContext {
            override fun onFetchedValue(fetchedValue: Any?) {
                println("${fieldName}:$path")
                executions.computeIfPresent(executionId) { _, executionState ->
                    executionState.fieldToDispatchedState(field, fieldExecutionStrategyPath, fieldGraphQLType, fetchedValue)
                    executionState
                }

                ifAllSyncExecutionsExhausted { executionIds ->
                    onSyncExecutionExhausted(executionIds)
                }
            }

            override fun onCompleted(result: Any?, t: Throwable?) {
                executions.computeIfPresent(executionId) { _, executionState ->
                    println("${fieldName}:$path")
                    executionState.fieldToCompletedState(field, fieldExecutionStrategyPath, result)
                    executionState
                }

                ifAllSyncExecutionsExhausted { executionIds ->
                    onSyncExecutionExhausted(executionIds)
                }
            }

            override fun onDispatched() {
            }
        }
    }

    /**
     * execute a given [predicate] when all [ExecutionInput] sharing a [GraphQLContext] exhausted their execution.
     * A Synchronous Execution is considered Exhausted when all [DataFetcher]s of all paths were executed up until
     * a scalar leaf or a [DataFetcher] that returns a [CompletableFuture]
     */
    fun ifAllSyncExecutionsExhausted(predicate: (List<ExecutionId>) -> Unit) =
        synchronized(executions) {
            val operationsToExecute = totalExecutions.get()
            if (executions.size < operationsToExecute || !dataLoaderRegistry.onDispatchFuturesHandled())
                return@synchronized

            if (executions.values.all(ExecutionBatchState::isSyncExecutionExhausted)) {
                predicate(executions.keys().toList())
            }
        }
}
