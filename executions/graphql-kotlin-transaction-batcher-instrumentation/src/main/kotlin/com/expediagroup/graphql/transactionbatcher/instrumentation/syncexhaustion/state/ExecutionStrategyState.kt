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
import graphql.schema.GraphQLType
import graphql.schema.GraphQLTypeUtil
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap

enum class ExecutionStrategyExhaustionState { NOT_EXHAUSTED, EXHAUSTED }
enum class FieldFetchState { NOT_DISPATCHED, DISPATCHED, COMPLETED }
enum class FieldFetchType { UNKNOWN, ASYNC, SYNC }

class ExecutionStrategyState(selections: List<Field>) {
    val fieldsState: ConcurrentHashMap<String, FieldState> = ConcurrentHashMap(
        selections.associateBy(Field::getResultKey) { FieldState() }
    )
    val totalFields = fieldsState.size
    var dispatchedFields: Int = 0
    var exhaustionState: ExecutionStrategyExhaustionState = ExecutionStrategyExhaustionState.NOT_EXHAUSTED

    inner class FieldState {
        var fetchState: FieldFetchState = FieldFetchState.NOT_DISPATCHED
        var fetchType: FieldFetchType = FieldFetchType.UNKNOWN
        var graphQLType: GraphQLType? = null
        var result: Any? = null
        val executionStrategyPaths: MutableList<String> = mutableListOf()

        fun isList(): Boolean = GraphQLTypeUtil.isList(graphQLType)
        fun isLeaf(): Boolean = GraphQLTypeUtil.isLeaf(graphQLType)

        fun toDispatchedState(graphQLType: GraphQLType, result: CompletableFuture<Any?>): FieldState {
            dispatchedFields++
            this.fetchState = FieldFetchState.DISPATCHED
            this.graphQLType = graphQLType
            this.fetchType = if (result.isDone) FieldFetchType.SYNC else FieldFetchType.ASYNC
            return this
        }
        fun toCompletedState(result: Any?): FieldState {
            this.fetchState = FieldFetchState.COMPLETED
            this.result = result
            return this
        }
    }
}
