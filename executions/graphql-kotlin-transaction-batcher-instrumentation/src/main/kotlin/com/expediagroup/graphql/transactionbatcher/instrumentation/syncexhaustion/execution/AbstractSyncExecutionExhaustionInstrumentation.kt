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

package com.expediagroup.graphql.transactionbatcher.instrumentation.syncexhaustion.execution

import com.expediagroup.graphql.transactionbatcher.instrumentation.NoOpExecutionStrategyInstrumentationContext
import com.expediagroup.graphql.transactionbatcher.instrumentation.syncexhaustion.state.SyncExecutionExhaustionInstrumentationState
import graphql.ExecutionResult
import graphql.execution.instrumentation.ExecutionStrategyInstrumentationContext
import graphql.execution.instrumentation.InstrumentationContext
import graphql.execution.instrumentation.SimpleInstrumentation
import graphql.execution.instrumentation.SimpleInstrumentationContext
import graphql.execution.instrumentation.parameters.InstrumentationExecuteOperationParameters
import graphql.execution.instrumentation.parameters.InstrumentationExecutionStrategyParameters
import graphql.execution.instrumentation.parameters.InstrumentationFieldFetchParameters

/**
 * Custom GraphQL [graphql.execution.instrumentation.Instrumentation] that calculate the state of executions
 * of all queries sharing the same GraphQLContext
 */
abstract class AbstractSyncExecutionExhaustionInstrumentation : SimpleInstrumentation(), SyncExecutionExhaustionInstrumentation {

    override fun beginExecuteOperation(
        parameters: InstrumentationExecuteOperationParameters
    ): InstrumentationContext<ExecutionResult> =
        parameters.executionContext
            .graphQLContext.get<SyncExecutionExhaustionInstrumentationState>(SyncExecutionExhaustionInstrumentationState::class)
            ?.beginExecuteOperation(parameters)
            ?: SimpleInstrumentationContext.noOp()

    override fun beginExecutionStrategy(
        parameters: InstrumentationExecutionStrategyParameters
    ): ExecutionStrategyInstrumentationContext =
        parameters.executionContext
            .graphQLContext.get<SyncExecutionExhaustionInstrumentationState>(SyncExecutionExhaustionInstrumentationState::class)
            ?.beginExecutionStrategy(parameters)
            ?: NoOpExecutionStrategyInstrumentationContext

    override fun beginFieldFetch(
        parameters: InstrumentationFieldFetchParameters
    ): InstrumentationContext<Any> =
        parameters.executionContext
            .graphQLContext.get<SyncExecutionExhaustionInstrumentationState>(SyncExecutionExhaustionInstrumentationState::class)
            ?.beginFieldFetch(
                parameters,
                this.calculateSyncExecutionState(
                    SyncExecutionExhaustionInstrumentationParameters(parameters.executionContext)
                )
            )
            ?: SimpleInstrumentationContext.noOp()
}
