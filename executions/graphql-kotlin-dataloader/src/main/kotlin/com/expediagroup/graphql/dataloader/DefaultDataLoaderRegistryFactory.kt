package com.expediagroup.graphql.dataloader

import org.dataloader.DataLoaderFactory
import org.dataloader.DataLoaderRegistry

/**
 * Default [DataLoaderRegistryFactory] that generates a [KotlinDataLoaderRegistry] with all
 * the configuration provided by the [KotlinDataLoader]s.
 */
class DefaultDataLoaderRegistryFactory(
    private val dataLoaders: List<KotlinDataLoader<*, *>>
) : DataLoaderRegistryFactory {

    constructor(vararg dataLoaders: KotlinDataLoader<*, *>) : this(dataLoaders.toList())

    override fun generate(): KotlinDataLoaderRegistry {
        val futureCacheMaps = mutableListOf<KotlinDefaultCacheMap<*, *>>()

        val registry = DataLoaderRegistry()
        dataLoaders.forEach { dataLoader ->
            val options = dataLoader.getOptions()

            // override DefaultCacheMap
            if (options.cachingEnabled() && options.cacheMap().isEmpty) {
                val futureCacheMap = KotlinDefaultCacheMap<Any?, Any?>()
                options.setCacheMap(futureCacheMap)
                futureCacheMaps += futureCacheMap
            }

            registry.register(
                dataLoader.dataLoaderName,
                DataLoaderFactory.newDataLoader(dataLoader.getBatchLoader(), options)
            )
        }
        return KotlinDataLoaderRegistry(registry, futureCacheMaps)
    }
}
