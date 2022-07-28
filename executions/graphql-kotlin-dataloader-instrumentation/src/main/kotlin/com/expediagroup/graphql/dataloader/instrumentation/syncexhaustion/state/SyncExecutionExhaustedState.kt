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

package com.expediagroup.graphql.dataloader.instrumentation.syncexhaustion.state

import com.expediagroup.graphql.dataloader.KotlinDataLoaderRegistry
import com.expediagroup.graphql.dataloader.instrumentation.syncexhaustion.execution.OnSyncExecutionExhaustedCallback
import graphql.ExecutionInput
import graphql.ExecutionResult
import graphql.GraphQLContext
import graphql.execution.MergedField
import graphql.execution.instrumentation.ExecutionStrategyInstrumentationContext
import graphql.execution.instrumentation.InstrumentationContext
import graphql.execution.instrumentation.parameters.InstrumentationExecuteOperationParameters
import graphql.execution.instrumentation.parameters.InstrumentationExecutionStrategyParameters
import graphql.execution.instrumentation.parameters.InstrumentationFieldFetchParameters
import graphql.schema.DataFetcher
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap

/**
 * Orchestrate the [ExecutionBatchState] of all [ExecutionInput] sharing the same [GraphQLContext],
 * when a certain state is reached will invoke [OnSyncExecutionExhaustedCallback]
 */
class SyncExecutionExhaustedState(
    private val totalExecutions: Int,
    private val dataLoaderRegistry: KotlinDataLoaderRegistry
) {
    val executions = ConcurrentHashMap<ExecutionInput, ExecutionBatchState>()

    /**
     * Create the [ExecutionBatchState] When a specific [ExecutionInput] starts his execution
     *
     * @param parameters contains information of which [ExecutionInput] will start his execution
     * @return a non null [InstrumentationContext] object
     */
    fun beginExecuteOperation(
        parameters: InstrumentationExecuteOperationParameters
    ): InstrumentationContext<ExecutionResult>? {
        executions.computeIfAbsent(parameters.executionContext.executionInput) {
            ExecutionBatchState()
        }
        return null
    }

    /**
     * Add [ExecutionStrategyState] into operation [ExecutionBatchState] for the field that
     * just started an ExecutionStrategy
     *
     * @param parameters contains information of which [ExecutionInput] started an executionStrategy
     * @return a noop [ExecutionStrategyInstrumentationContext]
     */
    fun beginExecutionStrategy(
        parameters: InstrumentationExecutionStrategyParameters
    ): ExecutionStrategyInstrumentationContext? {
        val executionInput = parameters.executionContext.executionInput

        executions.computeIfPresent(executionInput) { _, executionState ->
            val executionStrategyParameters = parameters.executionStrategyParameters

            val field = executionStrategyParameters.field?.singleField
            val path = executionStrategyParameters.path
            val selectionFields = executionStrategyParameters.fields.subFieldsList.map(MergedField::getSingleField)
            val parentGraphQLType = executionStrategyParameters.executionStepInfo.parent?.unwrappedNonNullType

            executionState.addExecutionStrategyState(field, path, selectionFields, parentGraphQLType)
            executionState
        }

        return null
    }

    /**
     * This is called just before a field [DataFetcher] is invoked
     *
     * @param parameters contains information of which field will starting the fetching
     * @param onSyncExecutionExhausted invoke when the sync execution fo all operations is exhausted
     * @return a [InstrumentationContext] object that will be called back when the [DataFetcher]
     * dispatches and completes
     */
    fun beginFieldFetch(
        parameters: InstrumentationFieldFetchParameters,
        onSyncExecutionExhausted: OnSyncExecutionExhaustedCallback
    ): InstrumentationContext<Any> {
        val executionInput = parameters.executionContext.executionInput
        val executionStepInfo = parameters.executionStepInfo
        val field = parameters.executionStepInfo.field.singleField
        val fieldExecutionStrategyPath = parameters.environment.executionStepInfo.path.parent
        val fieldGraphQLType = executionStepInfo.unwrappedNonNullType

        return object : InstrumentationContext<Any> {
            override fun onDispatched(result: CompletableFuture<Any?>) {
                executions.computeIfPresent(executionInput) { _, executionState ->
                    executionState.fieldToDispatchedState(field, fieldExecutionStrategyPath, fieldGraphQLType, result)
                    executionState
                }

                val allSyncExecutionsExhausted = allSyncExecutionsExhausted()
                if (allSyncExecutionsExhausted) {
                    onSyncExecutionExhausted(executions.keys().toList())
                }
            }
            override fun onCompleted(result: Any?, t: Throwable?) {
                executions.computeIfPresent(executionInput) { _, executionState ->
                    executionState.fieldToCompletedState(field, fieldExecutionStrategyPath, result)
                    executionState
                }

                val allSyncExecutionsExhausted = allSyncExecutionsExhausted()
                if (allSyncExecutionsExhausted) {
                    onSyncExecutionExhausted(executions.keys().toList())
                }
            }
        }
    }

    /**
     * Provide the information about when all [ExecutionInput] sharing a [GraphQLContext] exhausted their execution
     * A Synchronous Execution is considered Exhausted when all [DataFetcher]s of all paths were executed up until
     * a scalar leaf or a [DataFetcher] that returns a [CompletableFuture]
     */
    fun allSyncExecutionsExhausted(): Boolean = synchronized(executions) {
        when {
            executions.size < totalExecutions || !dataLoaderRegistry.onDispatchFuturesHandled() -> false
            else -> executions.values.all(ExecutionBatchState::isSyncExecutionExhausted)
        }
    }
}
