package com.expediagroup.graphql.transactionbatcher.instrumentation.level.execution

import graphql.execution.ExecutionContext

data class ExecutionLevelInstrumentationParameters(
    val executionContext: ExecutionContext
)
