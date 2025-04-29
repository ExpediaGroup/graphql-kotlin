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

import java.util.concurrent.CompletableFuture
import java.util.concurrent.atomic.AtomicInteger

class DataLoaderDispatchState {

    // count number of loads
    private val onLoadCounter: AtomicInteger = AtomicInteger(0)
    // take snapshot of load counts when dispatchAll is invoked, then on every load complete decrease it
    private val onDispatchLoadCounter: AtomicInteger = AtomicInteger(0)

    fun takeSnapshot() {
        onDispatchLoadCounter.set(onLoadCounter.get())
        onLoadCounter.set(0)
    }

    /**
     * Will signal when all dependants of all [onDispatchFutures] were invoked,
     * [onDispatchFutures] is the list of all [CompletableFuture]s that will complete because the [dispatchAll]
     * method was invoked
     *
     * @return weather or not all futures gathered before [dispatchAll] were handled
     */
    fun onDispatchFuturesHandled(): Boolean =
        onDispatchLoadCounter.get() == 0

    /**
     * Will signal if more dataLoaders where invoked during the [dispatchAll] invocation
     * @return weather or not futures where loaded during [dispatchAll]
     */
    fun dataLoadersInvokedAfterDispatch(): Boolean =
        onLoadCounter.get() > 0

    fun onDataLoaderPromiseDispatched() {
        onLoadCounter.incrementAndGet()
    }

    fun onDataLoaderPromiseCompleted(result: Any?, t: Throwable?) {
        onDispatchLoadCounter.decrementAndGet()
    }
}
