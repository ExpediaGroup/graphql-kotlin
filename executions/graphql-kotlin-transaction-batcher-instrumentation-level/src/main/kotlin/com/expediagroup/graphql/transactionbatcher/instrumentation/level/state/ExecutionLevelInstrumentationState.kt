package com.expediagroup.graphql.transactionbatcher.instrumentation.level.state

import com.expediagroup.graphql.transactionbatcher.instrumentation.level.execution.ExecutionLevelInstrumentationContext
import com.expediagroup.graphql.transactionbatcher.instrumentation.level.extensions.getDocumentHeight
import com.expediagroup.graphql.transactionbatcher.instrumentation.level.extensions.getExpectedStrategyCalls
import com.expediagroup.graphql.transactionbatcher.instrumentation.level.extensions.synchronizeIfPresent
import graphql.ExecutionInput
import graphql.ExecutionResult
import graphql.execution.FieldValueInfo
import graphql.execution.instrumentation.ExecutionStrategyInstrumentationContext
import graphql.execution.instrumentation.InstrumentationContext
import graphql.execution.instrumentation.SimpleInstrumentationContext
import graphql.execution.instrumentation.parameters.InstrumentationExecuteOperationParameters
import graphql.execution.instrumentation.parameters.InstrumentationExecutionStrategyParameters
import graphql.execution.instrumentation.parameters.InstrumentationFieldFetchParameters
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap

class ExecutionLevelInstrumentationState(
    private val totalExecutions: Int
) {
    val executions = ConcurrentHashMap<ExecutionInput, ExecutionState>()

    fun beginExecuteOperation(
        parameters: InstrumentationExecuteOperationParameters
    ): InstrumentationContext<ExecutionResult> {
        executions[parameters.executionContext.executionInput] = ExecutionState(
            parameters.executionContext.getDocumentHeight()
        )
        return SimpleInstrumentationContext.noOp()
    }

    fun beginExecutionStrategy(
        parameters: InstrumentationExecutionStrategyParameters,
        executionLevelContext: ExecutionLevelInstrumentationContext
    ): ExecutionStrategyInstrumentationContext {
        val executionInput = parameters.executionContext.executionInput
        val level = Level(parameters.executionStrategyParameters.path.level + 1)
        val fieldCount = parameters.executionStrategyParameters.fields.size()

        executions.synchronizeIfPresent(executionInput) { executionState ->
            executionState.increaseExpectedFetches(level, fieldCount)
            executionState.increaseHappenedExecutionStrategies(level)
        }

        return object : ExecutionStrategyInstrumentationContext {
            override fun onDispatched(result: CompletableFuture<ExecutionResult>) {
            }

            override fun onCompleted(result: ExecutionResult, t: Throwable) {
            }

            override fun onFieldValuesInfo(fieldValueInfoList: List<FieldValueInfo>) {
                val nextLevel = level.next()
                executions.synchronizeIfPresent(executionInput) { executionState ->
                    executionState.increaseHappenedOnFieldValueInfos(level)
                    executionState.increaseExpectedExecutionStrategies(
                        nextLevel,
                        fieldValueInfoList.getExpectedStrategyCalls()
                    )
                }
                val allExecutionsDispatched = synchronized(executions) {
                    allExecutionsDispatched(nextLevel)
                }
                if (allExecutionsDispatched) {
                    executionLevelContext.onLevelDispatched(nextLevel)
                }
            }

            override fun onFieldValuesException() {
                synchronized(executions) {
                    executions[executionInput]?.increaseHappenedOnFieldValueInfos(level)
                }
            }
        }
    }

    fun beginFieldFetch(
        parameters: InstrumentationFieldFetchParameters,
        executionLevelContext: ExecutionLevelInstrumentationContext
    ): InstrumentationContext<Any> {
        val executionInput = parameters.executionContext.executionInput
        val path = parameters.environment.executionStepInfo.path
        val level = Level(path.level)

        return object : InstrumentationContext<Any> {
            override fun onDispatched(result: CompletableFuture<Any?>) {
                executions.synchronizeIfPresent(executionInput) { executionState ->
                    executionState.increaseHappenedFetches(level)
                }
                val allExecutionsDispatched = synchronized(executions) {
                    allExecutionsDispatched(level)
                }
                if (allExecutionsDispatched) {
                    executionLevelContext.onLevelDispatched(level)
                }
            }

            override fun onCompleted(result: Any?, t: Throwable?) {
            }
        }
    }

    private fun allExecutionsDispatched(level: Level): Boolean =
        executions
            .takeIf { callstacks -> callstacks.size == totalExecutions }
            ?.filter { (_, callstack) -> callstack.contains(level) }
            ?.takeIf { callstacksWithSameLevel -> callstacksWithSameLevel.isNotEmpty() }
            ?.all { (_, callstack) -> callstack.isLevelDispatched(level) }
            ?: false
}
