/*
 * Copyright 2025 Expedia, Inc
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
import org.dataloader.DataLoader
import org.dataloader.DataLoaderRegistry
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

/**
 * Orchestrate the [ExecutionInputState] of all [ExecutionInput] sharing the same [GraphQLContext],
 * when a synchronous execution state is exhausted will dispatch DataLoaderRegistry from [dataLoaderRegistryProvider]
 */
class SyncExecutionExhaustedState(
    totalOperations: Int,
    private val dataLoaderRegistryProvider: () -> DataLoaderRegistry
) {
    private val totalExecutions = AtomicInteger(totalOperations)
    private val executions = ConcurrentHashMap<ExecutionId, ExecutionInputState>()
    private val dataLoadersDispatchState = DataLoaderRegistryState()

    /**
     * Create the [ExecutionInputState] When a specific [ExecutionInput] starts his execution
     *
     * @param parameters contains information of which [ExecutionInput] will start his execution
     * @return a non null [InstrumentationContext] object
     */
    fun beginExecution(
        parameters: InstrumentationExecutionParameters
    ): InstrumentationContext<ExecutionResult> {
        val executionId = parameters.executionInput.executionId ?: return SimpleInstrumentationContext.noOp()
        executions.computeIfAbsent(executionId) {
            ExecutionInputState(parameters.executionInput)
        }
        return object : SimpleInstrumentationContext<ExecutionResult>() {
            /**
             * Remove an [ExecutionInputState] from the state in case operation does not qualify for starting or completing execution,
             * for example:
             * - parsing, validation errors
             * - persisted query errors
             * - an exception during execution was thrown
             */
            override fun onCompleted(result: ExecutionResult?, t: Throwable?) {
                if ((result != null && result.errors.isNotEmpty()) || t != null) {
                    if (executions.containsKey(executionId)) {
                        synchronized(executions) {
                            executions.remove(executionId)
                            totalExecutions.set(totalExecutions.get() - 1)
                        }
                        if (allSyncExecutionsExhausted()) {
                            dataLoaderRegistryProvider.invoke().dispatchAll()
                        }
                    }
                }
            }
        }
    }

    /**
     * Add [ExecutionStrategyState] into operation [ExecutionInputState] for the object that
     * just started an ExecutionStrategy
     *
     * @param parameters contains information of which [ExecutionInput] started an executionStrategy
     * @return a noop [ExecutionStrategyInstrumentationContext]
     */
    fun beginRecursiveExecution(
        parameters: InstrumentationExecutionStrategyParameters
    ) {
        val executionId = parameters.executionContext.executionInput.executionId ?: return
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
     * This is invoked just before a field [DataFetcher] is invoked
     *
     * @param parameters contains information of which field will start the fetching
     * @return a [InstrumentationContext] object that will be called back when the [DataFetcher]
     * dispatches and completes
     */
    fun beginFieldFetching(
        parameters: InstrumentationFieldFetchParameters
    ): FieldFetchingInstrumentationContext {
        val executionId = parameters.executionContext.executionInput.executionId ?: return FieldFetchingInstrumentationContext.NOOP
        val field = parameters.executionStepInfo.field.singleField
        val fieldExecutionStrategyPath = parameters.executionStepInfo.path.parent
        val fieldGraphQLType = parameters.executionStepInfo.unwrappedNonNullType

        return object : FieldFetchingInstrumentationContext {
            override fun onFetchedValue(fetchedValue: Any?) {
                executions.computeIfPresent(executionId) { _, executionState ->
                    executionState.fieldToDispatchedState(field, fieldExecutionStrategyPath, fieldGraphQLType, fetchedValue)
                    executionState
                }

                if (allSyncExecutionsExhausted()) {
                    dataLoaderRegistryProvider.invoke().dispatchAll()
                }
            }

            override fun onCompleted(result: Any?, t: Throwable?) {
                executions.computeIfPresent(executionId) { _, executionState ->
                    executionState.fieldToCompletedState(field, fieldExecutionStrategyPath, result)
                    executionState
                }

                if (allSyncExecutionsExhausted()) {
                    dataLoaderRegistryProvider.invoke().dispatchAll()
                }
            }

            override fun onDispatched() {
            }
        }
    }

    fun dataLoadersLoadInvokedAfterDispatchAll(): Boolean =
        dataLoadersDispatchState.dataLoadersLoadInvokedAfterDispatchAll()

    /**
     * This is invoked right after a [DataLoader.load] was dispatched
     */
    fun onDataLoaderLoadDispatched() {
        dataLoadersDispatchState.onDataLoaderLoadDispatched()
    }

    /**
     * This is invoked right after a [DataLoader.load] was completed
     */
    fun onDataLoaderLoadCompleted() {
        dataLoadersDispatchState.onDataLoaderLoadCompleted()
        if (allSyncExecutionsExhausted()) {
            dataLoaderRegistryProvider.invoke().dispatchAll()
        }
    }

    /**
     * check if all [ExecutionInput] sharing a [GraphQLContext] exhausted their execution.
     * A Synchronous Execution is considered Exhausted when all [DataFetcher]s of all paths were executed up until
     * a scalar leaf or a [DataFetcher] that returns a [CompletableFuture]
     */
    fun allSyncExecutionsExhausted(): Boolean =
        synchronized(executions) {
            if (executions.size < totalExecutions.get() || !dataLoadersDispatchState.onDispatchAllFuturesCompleted())
                return false

            if (executions.values.all(ExecutionInputState::isSyncExecutionExhausted)) {
                dataLoadersDispatchState.takeSnapshot()
                return true
            } else {
                return false
            }
        }
}
