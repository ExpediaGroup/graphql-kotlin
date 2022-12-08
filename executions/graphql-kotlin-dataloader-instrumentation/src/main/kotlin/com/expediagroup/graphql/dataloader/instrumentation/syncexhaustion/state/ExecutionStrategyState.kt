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
import graphql.execution.ExecutionStrategy
import graphql.language.Field
import graphql.schema.DataFetcher
import graphql.schema.GraphQLList
import graphql.schema.GraphQLType
import graphql.schema.GraphQLTypeUtil
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicReference

/**
 * Hold and calculate the [fieldsState] of an [ExecutionStrategy]
 * associated with an [ExecutionInput] complex field.
 *
 * Example:
 *
 * Given this [ExecutionInput]:
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
 * the [ExecutionStrategyState] of the root [ExecutionStrategy] would be this:
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
 */
class ExecutionStrategyState(
    selections: List<Field>
) {
    private var dispatchedFields: AtomicReference<Int> = AtomicReference(0)
    private var syncState: AtomicReference<ExecutionStrategySyncState> = AtomicReference(ExecutionStrategySyncState.NOT_EXHAUSTED)
    val fieldsState: ConcurrentHashMap<String, FieldState> = ConcurrentHashMap(
        selections.associateBy(Field::getResultKey) { FieldState() }
    )

    /**
     * Transition this [ExecutionStrategyState] to a new state
     * @param newState the new [ExecutionStrategySyncState] that the executionStrategyState will transition
     */
    fun transitionTo(newState: ExecutionStrategySyncState): Unit = syncState.set(newState)

    /**
     * Check if the [syncState] was previously calculated as [ExecutionStrategySyncState.EXHAUSTED].
     *
     * @return Boolean result of checking if [syncState] is exhausted.
     */
    fun isSyncStateExhausted(): Boolean = syncState.get() == ExecutionStrategySyncState.EXHAUSTED

    /**
     * Check if all the [Field]s associated with this [ExecutionStrategyState] were visited.
     * A field is considered `"visited"` if at least was transitioned to [FieldFetchState.DISPATCHED] state
     *
     * @return Boolean result of checking if all fields were visited.
     */
    fun allFieldsVisited(): Boolean = dispatchedFields.get() == fieldsState.size

    /**
     * Hold, calculate and transition the state of a [Field] associated with a [ExecutionStrategyState].
     */
    inner class FieldState {
        private var fetchState: FieldFetchState = FieldFetchState.NOT_DISPATCHED
        private var fetchType: FieldFetchType = FieldFetchType.UNKNOWN
        private var graphQLType: GraphQLType? = null
        private var result: Any? = null
        val executionStrategyPaths: MutableList<String> = mutableListOf()

        /**
         * Transition the [FieldState] to [FieldFetchState.DISPATCHED] state
         * at this state we know the [GraphQLType] of the [Field] and have access to the result [CompletableFuture]
         * of the [Field] [DataFetcher] in order to calculate the [Field] [FieldFetchType].
         *
         * @param graphQLType [GraphQLType] of the [Field] which [DataFetcher] was dispatched.
         * @param result [CompletableFuture] result of the [DataFetcher.get] call.
         * @return this [FieldState].
         */
        fun toDispatchedState(
            graphQLType: GraphQLType,
            result: CompletableFuture<Any?>
        ): FieldState = this.also {
            this.fetchState = FieldFetchState.DISPATCHED
            this.graphQLType = graphQLType
            this.fetchType = when {
                result.isDone -> FieldFetchType.SYNC
                else -> FieldFetchType.ASYNC
            }

            this@ExecutionStrategyState.dispatchedFields.updateAndGet { current -> current + 1 }
        }

        /**
         * Transition the [FieldState] to [FieldFetchState.COMPLETED]state
         * at this state we know the result of the [CompletableFuture] [DataFetcher] call and potentially spin
         * new [ExecutionStrategy]ies depending on the [GraphQLType] type and [result] nullability
         *
         * @param result nullable Object after the [DataFetcher] [CompletableFuture] associated with the [Field]
         * completed.
         *
         * @return this [FieldState].
         */
        fun toCompletedState(
            result: Any?
        ): FieldState = this.also {
            this.fetchState = FieldFetchState.COMPLETED
            this.result = result
        }

        /**
         * Add the executionStrategy of a field that came out of the [result] object
         *
         * @param executionStrategyPath the execution strategy string representation of the field
         * associated with the [result] of this state.
         *
         * Example:
         *
         * ```
         * query allAstronauts {
         *   astronauts {
         *     id
         *     name
         *   }
         * }
         * ```
         *
         * `astronauts` [DataFetcher] will complete with a list of 3 object, the state will transition
         * to completed and an ExecutionStrategy for each object will begin, this method will receive
         * `"/astronauts[0]"` and it will add it to the state [executionStrategyPaths]
         * ```
         * {
         *   "fetchState": "COMPLETED",
         *   "fetchType": "ASYNC",
         *   "GraphQLType": "GraphQLList",
         *   "result": [{ "id": 1, ... }, { "id": 2, .... }, { "id": 3, .... }]
         *   "executionStrategyPaths": ["/astronauts[0]"]
         * }
         * ```
         */
        fun addExecutionStrategyPath(executionStrategyPath: String) {
            executionStrategyPaths.add(executionStrategyPath)
        }

        /**
         * field [fetchState] is completed with a non-null [result] and
         * [graphQLType] is not a leaf [GraphQLList] and
         * all non-null complex objects inside the [result] list started their own executionStrategy
         *
         * @return Boolean indicating if above conditions met
         */
        fun isCompletedListOfComplexObjects(): Boolean =
            fetchState == FieldFetchState.COMPLETED && result != null &&
                GraphQLTypeUtil.isList(graphQLType) && !GraphQLTypeUtil.isLeaf(graphQLType) &&
                (result as? List<*>)?.filterNotNull()?.size == executionStrategyPaths.size

        /**
         * field [fetchState] is completed with a non-null [result] and
         * [graphQLType] is not a leaf complex type which executionStrategy was started.
         *
         * @return Boolean indicating if above conditions met
         */
        fun isCompletedComplexObject(): Boolean =
            fetchState == FieldFetchState.COMPLETED && result != null &&
                !GraphQLTypeUtil.isList(graphQLType) && !GraphQLTypeUtil.isLeaf(graphQLType) &&
                executionStrategyPaths.isNotEmpty()

        /**
         * field [fetchState] is [FieldFetchState.COMPLETED]
         * field [graphQLType] is a Leaf or null.
         *
         * @return Boolean indicating if above conditions met
         */
        fun isCompletedLeafOrNull(): Boolean =
            fetchState == FieldFetchState.COMPLETED &&
                (GraphQLTypeUtil.isLeaf(graphQLType) || result == null)

        /**
         * field [fetchType] is [FieldFetchType.ASYNC],
         * field [fetchState] is [FieldFetchState.DISPATCHED]
         *
         * @return Boolean indicating if above conditions met
         */
        fun isAsyncDispatched(): Boolean =
            fetchType == FieldFetchType.ASYNC && fetchState == FieldFetchState.DISPATCHED
    }
}
