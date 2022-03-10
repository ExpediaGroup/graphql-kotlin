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

package com.expediagroup.graphql.transactionbatcher.instrumentation.state

import com.expediagroup.graphql.transactionbatcher.instrumentation.execution.ExecutionLevelInstrumentationContext
import com.expediagroup.graphql.transactionbatcher.instrumentation.extensions.getDocumentHeight
import com.expediagroup.graphql.transactionbatcher.instrumentation.extensions.getExpectedStrategyCalls
import com.expediagroup.graphql.transactionbatcher.instrumentation.extensions.synchronizeIfPresent
import graphql.ExecutionInput
import graphql.ExecutionResult
import graphql.execution.FieldValueInfo
import graphql.execution.instrumentation.ExecutionStrategyInstrumentationContext
import graphql.execution.instrumentation.InstrumentationContext
import graphql.execution.instrumentation.SimpleInstrumentationContext
import graphql.execution.instrumentation.parameters.InstrumentationExecuteOperationParameters
import graphql.execution.instrumentation.parameters.InstrumentationExecutionStrategyParameters
import graphql.execution.instrumentation.parameters.InstrumentationFieldFetchParameters
import graphql.schema.DataFetcher
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap

/**
 * Orchestrate the [ExecutionState] of all [ExecutionInput] sharing the same graphQLContext map,
 * when a certain state is reached will invoke [ExecutionLevelInstrumentationContext]
 */
class ExecutionLevelInstrumentationState(
    private val totalExecutions: Int
) {
    val executions = ConcurrentHashMap<ExecutionInput, ExecutionState>()

    /**
     * When a specific [ExecutionInput] starts his execution, calculate the height of the AST Document
     *
     * @param parameters contains information of which [ExecutionInput] will start his execution
     * @return a non null [InstrumentationContext] object
     */
    fun beginExecuteOperation(
        parameters: InstrumentationExecuteOperationParameters
    ): InstrumentationContext<ExecutionResult> {
        executions[parameters.executionContext.executionInput] = ExecutionState(
            parameters.executionContext.getDocumentHeight()
        )
        return SimpleInstrumentationContext.noOp()
    }

    /**
     * When a specific [ExecutionInput] begins an executionStrategy, modify the state of his [ExecutionState]
     *
     * @param parameters contains information of which [ExecutionInput] will start an ExecutionStrategy
     * @param executionLevelContext invoke a method associated with an event calculated using the [ExecutionState]
     */
    fun beginExecutionStrategy(
        parameters: InstrumentationExecutionStrategyParameters,
        executionLevelContext: ExecutionLevelInstrumentationContext
    ): ExecutionStrategyInstrumentationContext {
        val executionInput = parameters.executionContext.executionInput
        val level = Level(parameters.executionStrategyParameters.path.level + 1)
        val fieldCount = parameters.executionStrategyParameters.fields.size()

        executions.synchronizeIfPresent(executionInput) { executionState ->
            executionState.increaseExpectedFetches(level, fieldCount)
            executionState.increaseHappenedExecutionStrategies(level)
        }

        return object : ExecutionStrategyInstrumentationContext {
            override fun onDispatched(result: CompletableFuture<ExecutionResult>) {
            }

            override fun onCompleted(result: ExecutionResult, t: Throwable) {
            }

            override fun onFieldValuesInfo(fieldValueInfoList: List<FieldValueInfo>) {
                val nextLevel = level.next()

                executions.synchronizeIfPresent(executionInput) { executionState ->
                    executionState.increaseHappenedOnFieldValueInfos(level)
                    executionState.increaseExpectedExecutionStrategies(
                        nextLevel,
                        fieldValueInfoList.getExpectedStrategyCalls()
                    )
                }

                val allExecutionsDispatched = synchronized(executions) { allExecutionsDispatched(nextLevel) }
                if (allExecutionsDispatched) {
                    executionLevelContext.onDispatched(nextLevel, executions.keys().toList())
                    executions.forEach { (_, executionState) -> executionState.completeDataFetchers(nextLevel) }
                }
            }

            override fun onFieldValuesException() {
                synchronized(executions) {
                    executions[executionInput]?.increaseHappenedOnFieldValueInfos(level)
                }
            }
        }
    }

    /**
     * When a specific [ExecutionInput] begins an fieldFetch, modify the state of his [ExecutionState]
     *
     * @param parameters contains information of which [ExecutionInput] will start an ExecutionStrategy
     * @param executionLevelContext invoke a method associated with an event calculated using the [ExecutionState]
     */
    fun beginFieldFetch(
        parameters: InstrumentationFieldFetchParameters,
        executionLevelContext: ExecutionLevelInstrumentationContext
    ): InstrumentationContext<Any> {
        val executionInput = parameters.executionContext.executionInput
        val path = parameters.environment.executionStepInfo.path
        val level = Level(path.level)

        return object : InstrumentationContext<Any> {
            override fun onDispatched(result: CompletableFuture<Any?>) {
                executions.synchronizeIfPresent(executionInput) { executionState ->
                    executionState.increaseHappenedFetches(level)
                }

                val allExecutionsDispatched = synchronized(executions) { allExecutionsDispatched(level) }
                if (allExecutionsDispatched) {
                    executionLevelContext.onDispatched(level, executions.keys().toList())
                    executions.forEach { (_, executionState) -> executionState.completeDataFetchers(level) }
                }
            }

            override fun onCompleted(result: Any?, t: Throwable?) {
            }
        }
    }

    /**
     * Modify runtime behaviour of ExecutionStrategy by instrumenting a data fetcher that can be
     * manually completed, by default ExecutionStrategy will do a Depth First execution, by instrumenting
     * the [dataFetcher] it will switch to Breath First execution to complete when a level of all operations sharing
     * a graphQLContext was dispatched
     *
     * @param dataFetcher the original dataFetcher that will be instrumented
     * @param parameters contains information of which [ExecutionInput] will use the [dataFetcher]
     * @return [ManuallyCompletableDataFetcher]
     */
    fun instrumentDataFetcher(
        dataFetcher: DataFetcher<*>,
        parameters: InstrumentationFieldFetchParameters
    ): DataFetcher<*> =
        executions.synchronizeIfPresent(parameters.executionContext.executionInput) { executionState ->
            val level = Level(parameters.executionStepInfo.path.level)
            executionState.toManuallyCompletableDataFetcher(level, dataFetcher)
        } ?: dataFetcher

    /**
     * calculate if all executions sharing a graphQLContext was dispatched, by
     * 1. Checking if the height of  all executions was already calculated.
     * 2. Filter all executions sharing the same Level
     * 3. check if all executions sharing the same level dispatched that level.
     *
     * @param level that execution state will be calculated
     * @return Boolean for allExecutionsDispatched statement
     */
    private fun allExecutionsDispatched(level: Level): Boolean =
        executions
            .takeIf { executions -> executions.size == totalExecutions }
            ?.filter { (_, executionState) -> executionState.contains(level) }
            ?.takeIf { executionsWithSameLevel -> executionsWithSameLevel.isNotEmpty() }
            ?.all { (_, executionState) -> executionState.isLevelDispatched(level) }
            ?: false
}
