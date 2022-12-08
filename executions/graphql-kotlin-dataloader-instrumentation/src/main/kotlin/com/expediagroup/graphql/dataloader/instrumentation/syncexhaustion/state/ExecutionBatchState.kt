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

import graphql.ExecutionInput
import graphql.execution.ResultPath
import graphql.language.Field
import graphql.schema.DataFetcher
import graphql.schema.GraphQLType
import graphql.schema.GraphQLTypeUtil.isList
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap

/**
 * Hold and calculate the state of a given [ExecutionInput] by holding and tracking information of all Execution Strategies that
 * were executed to resolve the [ExecutionInput].
 *
 * Example:
 *
 * given this [ExecutionInput]
 *
 * ```
 * query getAstronaut {
 *   astronaut(id: 1) {
 *      id
 *      name
 *   }
 * }
 * ```
 *
 * When the [ExecutionBatchState] will be considered exhausted will have this state:
 *
 * ```
 * {
 *   "/": {
 *     "dispatchedFetches": 1,
 *       "fieldsState": {
 *         "astronaut": {
 *           "fetchState": "DISPATCHED",
 *           "fetchType": "ASYNC",
 *           "result": null,
 *           "executionStrategyPaths": []
 *         }
 *      }
 *   }
 * }
 * ```
 *
 * once astronaut [DataFetcher] completes his value starting a new ExecutionStrategy for `astronaut` field for
 * the resolution of `id`, and `name`, once these fields are dispatched and completed
 * and exhaustion will be calculated again and the state of [ExecutionBatchState] will be the following:
 *
 * ```
 * {
 *   "/": {
 *     "dispatchedFetches": 1,
 *       "fieldsState": {
 *         "astronaut": {
 *           "fetchState": "COMPLETED",
 *           "fetchType": "ASYNC",
 *           "result": { ... },
 *           "executionStrategyPaths": ["/astronaut"]
 *         }
 *      }
 *   },
 *   "/astronaut": {
 *     "dispatchedFetches": 2,
 *     "fieldsState": {
 *       "id": {
 *         "fetchState": "COMPLETED",
 *         "fetchType": "SYNC",
 *         "executionStrategyPaths": []
 *       },
 *       "name": {
 *         "fetchState": "COMPLETED",
 *         "fetchType": "SYNC",
 *         "executionStrategyPaths": []
 *       }
 *     }
 *   }
 * }
 * ```
 */
class ExecutionBatchState {

    private val executionStrategiesState: ConcurrentHashMap<String, ExecutionStrategyState> = ConcurrentHashMap()

    /**
     * This method will add an [ExecutionStrategyState] into [executionStrategiesState] which state will be changed when a field
     * dispatches or completes
     *
     * @param field a nullable [Field] that represents the field that just started an executionStrategy, the ONLY use case where this
     * param could be null is when the root executionStrategy starts.
     * @param fieldExecutionStrategyPath the [ResultPath] of the field that just started an executionStrategy, example: `"/astronaut"`, `"/"`.
     * @param fieldSelections the list of [Field]s that the executionStrategy will attempt to resolve.
     * @param parentFieldGraphQLType the [GraphQLType] of the parent [field] that will start an execution strategy.
     */
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
                    parentExecutionStrategyState.fieldsState[field?.resultKey]?.addExecutionStrategyPath(
                        fieldExecutionStrategyPathString
                    )
                    parentExecutionStrategyState
                }
        }
    }

    /**
     * Apply a transition on the [field] to dispatched state.
     *
     * @param field the [Field] that will transition to dispatched state.
     * @param fieldExecutionStrategyPath the [ResultPath] associated to the ExecutionStrategy that dispatched the [field].
     * @param fieldGraphQLType the [GraphQLType] of the [field].
     * @param result the [DataFetcher] [CompletableFuture] result.
     */
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

    /**
     * Apply a transition on the [field] to completed state.
     *
     * @param field the [Field] that will transition to completed state.
     * @param fieldExecutionStrategyPath the [ResultPath] associated to the ExecutionStrategy that completed the [field].
     * @param result the nullable [Any] result of the [DataFetcher] when its completed
     */
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

    /**
     * Recursively calculate the sync state of this [ExecutionBatchState],
     * by traversing all [executionStrategiesState].
     *
     * @param executionStrategyPath the string representation of the executionStrategy which state will be calculated,
     * defaults to [ROOT_EXECUTION_STRATEGY_PATH] to start fom the root [ExecutionStrategyState].
     */
    fun isSyncExecutionExhausted(
        executionStrategyPath: String = ROOT_EXECUTION_STRATEGY_PATH
    ): Boolean {
        val executionStrategyState = this.executionStrategiesState[executionStrategyPath] ?: return false

        if (!executionStrategyState.allFieldsVisited()) {
            return false
        }
        if (executionStrategyState.isSyncStateExhausted()) {
            return true
        }

        return executionStrategyState.fieldsState.all { (_, state) ->
            when {
                state.isCompletedListOfComplexObjects() -> {
                    state.executionStrategyPaths.all { executionStrategyPath ->
                        isSyncExecutionExhausted(executionStrategyPath)
                    }
                }
                state.isCompletedComplexObject() -> {
                    isSyncExecutionExhausted(state.executionStrategyPaths.first())
                }
                state.isCompletedLeafOrNull() || state.isAsyncDispatched() -> {
                    true
                }
                else -> false
            }
        }.also { isExhausted ->
            /**
             * in order to avoid some computations we can store the [ExecutionStrategySyncState] that we
             * just calculated.
             */
            when {
                isExhausted -> executionStrategyState.transitionTo(ExecutionStrategySyncState.EXHAUSTED)
                else -> executionStrategyState.transitionTo(ExecutionStrategySyncState.NOT_EXHAUSTED)
            }
        }
    }

    /**
     * Reset the [ExecutionStrategySyncState] of each [ExecutionStrategyState] from [origin] path to root.
     *
     * @param origin [ResultPath] from where the [ExecutionStrategySyncState] will be reset
     * up to the root [ExecutionStrategyState]
     */
    private fun setPathToNotExhaustedState(
        origin: ResultPath
    ) {
        var currentPath: ResultPath? = origin
        while (currentPath != null) {
            executionStrategiesState[currentPath.toString()]?.transitionTo(ExecutionStrategySyncState.NOT_EXHAUSTED)
            currentPath = currentPath.parent
        }
    }

    companion object {
        private const val ROOT_EXECUTION_STRATEGY_PATH: String = ""
    }
}
