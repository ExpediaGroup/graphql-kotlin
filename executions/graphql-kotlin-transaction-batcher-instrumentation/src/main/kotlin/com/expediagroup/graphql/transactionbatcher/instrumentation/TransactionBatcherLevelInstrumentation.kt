package com.expediagroup.graphql.transactionbatcher.instrumentation

import com.expediagroup.graphql.transactionbatcher.instrumentation.execution.AbstractExecutionLevelInstrumentation
import com.expediagroup.graphql.transactionbatcher.instrumentation.execution.ExecutionLevelInstrumentationContext
import com.expediagroup.graphql.transactionbatcher.instrumentation.execution.ExecutionLevelInstrumentationParameters
import com.expediagroup.graphql.transactionbatcher.instrumentation.state.Level
import com.expediagroup.graphql.transactionbatcher.transaction.TransactionBatcher
import graphql.ExecutionInput

class TransactionBatcherLevelInstrumentation : AbstractExecutionLevelInstrumentation() {
    override fun calculateLevelState(
        parameters: ExecutionLevelInstrumentationParameters
    ): ExecutionLevelInstrumentationContext =
        object : ExecutionLevelInstrumentationContext {
            override fun onLevelDispatched(level: Level, executions: List<ExecutionInput>) {
                parameters
                    .executionContext
                    .graphQLContext.get<TransactionBatcher>(TransactionBatcher::class)
                    ?.dispatch()
            }
        }
}
