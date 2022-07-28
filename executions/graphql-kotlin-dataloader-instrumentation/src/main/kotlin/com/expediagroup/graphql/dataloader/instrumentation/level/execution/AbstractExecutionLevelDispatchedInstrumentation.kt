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

package com.expediagroup.graphql.dataloader.instrumentation.level.execution

import com.expediagroup.graphql.dataloader.instrumentation.extensions.isMutation
import com.expediagroup.graphql.dataloader.instrumentation.level.state.ExecutionLevelDispatchedState
import com.expediagroup.graphql.dataloader.instrumentation.level.state.Level
import graphql.ExecutionInput
import graphql.ExecutionResult
import graphql.execution.ExecutionContext
import graphql.execution.instrumentation.ExecutionStrategyInstrumentationContext
import graphql.execution.instrumentation.Instrumentation
import graphql.execution.instrumentation.InstrumentationContext
import graphql.execution.instrumentation.InstrumentationState
import graphql.execution.instrumentation.parameters.InstrumentationExecuteOperationParameters
import graphql.execution.instrumentation.parameters.InstrumentationExecutionStrategyParameters
import graphql.execution.instrumentation.parameters.InstrumentationFieldFetchParameters
import graphql.schema.DataFetcher

/**
 * Represents the signature of a callback that will be executed when a [Level] is dispatched
 */
internal typealias OnLevelDispatchedCallback = (Level, List<ExecutionInput>) -> Unit
/**
 * Custom GraphQL [graphql.execution.instrumentation.Instrumentation] that calculate the state of executions
 * of all queries sharing the same GraphQLContext map
 */
abstract class AbstractExecutionLevelDispatchedInstrumentation : Instrumentation {
    /**
     * This is invoked each time instrumentation attempts to calculate a level dispatched state, this can be called from either
     * `beginFieldField` or `beginExecutionStrategy`.
     *
     * @param parameters contains information of which [ExecutionInput] caused the calculation and from which hook
     * @return [OnLevelDispatchedCallback] to invoke a method when a certain level of all operations dispatched
     * like `onDispatched`
     */
    abstract fun getOnLevelDispatchedCallback(
        parameters: ExecutionLevelDispatchedInstrumentationParameters
    ): OnLevelDispatchedCallback

    override fun beginExecuteOperation(
        parameters: InstrumentationExecuteOperationParameters,
        state: InstrumentationState?
    ): InstrumentationContext<ExecutionResult>? =
        parameters.executionContext.takeUnless(ExecutionContext::isMutation)
            ?.graphQLContext?.get<ExecutionLevelDispatchedState>(ExecutionLevelDispatchedState::class)
            ?.beginExecuteOperation(parameters)

    override fun beginExecutionStrategy(
        parameters: InstrumentationExecutionStrategyParameters,
        state: InstrumentationState?
    ): ExecutionStrategyInstrumentationContext? =
        parameters.executionContext.takeUnless(ExecutionContext::isMutation)
            ?.graphQLContext?.get<ExecutionLevelDispatchedState>(ExecutionLevelDispatchedState::class)
            ?.beginExecutionStrategy(
                parameters,
                this.getOnLevelDispatchedCallback(
                    ExecutionLevelDispatchedInstrumentationParameters(
                        parameters.executionContext,
                        ExecutionLevelCalculationSource.EXECUTION_STRATEGY
                    )
                )
            )

    override fun beginFieldFetch(
        parameters: InstrumentationFieldFetchParameters,
        state: InstrumentationState?
    ): InstrumentationContext<Any>? =
        parameters.executionContext.takeUnless(ExecutionContext::isMutation)
            ?.graphQLContext?.get<ExecutionLevelDispatchedState>(ExecutionLevelDispatchedState::class)
            ?.beginFieldFetch(
                parameters,
                this.getOnLevelDispatchedCallback(
                    ExecutionLevelDispatchedInstrumentationParameters(
                        parameters.executionContext,
                        ExecutionLevelCalculationSource.FIELD_FETCH
                    )
                )
            )

    override fun instrumentDataFetcher(
        dataFetcher: DataFetcher<*>,
        parameters: InstrumentationFieldFetchParameters,
        state: InstrumentationState?
    ): DataFetcher<*> =
        parameters.executionContext.takeUnless(ExecutionContext::isMutation)
            ?.graphQLContext?.get<ExecutionLevelDispatchedState>(ExecutionLevelDispatchedState::class)
            ?.instrumentDataFetcher(dataFetcher, parameters)
            ?: dataFetcher
}
