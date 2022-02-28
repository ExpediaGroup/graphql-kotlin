package com.expediagroup.graphql.transactionbatcher.instrumentation.execution

import graphql.execution.ExecutionContext

data class ExecutionLevelInstrumentationParameters(
    val executionContext: ExecutionContext
)
