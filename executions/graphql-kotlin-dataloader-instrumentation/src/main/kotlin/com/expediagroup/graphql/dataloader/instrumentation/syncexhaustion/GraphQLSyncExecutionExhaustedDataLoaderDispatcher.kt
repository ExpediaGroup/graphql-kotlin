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

package com.expediagroup.graphql.dataloader.instrumentation.syncexhaustion

import com.expediagroup.graphql.dataloader.instrumentation.extensions.isMutation
import com.expediagroup.graphql.dataloader.instrumentation.syncexhaustion.state.SyncExecutionExhaustedState
import graphql.ExecutionResult
import graphql.GraphQLContext
import graphql.execution.ExecutionContext
import graphql.execution.instrumentation.ExecuteObjectInstrumentationContext
import graphql.execution.instrumentation.ExecutionStrategyInstrumentationContext
import graphql.execution.instrumentation.FieldFetchingInstrumentationContext
import graphql.execution.instrumentation.Instrumentation
import graphql.execution.instrumentation.InstrumentationContext
import graphql.execution.instrumentation.InstrumentationState
import graphql.execution.instrumentation.SimplePerformantInstrumentation
import graphql.execution.instrumentation.parameters.InstrumentationExecutionParameters
import graphql.execution.instrumentation.parameters.InstrumentationExecutionStrategyParameters
import graphql.execution.instrumentation.parameters.InstrumentationFieldCompleteParameters
import graphql.execution.instrumentation.parameters.InstrumentationFieldFetchParameters

/**
 * Custom GraphQL [Instrumentation] that calculate the synchronous execution exhaustion
 * of all GraphQL operations sharing the same [GraphQLContext]
 */
class GraphQLSyncExecutionExhaustedDataLoaderDispatcher : SimplePerformantInstrumentation() {

    override fun beginExecution(
        parameters: InstrumentationExecutionParameters,
        state: InstrumentationState?
    ): InstrumentationContext<ExecutionResult>? =
        parameters.graphQLContext
            ?.get<SyncExecutionExhaustedState>(SyncExecutionExhaustedState::class)
            ?.beginExecution(parameters)

    override fun beginExecutionStrategy(
        parameters: InstrumentationExecutionStrategyParameters,
        state: InstrumentationState?
    ): ExecutionStrategyInstrumentationContext? {
        parameters.executionContext.takeUnless(ExecutionContext::isMutation)
            ?.graphQLContext?.get<SyncExecutionExhaustedState>(SyncExecutionExhaustedState::class)
            ?.beginRecursiveExecution(parameters)
        return null
    }

    override fun beginExecuteObject(
        parameters: InstrumentationExecutionStrategyParameters,
        state: InstrumentationState?
    ): ExecuteObjectInstrumentationContext? {
        parameters.executionContext.takeUnless(ExecutionContext::isMutation)
            ?.graphQLContext?.get<SyncExecutionExhaustedState>(SyncExecutionExhaustedState::class)
            ?.beginRecursiveExecution(parameters)
        return null
    }

    override fun beginFieldFetching(
        parameters: InstrumentationFieldFetchParameters,
        state: InstrumentationState?
    ): FieldFetchingInstrumentationContext? =
        parameters.executionContext.takeUnless(ExecutionContext::isMutation)
            ?.graphQLContext?.get<SyncExecutionExhaustedState>(SyncExecutionExhaustedState::class)
            ?.beginFieldFetching(parameters)

    override fun beginFieldCompletion(
        parameters: InstrumentationFieldCompleteParameters,
        state: InstrumentationState?
    ): InstrumentationContext<Any>? {
        println("field completed: ${parameters.fetchedValue}")
        return super.beginFieldCompletion(parameters, state)
    }
}
