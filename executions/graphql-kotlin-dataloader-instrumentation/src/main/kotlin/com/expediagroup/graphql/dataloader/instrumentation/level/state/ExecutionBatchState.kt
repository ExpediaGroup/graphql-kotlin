/*
 * Copyright 2022 Expedia, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.expediagroup.graphql.dataloader.instrumentation.level.state

import graphql.schema.DataFetcher

enum class LevelState { NOT_DISPATCHED, DISPATCHED }

/**
 * Handle the state of an [graphql.ExecutionInput]
 */
class ExecutionBatchState(documentHeight: Int) {

    private val levelsState: MutableMap<Level, LevelState> = mutableMapOf(
        *Array(documentHeight) { number -> Level(number + 1) to LevelState.NOT_DISPATCHED }
    )

    private val expectedFetches: MutableMap<Level, Int> = mutableMapOf(
        *Array(documentHeight) { number -> Level(number + 1) to 0 }
    )
    private val dispatchedFetches: MutableMap<Level, Int> = mutableMapOf(
        *Array(documentHeight) { number -> Level(number + 1) to 0 }
    )

    private val expectedExecutionStrategies: MutableMap<Level, Int> = mutableMapOf(
        *Array(documentHeight) { number ->
            val level = Level(number + 1)
            level to if (level.isFirst()) 1 else 0
        }
    )
    private val dispatchedExecutionStrategies: MutableMap<Level, Int> = mutableMapOf(
        *Array(documentHeight) { number -> Level(number + 1) to 0 }
    )

    private val onFieldValueInfos: MutableMap<Level, Int> = mutableMapOf(
        *Array(documentHeight) { number -> Level(number + 1) to 0 }
    )

    private val manuallyCompletableDataFetchers: MutableMap<Level, MutableList<ManuallyCompletableDataFetcher>> =
        mutableMapOf(
            *Array(documentHeight) { number -> Level(number + 1) to mutableListOf() }
        )

    /**
     * Check if the [ExecutionBatchState] contains a level
     *
     * @param level to check if his state is being calculated
     * @return whether or not state contains the level
     */
    fun contains(level: Level): Boolean = levelsState.containsKey(level)

    /**
     * Increase fetches that this [ExecutionBatchState] is expecting
     *
     * @param level which level expects [count] of fetches
     * @param count how many more fetches the [level] will expect
     * @return total expected fetches
     */
    fun increaseExpectedFetches(level: Level, count: Int): Int? =
        expectedFetches.computeIfPresent(level) { _, currentCount -> currentCount + count }

    /**
     * Increase dispatched fetches of this [ExecutionBatchState]
     *
     * @param level which level should increase dispatched fetches
     * @return total dispatched fetches
     */
    fun increaseDispatchedFetches(level: Level): Int? =
        dispatchedFetches.computeIfPresent(level) { _, currentCount -> currentCount + 1 }

    /**
     * Increase executionStrategies that this [ExecutionBatchState] is expecting
     *
     * @param level which level expects [count] of fetches
     * @param count how many more executionStrategies the [level] will expect
     * @return total expected executionStrategies
     */
    fun increaseExpectedExecutionStrategies(level: Level, count: Int): Int? =
        expectedExecutionStrategies.computeIfPresent(level) { _, currentCount -> currentCount + count }

    /**
     * Increase dispatched executionStrategies of this [ExecutionBatchState]
     *
     * @param level which level should increase dispatched fetches
     * @return total dispatched executionStrategies
     */
    fun increaseDispatchedExecutionStrategies(level: Level): Int? =
        dispatchedExecutionStrategies.computeIfPresent(level) { _, currentCount -> currentCount + 1 }

    /**
     * Increase OnFieldValueInfos invocations of this [ExecutionBatchState]
     *
     * @param level which level should increase OnFieldValueInfos invocations
     * @return total onFieldValueInfos invocations
     */
    fun increaseOnFieldValueInfos(level: Level): Int? =
        onFieldValueInfos.computeIfPresent(level) { _, currentCount -> currentCount + 1 }

    /**
     * Instrument a dataFetcher to modify his runtime behavior to manually complete the returned CompletableFuture
     *
     * @param level which level the [dataFetcher] belongs
     * @param dataFetcher to be instrumented
     * @return instrumented dataFetcher
     */
    fun toManuallyCompletableDataFetcher(level: Level, dataFetcher: DataFetcher<*>): ManuallyCompletableDataFetcher =
        ManuallyCompletableDataFetcher(dataFetcher).also { manuallyCompletableDataFetchers[level]?.add(it) }

    /**
     * Complete all the [manuallyCompletableDataFetchers]
     *
     * @param level which level should complete dataFetchers
     */
    fun completeDataFetchers(level: Level) {
        manuallyCompletableDataFetchers[level]?.forEach(ManuallyCompletableDataFetcher::complete)
    }

    /**
     * Check if a given level is dispatched
     *
     * @param level which level check if its dispatched
     */
    fun isLevelDispatched(level: Level): Boolean = when {
        levelsState[level] == LevelState.DISPATCHED -> true
        level.isFirst() -> dispatchedFetches[level] == expectedFetches[level]
        else -> {
            val previousLevel = level.previous()
            isLevelDispatched(previousLevel) &&
                onFieldValueInfos[previousLevel] == expectedExecutionStrategies[previousLevel] &&
                dispatchedExecutionStrategies[level] == expectedExecutionStrategies[level] &&
                dispatchedFetches[level] == expectedFetches[level]
        }
    }.also { isLevelDispatched ->
        if (isLevelDispatched) {
            levelsState[level] = LevelState.DISPATCHED
        }
    }
}
