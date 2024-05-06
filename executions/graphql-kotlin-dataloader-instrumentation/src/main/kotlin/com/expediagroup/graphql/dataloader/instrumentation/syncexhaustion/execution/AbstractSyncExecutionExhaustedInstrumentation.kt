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

package com.expediagroup.graphql.dataloader.instrumentation.syncexhaustion.execution

import com.expediagroup.graphql.dataloader.instrumentation.extensions.isMutation
import com.expediagroup.graphql.dataloader.instrumentation.syncexhaustion.state.SyncExecutionExhaustedState
import graphql.ExecutionInput
import graphql.ExecutionResult
import graphql.GraphQLContext
import graphql.execution.ExecutionContext
import graphql.execution.ExecutionId
import graphql.execution.instrumentation.ExecuteObjectInstrumentationContext
import graphql.execution.instrumentation.ExecutionStrategyInstrumentationContext
import graphql.execution.instrumentation.FieldFetchingInstrumentationContext
import graphql.execution.instrumentation.Instrumentation
import graphql.execution.instrumentation.InstrumentationContext
import graphql.execution.instrumentation.InstrumentationState
import graphql.execution.instrumentation.SimplePerformantInstrumentation
import graphql.execution.instrumentation.parameters.InstrumentationExecutionParameters
import graphql.execution.instrumentation.parameters.InstrumentationExecutionStrategyParameters
import graphql.execution.instrumentation.parameters.InstrumentationFieldFetchParameters

/**
 * typealias that represents the signature of a callback that will be executed when sync execution is exhausted
 */
internal typealias OnSyncExecutionExhaustedCallback = (List<ExecutionId>) -> Unit

/**
 * Custom GraphQL [Instrumentation] that calculate the synchronous execution exhaustion
 * of all GraphQL operations sharing the same [GraphQLContext]
 */
abstract class AbstractSyncExecutionExhaustedInstrumentation : SimplePerformantInstrumentation() {
    /**
     * This is invoked each time instrumentation attempts to calculate exhaustion state, this can be called from either
     * `beginFieldField.dispatch` or `beginFieldFetch.complete`.
     *
     * @param parameters contains information of which [ExecutionInput] caused the calculation
     * @return [OnSyncExecutionExhaustedCallback] to invoke when the synchronous execution of all operations was exhausted
     */
    abstract fun getOnSyncExecutionExhaustedCallback(
        parameters: SyncExecutionExhaustedInstrumentationParameters
    ): OnSyncExecutionExhaustedCallback

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
            ?.beginFieldFetching(
                parameters,
                this.getOnSyncExecutionExhaustedCallback(
                    SyncExecutionExhaustedInstrumentationParameters(parameters.executionContext)
                )
            )
}
