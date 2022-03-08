package com.expediagroup.graphql.transactionbatcher.instrumentation.execution

import graphql.execution.ExecutionContext

enum class ExecutionLevelCalculationSource { EXECUTION_STRATEGY, FIELD_FETCH }

data class ExecutionLevelInstrumentationParameters(
    val executionContext: ExecutionContext,
    val calculationSource: ExecutionLevelCalculationSource
)
