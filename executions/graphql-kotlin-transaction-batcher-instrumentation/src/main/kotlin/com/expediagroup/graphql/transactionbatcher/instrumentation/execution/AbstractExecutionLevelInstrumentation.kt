package com.expediagroup.graphql.transactionbatcher.instrumentation.execution

import com.expediagroup.graphql.transactionbatcher.instrumentation.state.ExecutionLevelInstrumentationState
import graphql.ExecutionResult
import graphql.execution.instrumentation.ExecutionStrategyInstrumentationContext
import graphql.execution.instrumentation.InstrumentationContext
import graphql.execution.instrumentation.SimpleInstrumentation
import graphql.execution.instrumentation.SimpleInstrumentationContext
import graphql.execution.instrumentation.parameters.InstrumentationExecuteOperationParameters
import graphql.execution.instrumentation.parameters.InstrumentationExecutionStrategyParameters
import graphql.execution.instrumentation.parameters.InstrumentationFieldFetchParameters
import graphql.schema.DataFetcher

abstract class AbstractExecutionLevelInstrumentation : SimpleInstrumentation(), ExecutionLevelInstrumentation {

    override fun beginExecuteOperation(
        parameters: InstrumentationExecuteOperationParameters
    ): InstrumentationContext<ExecutionResult> =
        parameters.executionContext
            .graphQLContext.get<ExecutionLevelInstrumentationState>(ExecutionLevelInstrumentationState::class)
            ?.beginExecuteOperation(parameters)
            ?: SimpleInstrumentationContext.noOp()

    override fun beginExecutionStrategy(
        parameters: InstrumentationExecutionStrategyParameters
    ): ExecutionStrategyInstrumentationContext =
        parameters.executionContext
            .graphQLContext.get<ExecutionLevelInstrumentationState>(ExecutionLevelInstrumentationState::class)
            ?.beginExecutionStrategy(
                parameters,
                this.calculateLevelState(
                    ExecutionLevelInstrumentationParameters(
                        parameters.executionContext,
                        ExecutionLevelCalculationSource.EXECUTION_STRATEGY
                    )
                )
            )
            ?: NoOpExecutionStrategyInstrumentationContext

    override fun beginFieldFetch(
        parameters: InstrumentationFieldFetchParameters
    ): InstrumentationContext<Any> =
        parameters.executionContext
            .graphQLContext.get<ExecutionLevelInstrumentationState>(ExecutionLevelInstrumentationState::class)
            ?.beginFieldFetch(
                parameters,
                this.calculateLevelState(
                    ExecutionLevelInstrumentationParameters(
                        parameters.executionContext,
                        ExecutionLevelCalculationSource.FIELD_FETCH
                    )
                )
            )
            ?: SimpleInstrumentationContext.noOp()

    override fun instrumentDataFetcher(
        dataFetcher: DataFetcher<*>,
        parameters: InstrumentationFieldFetchParameters
    ): DataFetcher<*> =
        parameters.executionContext
            .graphQLContext.get<ExecutionLevelInstrumentationState>(ExecutionLevelInstrumentationState::class)
            ?.instrumentDataFetcher(dataFetcher, parameters)
            ?: dataFetcher
}
