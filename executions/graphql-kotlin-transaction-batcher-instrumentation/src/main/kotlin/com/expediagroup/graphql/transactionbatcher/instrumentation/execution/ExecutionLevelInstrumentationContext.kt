package com.expediagroup.graphql.transactionbatcher.instrumentation.execution

import com.expediagroup.graphql.transactionbatcher.instrumentation.state.Level

interface ExecutionLevelInstrumentationContext {
    fun onLevelDispatched(level: Level)
}
