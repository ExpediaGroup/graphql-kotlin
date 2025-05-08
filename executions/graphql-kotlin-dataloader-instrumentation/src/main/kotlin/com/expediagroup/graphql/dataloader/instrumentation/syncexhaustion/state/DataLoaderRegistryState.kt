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
     * Count [DataLoader.load] invocations
     */
    private val loadCounter = AtomicInteger(0)

    /**
     * Snapshot of [loadCounter] when [DataLoaderRegistry.dispatchAll] is invoked,
     * then on every load complete decrease it
     */
    private val onDispatchAllLoadCounter = AtomicInteger(0)

    /**
     * Take snapshot of [loadCounter] when [DataLoaderRegistry.dispatchAll] is invoked
     */
    fun takeSnapshot() {
        onDispatchAllLoadCounter.set(loadCounter.get())
        loadCounter.set(0)
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
    fun onDataLoaderLoadDispatched() {
        loadCounter.incrementAndGet()
    }

    /**
     * Decrease [onDispatchAllLoadCounter] when [DataLoader.load] returned [CompletableFuture] completes
     */
    fun onDataLoaderLoadCompleted() {
        onDispatchAllLoadCounter.decrementAndGet()
    }
}
