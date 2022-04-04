/*
 * Copyright 2021 Expedia, Inc
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

package com.expediagroup.graphql.server.execution

import org.dataloader.DataLoader
import org.dataloader.DataLoaderFactory
import org.dataloader.DataLoaderOptions
import org.dataloader.DataLoaderRegistry
import org.dataloader.stats.Statistics
import java.util.concurrent.CompletableFuture
import java.util.function.Function

/**
 * Factory used to generate [DataLoaderRegistry] per GraphQL execution.
 */
interface DataLoaderRegistryFactory {
    /**
     * Generate [DataLoaderRegistry] to be used for GraphQL request execution.
     */
    fun generate(): DataLoaderRegistry
}

class KotlinDataLoaderRegistry(
    private val registry: DataLoaderRegistry,
    private val cacheMaps: List<KotlinDefaultCacheMap<*, *>>
) : DataLoaderRegistry() {

    private val futuresToComplete: MutableList<CompletableFuture<*>> = mutableListOf()

    override fun register(key: String, dataLoader: DataLoader<*, *>): DataLoaderRegistry = registry.register(key, dataLoader)
    override fun <K : Any, V : Any> computeIfAbsent(key: String, mappingFunction: Function<String, DataLoader<*, *>>): DataLoader<K, V> = registry.computeIfAbsent(key, mappingFunction)
    override fun combine(registry: DataLoaderRegistry): DataLoaderRegistry = this.registry.combine(registry)
    override fun getDataLoaders(): MutableList<DataLoader<*, *>> = registry.dataLoaders
    override fun getDataLoadersMap(): MutableMap<String, DataLoader<*, *>> = registry.dataLoadersMap
    override fun unregister(key: String): DataLoaderRegistry = registry.unregister(key)
    override fun <K : Any, V : Any> getDataLoader(key: String?): DataLoader<K, V> = registry.getDataLoader(key)
    override fun getKeys(): MutableSet<String> = registry.keys

    override fun dispatchAll() {
        futuresToComplete.addAll(
            cacheMaps.map(KotlinDefaultCacheMap<*, *>::values).flatten()
        )
        registry.dispatchAll()
    }

    override fun dispatchAllWithCount(): Int = registry.dispatchAllWithCount()
    override fun dispatchDepth(): Int = registry.dispatchDepth()
    override fun getStatistics(): Statistics = registry.statistics

    fun isDispatchCompleted(): Boolean = this.futuresToComplete.all {
        it.numberOfDependents == 0
    }
}

/**
 * Default [DataLoaderRegistryFactory] that generates a [DataLoaderRegistry] with all the names from the [KotlinDataLoader]s.
 */
class DefaultDataLoaderRegistryFactory(
    private val dataLoaders: List<KotlinDataLoader<*, *>>
) : DataLoaderRegistryFactory {
    override fun generate(): KotlinDataLoaderRegistry {
        val cacheMaps = mutableListOf<KotlinDefaultCacheMap<*, *>>()
        val registry = DataLoaderRegistry()
        dataLoaders.forEach { dataLoader ->
            val cacheMap = KotlinDefaultCacheMap<Any?, Any?>()
            cacheMaps.add(cacheMap)
            registry.register(
                dataLoader.dataLoaderName,
                DataLoaderFactory.newDataLoader(
                    dataLoader.getBatchLoader(),
                    DataLoaderOptions().setCacheMap(cacheMap)
                )
            )
        }
        return KotlinDataLoaderRegistry(registry, cacheMaps)
    }
}
