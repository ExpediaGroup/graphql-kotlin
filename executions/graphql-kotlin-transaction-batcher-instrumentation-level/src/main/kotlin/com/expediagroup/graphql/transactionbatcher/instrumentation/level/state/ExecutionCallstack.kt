package com.expediagroup.graphql.transactionbatcher.instrumentation.level.state

enum class LevelState { NOT_DISPATCHED, DISPATCHED }

class ExecutionCallstack(documentHeight: Int) {

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

    fun containsLevel(level: Level): Boolean = levelsState.containsKey(level)

    fun markLevelAsDispatched(level: Level) {
        levelsState.computeIfPresent(level) { _, _ -> LevelState.DISPATCHED }
    }

    fun isLevelDispatched(level: Level): Boolean = levelsState[level] == LevelState.DISPATCHED

    fun increaseExpectedFetches(level: Level, count: Int) {
        expectedFetches.computeIfPresent(level) { _, value -> value + count }
    }

    fun increaseHappenedFetches(level: Level) {
        happenedFetches.computeIfPresent(level) { _, value -> value + 1 }
    }

    fun increaseExpectedExecutionStrategies(level: Level, count: Int) {
        expectedExecutionStrategies.computeIfPresent(level) { _, value -> value + count }
    }

    fun increaseHappenedExecutionStrategies(level: Level) {
        happenedExecutionStrategies.computeIfPresent(level) { _, value -> value + 1 }
    }

    fun increaseHappenedOnFieldValueInfos(level: Level) {
        happenedOnFieldValueInfos.computeIfPresent(level) { _, value -> value + 1 }
    }

    fun allExecutionStrategiesHappened(level: Level): Boolean =
        happenedExecutionStrategies[level] == expectedExecutionStrategies[level]

    fun allOnFieldValuesInfosHappened(level: Level): Boolean =
        happenedOnFieldValueInfos[level] == expectedExecutionStrategies[level]

    fun allFetchesHappened(level: Level): Boolean =
        happenedFetches[level] == expectedFetches[level]
}
