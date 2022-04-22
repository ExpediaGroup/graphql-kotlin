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
 * Custom [DataLoaderRegistry] decorator that has access to the [CacheMap] of each registered [DataLoader]
 * in order to keep track of the [futuresToComplete] when [dispatchAll] is invoked,
 * that way we can know if all dependants of the [CompletableFuture]s were executed.
 */
class KotlinDataLoaderRegistry(
    private val registry: DataLoaderRegistry = DataLoaderRegistry(),
    private val futureCacheMaps: List<KotlinDefaultCacheMap<*, *>> = emptyList()
) : DataLoaderRegistry() {

    private val futuresToComplete: MutableList<CompletableFuture<*>> = mutableListOf()

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
     * This will invoke [DataLoader.dispatch] on each of the registered [DataLoader]s,
     * it will start to keep track of the [CompletableFuture]s of each [DataLoader] by adding them to
     * [futuresToComplete]
     */
    override fun dispatchAll() {
        futuresToComplete.addAll(
            futureCacheMaps.map(KotlinDefaultCacheMap<*, *>::values).flatten()
        )
        registry.dispatchAll()
    }

    /**
     * will return futures that are still waiting for completion
     * @return list of completable futures that are waiting for completion
     */
    fun getFuturesToComplete(): List<CompletableFuture<*>> = futuresToComplete

    /**
     * Will signal when all dependants of all [futuresToComplete] were invoked,
     * [futuresToComplete] is the list of all [CompletableFuture]s that will complete because the [dispatchAll]
     * method was invoked
     */
    fun isDispatchedAndCompleted(): Boolean =
        futuresToComplete
            .all { it.numberOfDependents == 0 }
            .also { allFuturesCompleted ->
                if (allFuturesCompleted) {
                    futuresToComplete.clear()
                }
            }
}
