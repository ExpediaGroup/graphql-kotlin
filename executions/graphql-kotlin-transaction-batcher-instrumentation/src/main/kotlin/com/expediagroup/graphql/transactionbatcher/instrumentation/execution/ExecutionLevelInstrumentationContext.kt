package com.expediagroup.graphql.transactionbatcher.instrumentation.execution

import com.expediagroup.graphql.transactionbatcher.instrumentation.state.Level
import graphql.ExecutionInput

interface ExecutionLevelInstrumentationContext {
    fun onLevelDispatched(level: Level, executions: List<ExecutionInput>)
}
