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

package com.expediagroup.graphql.server.execution.dataloader

import org.dataloader.DataLoaderFactory
import org.dataloader.DataLoaderOptions
import org.dataloader.DataLoaderRegistry

/**
 * Factory used to generate a [DataLoaderRegistry].
 */
interface DataLoaderRegistryFactory {
    /**
     * Generate [DataLoaderRegistry] to be used for GraphQL request execution.
     */
    fun generate(): DataLoaderRegistry
}

/**
 * Default [DataLoaderRegistryFactory] that generates a [DataLoaderRegistry] with all
 * the configuration provided by the [KotlinDataLoader]s.
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
