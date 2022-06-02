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

package com.expediagroup.graphql.dataloader

import org.dataloader.CacheMap
import org.dataloader.DataLoader
import org.dataloader.DataLoaderRegistry
import org.dataloader.stats.Statistics
import java.util.concurrent.CompletableFuture
import java.util.function.Function

/**
 * Custom [DataLoaderRegistry] decorator that access the [CacheMap] of each registered [DataLoader]
 * in order to keep track of the [onDispatchFutures] when [dispatchAll] is invoked,
 * that way we can know if all dependants of the [CompletableFuture]s were executed.
 */
class KotlinDataLoaderRegistry(
    private val registry: DataLoaderRegistry = DataLoaderRegistry()
) : DataLoaderRegistry() {

    private val onDispatchFutures: MutableList<CompletableFuture<*>> = mutableListOf()

    override fun register(key: String, dataLoader: DataLoader<*, *>): DataLoaderRegistry = registry.register(key, dataLoader)
    override fun <K, V> computeIfAbsent(key: String, mappingFunction: Function<String, DataLoader<*, *>>): DataLoader<K, V> = registry.computeIfAbsent(key, mappingFunction)
    override fun combine(registry: DataLoaderRegistry): DataLoaderRegistry = this.registry.combine(registry)
    override fun getDataLoaders(): MutableList<DataLoader<*, *>> = registry.dataLoaders
    override fun getDataLoadersMap(): MutableMap<String, DataLoader<*, *>> = registry.dataLoadersMap
    override fun unregister(key: String): DataLoaderRegistry = registry.unregister(key)
    override fun <K, V> getDataLoader(key: String): DataLoader<K, V> = registry.getDataLoader(key)
    override fun getKeys(): MutableSet<String> = registry.keys
    override fun dispatchAllWithCount(): Int = registry.dispatchAllWithCount()
    override fun dispatchDepth(): Int = registry.dispatchDepth()
    override fun getStatistics(): Statistics = registry.statistics

    /**
     * will return a list of futures that represents the state of the [CompletableFuture]s from each
     * [DataLoader] cacheMap when [dispatchAll] was invoked.
     *
     * @return list of current completable futures.
     */
    fun getOnDispatchFutures(): List<CompletableFuture<*>> = onDispatchFutures

    /**
     * will return a list of futures that represents the **current** state of the [CompletableFuture]s from each
     * [DataLoader] [CacheMap].
     *
     * @return list of current completable futures.
     */
    fun getCurrentFutures(): List<CompletableFuture<*>> =
        registry.dataLoaders.map { it.cacheMap.all }.flatten()

    /**
     * This will invoke [DataLoader.dispatch] on each of the registered [DataLoader]s,
     * it will start to keep track of the [CompletableFuture]s of each [DataLoader] by adding them to
     * [onDispatchFutures]
     */
    override fun dispatchAll() {
        onDispatchFutures.clear()
        onDispatchFutures.addAll(getCurrentFutures())
        registry.dispatchAll()
    }

    /**
     * Will signal when all dependants of all [onDispatchFutures] were invoked,
     * [onDispatchFutures] is the list of all [CompletableFuture]s that will complete because the [dispatchAll]
     * method was invoked
     *
     * @return weather or not all futures gathered before [dispatchAll] were handled
     */
    fun onDispatchFuturesHandled(): Boolean =
        onDispatchFutures.all { it.numberOfDependents == 0 }

    /**
     * Will signal if more dataLoaders where invoked during the [dispatchAll] invocation
     * @return weather or not futures where loaded during [dispatchAll]
     */
    fun dataLoadersInvokedOnDispatch(): Boolean =
        getCurrentFutures().size > onDispatchFutures.size
}
