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

package com.expediagroup.graphql.transactionbatcher.instrumentation.syncexhaustion.state

import graphql.language.Field
import graphql.execution.ResultPath
import graphql.schema.GraphQLType
import graphql.schema.GraphQLTypeUtil.isList
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap

class ExecutionBatchState {

    private val executionStrategiesState: ConcurrentHashMap<String, ExecutionStrategyState> = ConcurrentHashMap()

    fun addExecutionStrategyState(
        field: Field?,
        fieldExecutionStrategyPath: ResultPath,
        fieldSelections: List<Field>,
        parentFieldGraphQLType: GraphQLType?
    ) {
        val fieldExecutionStrategyPathString = fieldExecutionStrategyPath.toString()

        executionStrategiesState.computeIfAbsent(fieldExecutionStrategyPathString) {
            ExecutionStrategyState(fieldSelections)
        }

        val parentFieldExecutionStrategyPathString = when {
            isList(parentFieldGraphQLType) -> fieldExecutionStrategyPath.parent?.parent?.toString()
            else -> fieldExecutionStrategyPath.parent?.toString()
        }

        parentFieldExecutionStrategyPathString?.let {
            executionStrategiesState
                .computeIfPresent(parentFieldExecutionStrategyPathString) { _, parentExecutionStrategyState ->
                    parentExecutionStrategyState.fieldsState[field?.resultKey]?.executionStrategyPaths?.add(
                        fieldExecutionStrategyPathString
                    )
                    parentExecutionStrategyState
                }
        }
    }

    fun fieldToDispatchedState(
        field: Field,
        fieldExecutionStrategyPath: ResultPath,
        fieldGraphQLType: GraphQLType,
        result: CompletableFuture<Any?>
    ) {
        val fieldExecutionStrategyPathString = fieldExecutionStrategyPath.toString()
        executionStrategiesState[fieldExecutionStrategyPathString]?.let { executionStrategyState ->
            executionStrategyState.fieldsState[field.resultKey]?.toDispatchedState(fieldGraphQLType, result)
            setPathToNotExhaustedState(fieldExecutionStrategyPath)
        }
    }

    fun fieldToCompletedState(
        field: Field,
        fieldExecutionStrategyPath: ResultPath,
        result: Any?
    ) {
        val fieldExecutionStrategyPathString = fieldExecutionStrategyPath.toString()
        executionStrategiesState[fieldExecutionStrategyPathString]?.let { executionStrategyState ->
            executionStrategyState.fieldsState[field.resultKey]?.toCompletedState(result)
            setPathToNotExhaustedState(fieldExecutionStrategyPath)
        }
    }

    fun isSyncExecutionExhausted(
        executionStrategyPath: String = ROOT_EXECUTION_STRATEGY_PATH
    ): Boolean {
        val executionStrategyState = this.executionStrategiesState[executionStrategyPath]

        if (executionStrategyState == null || !executionStrategyState.allFieldsDispatched()) {
            return false
        }
        if (executionStrategyState.exhaustionState == ExecutionStrategyExhaustionState.EXHAUSTED) {
            return true
        }

        return executionStrategyState.fieldsState.all { (_, state) ->
            when {
                // field is completed and is a non leaf list,
                // so we need to check that non null results size equals to the executionStrategyPaths size
                // and then check for isSyncExecutionExhausted for each executionStrategyPath
                state.fetchState == FieldFetchState.COMPLETED && state.result != null &&
                    state.isList() && !state.isLeaf() &&
                    (state.result as? List<*>)?.filterNotNull()?.size == state.executionStrategyPaths.size -> {
                    state.executionStrategyPaths.all { executionStrategyPath ->
                        isSyncExecutionExhausted(executionStrategyPath)
                    }
                }
                // field is completed and his type is not a list nor a leaf,
                // so we need to check that results size equals to the executionStrategyPaths size
                // and then check for isSyncExecutionExhausted of executionStrategyPath
                state.fetchState == FieldFetchState.COMPLETED && state.result != null &&
                    !state.isList() && !state.isLeaf() && state.executionStrategyPaths.isNotEmpty() -> {
                    isSyncExecutionExhausted(state.executionStrategyPaths.first())
                }
                // field is completed and his type is leaf or null
                state.fetchState == FieldFetchState.COMPLETED &&
                    (state.isLeaf() || state.result == null) -> {
                    true
                }
                // field is async, dispatched and his type is not leaf
                state.fetchType == FieldFetchType.ASYNC &&
                    state.fetchState == FieldFetchState.DISPATCHED &&
                    !state.isLeaf() -> {
                    true
                }
                else -> false
            }
        }.also { isExhausted ->
            executionStrategyState.exhaustionState = when {
                isExhausted -> ExecutionStrategyExhaustionState.EXHAUSTED
                else -> ExecutionStrategyExhaustionState.NOT_EXHAUSTED
            }
        }
    }

    private fun setPathToNotExhaustedState(
        origin: ResultPath
    ) {
        var currentPath: ResultPath? = origin
        while (currentPath != null) {
            executionStrategiesState[currentPath.toString()]?.exhaustionState =
                ExecutionStrategyExhaustionState.NOT_EXHAUSTED
            currentPath = currentPath.parent
        }
    }

    companion object {
        private const val ROOT_EXECUTION_STRATEGY_PATH: String = ""
    }
}
