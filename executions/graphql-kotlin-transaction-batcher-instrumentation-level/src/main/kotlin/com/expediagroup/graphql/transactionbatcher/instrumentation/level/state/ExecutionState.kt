package com.expediagroup.graphql.transactionbatcher.instrumentation.level.state

enum class LevelState { NOT_DISPATCHED, DISPATCHED }

class ExecutionState(documentHeight: Int) {

    private val levelsState: MutableMap<Level, LevelState> = mutableMapOf(
        *Array(documentHeight) { level -> Pair(Level(level + 1), LevelState.NOT_DISPATCHED) }
    )

    private val expectedFetches: MutableMap<Level, Int> = mutableMapOf(
        *Array(documentHeight) { level -> Pair(Level(level + 1), 0) }
    )
    private val happenedFetches: MutableMap<Level, Int> = mutableMapOf(
        *Array(documentHeight) { level -> Pair(Level(level + 1), 0) }
    )

    private val expectedExecutionStrategies: MutableMap<Level, Int> = mutableMapOf(
        *Array(documentHeight) { level -> Pair(Level(level + 1), 0) }
    )
    private val happenedExecutionStrategies: MutableMap<Level, Int> = mutableMapOf(
        *Array(documentHeight) { level -> Pair(Level(level + 1), 0) }
    )

    private val happenedOnFieldValueInfos: MutableMap<Level, Int> = mutableMapOf(
        *Array(documentHeight) { level -> Pair(Level(level + 1), 0) }
    )

    init {
        expectedExecutionStrategies[Level(1)] = 1
    }

    fun contains(level: Level): Boolean = levelsState.containsKey(level)

    fun increaseExpectedFetches(level: Level, count: Int): Int? =
        expectedFetches[level]?.plus(count)

    fun increaseHappenedFetches(level: Level): Int? =
        happenedFetches[level]?.inc()

    fun increaseExpectedExecutionStrategies(level: Level, count: Int): Int? =
        expectedExecutionStrategies[level]?.plus(count)

    fun increaseHappenedExecutionStrategies(level: Level): Int? =
        happenedExecutionStrategies[level]?.inc()

    fun increaseHappenedOnFieldValueInfos(level: Level): Int? =
        happenedOnFieldValueInfos[level]?.inc()

    fun isLevelDispatched(level: Level): Boolean = when {
        levelsState[level] == LevelState.DISPATCHED -> true
        level.isFirst() -> happenedFetches[level] == expectedFetches[level]
        else -> {
            isLevelDispatched(level.previous()) &&
                happenedOnFieldValueInfos[level] == expectedExecutionStrategies[level] &&
                happenedExecutionStrategies[level] == expectedExecutionStrategies[level] &&
                happenedFetches[level] == expectedFetches[level]
        }
    }.also { isLevelDispatched ->
        if (isLevelDispatched) {
            levelsState.computeIfPresent(level) { _, _ -> LevelState.DISPATCHED }
        }
    }
}
