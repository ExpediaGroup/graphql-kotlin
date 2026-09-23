/*
 * Copyright 2026 Expedia, Inc
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

import org.dataloader.DataLoader
import org.dataloader.DataLoaderFactory
import org.dataloader.DataLoaderRegistry
import org.dataloader.DelegatingDataLoader
import org.dataloader.instrumentation.DataLoaderInstrumentation
import java.util.concurrent.CompletableFuture
import java.util.function.Consumer

/**
 *
 * [java-data-loader 6](https://github.com/graphql-java/java-dataloader/pull/241) removed synchronization from [DataLoader.load],
 * allowing concurrent cache misses to invoke the loader more than once before either result future is cached.
 */
internal class SynchronizedDataLoader<K : Any, V : Any>(
    delegate: DataLoader<K, V>
) : DelegatingDataLoader<K, V>(delegate) {
    override fun load(key: K): CompletableFuture<V> =
        synchronized(this) {
            super.load(key)
        }

    override fun load(key: K, keyContext: Any?): CompletableFuture<V> =
        synchronized(this) {
            super.load(key, keyContext)
        }

    override fun loadMany(keys: List<K>): CompletableFuture<List<V>> =
        synchronized(this) {
            super.loadMany(keys)
        }

    override fun loadMany(keys: List<K>, keyContexts: List<Any>): CompletableFuture<List<V>> =
        synchronized(this) {
            super.loadMany(keys, keyContexts)
        }

    override fun loadMany(keysAndContexts: Map<K, *>): CompletableFuture<Map<K, V>> =
        synchronized(this) {
            super.loadMany(keysAndContexts)
        }

    /**
     * [DataLoaderRegistry] transforms every registered [DataLoader] to add its name and [DataLoaderInstrumentation],
     * the transformed [DataLoader] needs to be synchronized as well
     */
    override fun transform(builderConsumer: Consumer<DataLoaderFactory.Builder<K, V>>): DataLoader<K, V> =
        SynchronizedDataLoader(super.transform(builderConsumer))
}
