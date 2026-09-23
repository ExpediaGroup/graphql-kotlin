/*
 * Copyright 2025 Expedia, Inc
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

package com.expediagroup.graphql.dataloader.instrumentation.syncexhaustion.state

import org.dataloader.DataLoader
import org.dataloader.DataLoaderRegistry
import java.util.concurrent.CompletableFuture
import java.util.concurrent.atomic.AtomicInteger

/**
 * Calculate the state of [DataLoader.load]s in a [DataLoaderRegistry]
 */
class DataLoaderRegistryState {
    /**
     * Count [DataLoader.load] invocations that are not completed yet and were invoked
     * after the last [DataLoaderRegistry.dispatchAll]
     */
    @Volatile
    private var loadCounter = AtomicInteger(0)

    /**
     * Count [DataLoader.load] invocations that are not completed yet and were invoked
     * before the last [DataLoaderRegistry.dispatchAll]
     */
    @Volatile
    private var onDispatchAllLoadCounter = AtomicInteger(0)

    /**
     * Take snapshot of [loadCounter] when [DataLoaderRegistry.dispatchAll] is invoked,
     * loads tracked by [trackDataLoaderLoad] before the snapshot will decrease [onDispatchAllLoadCounter] when completed
     */
    @Synchronized
    fun takeSnapshot() {
        onDispatchAllLoadCounter = loadCounter
        loadCounter = AtomicInteger(0)
    }

    /**
     * @return if all [CompletableFuture]s returned by [DataLoader.load] were completed
     */
    fun onDispatchAllFuturesCompleted(): Boolean =
        onDispatchAllLoadCounter.get() == 0

    /**
     * @return If more [DataLoader.load] where invoked after the [DataLoaderRegistry.dispatchAll] invocation
     */
    fun dataLoadersLoadInvokedAfterDispatchAll(): Boolean =
        loadCounter.get() > 0

    /**
     * Increase [loadCounter] when [DataLoader.load] is invoked
     */
    @Deprecated(
        "Loads are tracked by SyncExecutionExhaustedState through DataLoaderSyncExecutionExhaustedDataLoaderDispatcher, " +
            "register it with KotlinDataLoaderRegistryFactory.generate instead of calling this method directly. " +
            "Will be removed in the next major version."
    )
    fun onDataLoaderLoadDispatched() {
        loadCounter.incrementAndGet()
    }

    /**
     * Decrease [onDispatchAllLoadCounter] when [DataLoader.load] returned [CompletableFuture] completes
     */
    @Deprecated(
        "Loads are tracked by SyncExecutionExhaustedState through DataLoaderSyncExecutionExhaustedDataLoaderDispatcher, " +
            "register it with KotlinDataLoaderRegistryFactory.generate instead of calling this method directly. " +
            "This method decreases onDispatchAllLoadCounter even for loads that were not included in the last snapshot. " +
            "Will be removed in the next major version."
    )
    fun onDataLoaderLoadCompleted() {
        onDispatchAllLoadCounter.decrementAndGet()
    }

    /**
     * Increase [loadCounter] when [DataLoader.load] is invoked
     *
     * @return the counter that the load was added to, it needs to be provided to [onDataLoaderLoadCompleted]
     * when the [CompletableFuture] returned by [DataLoader.load] completes
     */
    @Synchronized
    internal fun trackDataLoaderLoad(): AtomicInteger =
        loadCounter.also(AtomicInteger::incrementAndGet)

    /**
     * Decrease the counter that the load was added to when [DataLoader.load] returned [CompletableFuture] completes,
     * a load that completes before the next snapshot, for example a cache hit on a completed [CompletableFuture],
     * will decrease [loadCounter] instead of [onDispatchAllLoadCounter]
     *
     * @param loadCounter the counter returned by [trackDataLoaderLoad]
     */
    internal fun onDataLoaderLoadCompleted(loadCounter: AtomicInteger) {
        loadCounter.decrementAndGet()
    }
}
