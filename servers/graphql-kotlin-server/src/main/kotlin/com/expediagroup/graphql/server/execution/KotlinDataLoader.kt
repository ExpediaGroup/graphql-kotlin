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

import org.dataloader.BatchLoader
import org.dataloader.CacheMap
import org.dataloader.DataLoader
import java.util.concurrent.CompletableFuture

/**
 * Wrapper around the [DataLoader] class so we can have common logic around registering the loaders
 * by return type and loading values in the data fetchers.
 */
interface KotlinDataLoader<K, V> {
    val dataLoaderName: String
    fun getBatchLoader(): BatchLoader<K, V>
}

class KotlinDefaultCacheMap<K, V> : CacheMap<K, V> {

    private val cache: MutableMap<K, CompletableFuture<V>> = mutableMapOf()

    override fun containsKey(key: K): Boolean = cache.containsKey(key)

    override fun get(key: K): CompletableFuture<V>? = cache[key]

    override fun set(key: K, value: CompletableFuture<V>): CacheMap<K, V> = this.also { cache[key] = value }

    override fun delete(key: K): CacheMap<K, V> = this.also { cache.remove(key) }

    override fun clear(): CacheMap<K, V> = this.also { cache.clear() }

    fun values(): List<CompletableFuture<V>> = cache.values.toList()
}
