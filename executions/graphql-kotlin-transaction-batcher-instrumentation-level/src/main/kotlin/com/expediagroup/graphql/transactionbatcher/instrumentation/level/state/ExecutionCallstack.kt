package com.expediagroup.graphql.transactionbatcher.instrumentation.level.state

import graphql.execution.instrumentation.InstrumentationState

enum class LevelState { NOT_DISPATCHED, DISPATCHED }

class ExecutionCallstack(
    height: Int
) : InstrumentationState {

    private val levelsState: MutableMap<Level, LevelState> =
        mutableMapOf(*Array(height) { level -> Pair(Level(level + 1), LevelState.NOT_DISPATCHED) })

    private val expectedFetchCountPerLevel: MutableMap<Level, Int> = mutableMapOf(
        *Array(height) { level -> Pair(Level(level + 1), 0) }
    )
    private val happenedFetchCountPerLevel: MutableMap<Level, Int> = mutableMapOf(
        *Array(height) { level -> Pair(Level(level + 1), 0) }
    )
    private val expectedStrategyCallsPerLevel: MutableMap<Level, Int> = mutableMapOf(
        *Array(height) { level -> Pair(Level(level + 1), 0) }
    )
    private val happenedStrategyCallsPerLevel: MutableMap<Level, Int> = mutableMapOf(
        *Array(height) { level -> Pair(Level(level + 1), 0) }
    )
    private val happenedOnFieldValueCallsPerLevel: MutableMap<Level, Int> = mutableMapOf(
        *Array(height) { level -> Pair(Level(level + 1), 0) }
    )

    init {
        expectedStrategyCallsPerLevel[Level(1)] = 1
    }

    fun containsLevel(level: Level): Boolean = levelsState.containsKey(level)

    fun markLevelAsDispatched(level: Level) {
        levelsState.computeIfPresent(level) { _, _ -> LevelState.DISPATCHED }
    }

    fun isLevelDispatched(level: Level): Boolean = levelsState[level] == LevelState.DISPATCHED

    fun increaseExpectedFetchCount(level: Level, count: Int) {
        expectedFetchCountPerLevel.computeIfPresent(level) { _, value -> value + count }
    }

    fun increaseHappenedFetchCount(level: Level) {
        happenedFetchCountPerLevel.computeIfPresent(level) { _, value -> value + 1 }
    }

    fun increaseExpectedStrategyCalls(level: Level, count: Int) {
        expectedStrategyCallsPerLevel.computeIfPresent(level) { _, value -> value + count }
    }

    fun increaseHappenedStrategyCalls(level: Level) {
        happenedStrategyCallsPerLevel.computeIfPresent(level) { _, value -> value + 1 }
    }

    fun increaseHappenedOnFieldValueCalls(level: Level) {
        happenedOnFieldValueCallsPerLevel.computeIfPresent(level) { _, value -> value + 1 }
    }

    fun allStrategyCallsHappened(level: Level): Boolean =
        happenedStrategyCallsPerLevel[level] == expectedStrategyCallsPerLevel[level]

    fun allOnFieldCallsHappened(level: Level): Boolean =
        happenedOnFieldValueCallsPerLevel[level] == expectedStrategyCallsPerLevel[level]

    fun allFetchesHappened(level: Level): Boolean =
        happenedFetchCountPerLevel[level] == expectedFetchCountPerLevel[level]
}
