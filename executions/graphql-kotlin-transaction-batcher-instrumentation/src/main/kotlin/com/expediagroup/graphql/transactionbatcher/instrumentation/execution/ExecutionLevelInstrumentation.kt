package com.expediagroup.graphql.transactionbatcher.instrumentation.execution

import graphql.execution.instrumentation.Instrumentation

interface ExecutionLevelInstrumentation : Instrumentation {
    fun beginExecutionLevel(
        parameters: ExecutionLevelInstrumentationParameters
    ): ExecutionLevelInstrumentationContext
}
