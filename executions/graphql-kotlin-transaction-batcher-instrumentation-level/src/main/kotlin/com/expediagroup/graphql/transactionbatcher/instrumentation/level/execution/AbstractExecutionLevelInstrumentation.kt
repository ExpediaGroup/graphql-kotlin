package com.expediagroup.graphql.transactionbatcher.instrumentation.level.execution

import com.expediagroup.graphql.transactionbatcher.instrumentation.level.state.ExecutionLevelInstrumentationState
import graphql.ExecutionResult
import graphql.execution.instrumentation.ExecutionStrategyInstrumentationContext
import graphql.execution.instrumentation.InstrumentationContext
import graphql.execution.instrumentation.SimpleInstrumentation
import graphql.execution.instrumentation.SimpleInstrumentationContext
import graphql.execution.instrumentation.parameters.InstrumentationExecuteOperationParameters
import graphql.execution.instrumentation.parameters.InstrumentationExecutionStrategyParameters
import graphql.execution.instrumentation.parameters.InstrumentationFieldFetchParameters
import java.util.concurrent.CompletableFuture

abstract class AbstractExecutionLevelInstrumentation : SimpleInstrumentation(), ExecutionLevelInstrumentation {
    override fun beginExecuteOperation(
        parameters: InstrumentationExecuteOperationParameters
    ): InstrumentationContext<ExecutionResult> =
        parameters
            .executionContext
            .graphQLContext.get<ExecutionLevelInstrumentationState>(ExecutionLevelInstrumentationState::class)
            ?.beginExecuteOperation(parameters)
            ?: SimpleInstrumentationContext.noOp()

    override fun beginExecutionStrategy(
        parameters: InstrumentationExecutionStrategyParameters
    ): ExecutionStrategyInstrumentationContext =
        parameters
            .executionContext
            .graphQLContext.get<ExecutionLevelInstrumentationState>(ExecutionLevelInstrumentationState::class)
            ?.beginExecutionStrategy(
                parameters,
                this.beginExecutionLevel(
                    ExecutionLevelInstrumentationParameters(parameters.executionContext)
                )
            )
            ?: object : ExecutionStrategyInstrumentationContext {
                override fun onDispatched(result: CompletableFuture<ExecutionResult>) {
                }
                override fun onCompleted(result: ExecutionResult, t: Throwable) {
                }
            }

    override fun beginFieldFetch(
        parameters: InstrumentationFieldFetchParameters
    ): InstrumentationContext<Any> =
        parameters
            .executionContext
            .graphQLContext.get<ExecutionLevelInstrumentationState>(ExecutionLevelInstrumentationState::class)
            ?.beginFieldFetch(
                parameters,
                this.beginExecutionLevel(ExecutionLevelInstrumentationParameters(parameters.executionContext))
            )
            ?: SimpleInstrumentationContext.noOp()
}
