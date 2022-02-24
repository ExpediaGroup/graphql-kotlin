package com.expediagroup.graphql.transactionbatcher.instrumentation.level.state

import com.expediagroup.graphql.transactionbatcher.instrumentation.level.execution.ExecutionLevelInstrumentationContext
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
    val callstacks = ConcurrentHashMap<ExecutionInput, ExecutionCallstack>()

    fun beginExecuteOperation(
        parameters: InstrumentationExecuteOperationParameters
    ): InstrumentationContext<ExecutionResult> {
        if (!callstacks.contains(parameters.executionContext.executionInput)) {
            val height = DocumentHeightCalculator.calculate(parameters.executionContext)
            callstacks[parameters.executionContext.executionInput] = ExecutionCallstack(height)
        }
        return SimpleInstrumentationContext.noOp()
    }

    fun beginExecutionStrategy(
        parameters: InstrumentationExecutionStrategyParameters,
        executionLevelContext: ExecutionLevelInstrumentationContext
    ): ExecutionStrategyInstrumentationContext {
        val executionInput = parameters.executionContext.executionInput
        val level = Level(parameters.executionStrategyParameters.path.level + 1)
        val fieldCount = parameters.executionStrategyParameters.fields.size()

        synchronized(callstacks) {
            callstacks[executionInput]?.increaseExpectedFetchCount(level, fieldCount)
            callstacks[executionInput]?.increaseHappenedStrategyCalls(level)
        }

        return object : ExecutionStrategyInstrumentationContext {
            override fun onDispatched(result: CompletableFuture<ExecutionResult>) {
            }

            override fun onCompleted(result: ExecutionResult, t: Throwable) {
            }

            override fun onFieldValuesInfo(fieldValueInfoList: List<FieldValueInfo>) {
                val nextLevel = level.next()
                val isLevelReady = synchronized(callstacks) {
                    callstacks[executionInput]?.increaseHappenedOnFieldValueCalls(level)
                    val expectedStrategyCallsForNextLevel = getExpectedStrategyCalls(fieldValueInfoList)
                    callstacks[executionInput]?.increaseExpectedStrategyCalls(
                        nextLevel,
                        expectedStrategyCallsForNextLevel
                    )
                    allExecutionsDispatched(nextLevel)
                }
                if (isLevelReady) {
                    executionLevelContext.onLevelReady(nextLevel)
                }
            }

            override fun onFieldValuesException() {
                synchronized(callstacks) {
                    callstacks[executionInput]?.increaseHappenedOnFieldValueCalls(level)
                }
            }
        }
    }

    private fun getExpectedStrategyCalls(fieldValueInfoList: List<FieldValueInfo>): Int {
        var count = 0
        fieldValueInfoList.forEach { fieldValueInfo ->
            when (fieldValueInfo.completeValueType) {
                FieldValueInfo.CompleteValueType.OBJECT -> count++
                FieldValueInfo.CompleteValueType.LIST -> count += getFieldCount(fieldValueInfo.fieldValueInfos)
                else -> {
                }
            }
        }
        return count
    }

    private fun getFieldCount(fieldValueInfos: List<FieldValueInfo>): Int {
        var count = 0
        fieldValueInfos.forEach { fieldValueInfo ->
            when (fieldValueInfo.completeValueType) {
                FieldValueInfo.CompleteValueType.OBJECT -> count++
                FieldValueInfo.CompleteValueType.LIST -> count += getFieldCount(fieldValueInfo.fieldValueInfos)
                else -> {
                }
            }
        }
        return count
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
                val dispatchNeeded = synchronized(callstacks) {
                    callstacks[executionInput]?.increaseHappenedFetchCount(level)
                    allExecutionsDispatched(level)
                }
                if (dispatchNeeded) {
                    executionLevelContext.onLevelReady(level)
                }
            }

            override fun onCompleted(result: Any?, t: Throwable?) {
            }
        }
    }

    private fun allExecutionsDispatched(level: Level): Boolean =
        when (callstacks.size) {
            totalExecutions -> {
                callstacks
                    .filter { (_, callstack) -> callstack.containsLevel(level) }
                    .takeIf { callstacksWithSameLevel -> callstacksWithSameLevel.isNotEmpty() }
                    ?.all { (_, callstack) -> isLevelDispatched(callstack, level) }
                    ?: false
            }
            else -> false
        }

    private fun isLevelDispatched(callstack: ExecutionCallstack, level: Level): Boolean =
        when {
            callstack.isLevelDispatched(level) -> true
            level.isFirst() -> callstack.allFetchesHappened(level)
            else -> {
                isLevelDispatched(callstack, level.previous()) &&
                    callstack.allOnFieldCallsHappened(level.previous()) &&
                    callstack.allStrategyCallsHappened(level) &&
                    callstack.allFetchesHappened(level)
            }
        }.also { isLevelDispatched ->
            if (isLevelDispatched) {
                callstack.markLevelAsDispatched(level)
            }
        }
}
