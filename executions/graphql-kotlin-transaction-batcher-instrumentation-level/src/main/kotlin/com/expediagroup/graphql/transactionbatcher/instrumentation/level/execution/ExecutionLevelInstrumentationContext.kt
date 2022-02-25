package com.expediagroup.graphql.transactionbatcher.instrumentation.level.execution

import com.expediagroup.graphql.transactionbatcher.instrumentation.level.state.Level

interface ExecutionLevelInstrumentationContext {
    fun onLevelDispatched(level: Level)
}
