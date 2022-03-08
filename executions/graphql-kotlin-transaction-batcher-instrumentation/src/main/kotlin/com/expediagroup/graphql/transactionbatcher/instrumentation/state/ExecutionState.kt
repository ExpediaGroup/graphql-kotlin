package com.expediagroup.graphql.transactionbatcher.instrumentation.state

import graphql.schema.DataFetcher

enum class LevelState { NOT_DISPATCHED, DISPATCHED }

class ExecutionState(documentHeight: Int) {

    private val levelsState: MutableMap<Level, LevelState> = mutableMapOf(
        *Array(documentHeight) { number -> Level(number + 1) to LevelState.NOT_DISPATCHED }
    )

    private val expectedFetches: MutableMap<Level, Int> = mutableMapOf(
        *Array(documentHeight) { number -> Level(number + 1) to 0 }
    )
    private val happenedFetches: MutableMap<Level, Int> = mutableMapOf(
        *Array(documentHeight) { number -> Level(number + 1) to 0 }
    )

    private val expectedExecutionStrategies: MutableMap<Level, Int> = mutableMapOf(
        *Array(documentHeight) { number ->
            val level = Level(number + 1)
            level to if (level.isFirst()) 1 else 0
        }
    )
    private val happenedExecutionStrategies: MutableMap<Level, Int> = mutableMapOf(
        *Array(documentHeight) { number -> Level(number + 1) to 0 }
    )

    private val happenedOnFieldValueInfos: MutableMap<Level, Int> = mutableMapOf(
        *Array(documentHeight) { number -> Level(number + 1) to 0 }
    )

    private val manuallyCompletableDataFetchers: MutableMap<Level, MutableList<ManuallyCompletableDataFetcher>> =
        mutableMapOf(
            *Array(documentHeight) { number -> Level(number + 1) to mutableListOf() }
        )

    fun contains(level: Level): Boolean = levelsState.containsKey(level)

    fun increaseExpectedFetches(level: Level, count: Int): Int? =
        expectedFetches.computeIfPresent(level) { _, currentCount -> currentCount + count }

    fun increaseHappenedFetches(level: Level): Int? =
        happenedFetches.computeIfPresent(level) { _, currentCount -> currentCount + 1 }

    fun increaseExpectedExecutionStrategies(level: Level, count: Int): Int? =
        expectedExecutionStrategies.computeIfPresent(level) { _, currentCount -> currentCount + count }

    fun increaseHappenedExecutionStrategies(level: Level): Int? =
        happenedExecutionStrategies.computeIfPresent(level) { _, currentCount -> currentCount + 1 }

    fun increaseHappenedOnFieldValueInfos(level: Level): Int? =
        happenedOnFieldValueInfos.computeIfPresent(level) { _, currentCount -> currentCount + 1 }

    fun toManuallyCompletableDataFetcher(level: Level, dataFetcher: DataFetcher<*>): ManuallyCompletableDataFetcher =
        ManuallyCompletableDataFetcher(dataFetcher).also {
            manuallyCompletableDataFetchers[level]?.add(it)
        }

    fun isLevelDispatched(level: Level): Boolean = when {
        levelsState[level] == LevelState.DISPATCHED -> true
        level.isFirst() -> happenedFetches[level] == expectedFetches[level]
        else -> {
            val previousLevel = level.previous()
            isLevelDispatched(previousLevel) &&
                happenedOnFieldValueInfos[previousLevel] == expectedExecutionStrategies[previousLevel] &&
                happenedExecutionStrategies[level] == expectedExecutionStrategies[level] &&
                happenedFetches[level] == expectedFetches[level]
        }
    }.also { isLevelDispatched ->
        if (isLevelDispatched) {
            levelsState[level] = LevelState.DISPATCHED
        }
    }

    fun completeDataFetchers(level: Level) {
        manuallyCompletableDataFetchers[level]?.forEach(ManuallyCompletableDataFetcher::complete)
    }
}
