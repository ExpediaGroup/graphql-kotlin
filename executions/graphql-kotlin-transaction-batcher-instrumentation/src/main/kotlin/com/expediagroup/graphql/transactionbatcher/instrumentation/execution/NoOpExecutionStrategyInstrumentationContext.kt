package com.expediagroup.graphql.transactionbatcher.instrumentation.execution

import graphql.ExecutionResult
import graphql.execution.instrumentation.ExecutionStrategyInstrumentationContext
import java.util.concurrent.CompletableFuture

object NoOpExecutionStrategyInstrumentationContext : ExecutionStrategyInstrumentationContext {
    override fun onDispatched(result: CompletableFuture<ExecutionResult>) {
    }
    override fun onCompleted(result: ExecutionResult, t: Throwable) {
    }
}
