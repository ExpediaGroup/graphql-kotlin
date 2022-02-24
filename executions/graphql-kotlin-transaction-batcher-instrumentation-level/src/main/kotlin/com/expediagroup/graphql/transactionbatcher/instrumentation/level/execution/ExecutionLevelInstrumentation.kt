package com.expediagroup.graphql.transactionbatcher.instrumentation.level.execution

import graphql.execution.instrumentation.Instrumentation

interface ExecutionLevelInstrumentation : Instrumentation {
    fun beginExecuteOperationLevel(
        parameters: ExecutionLevelInstrumentationParameters
    ): ExecutionLevelInstrumentationContext
}
