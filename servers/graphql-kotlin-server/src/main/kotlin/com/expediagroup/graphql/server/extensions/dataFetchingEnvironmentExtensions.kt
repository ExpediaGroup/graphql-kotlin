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

package com.expediagroup.graphql.server.extensions

import com.expediagroup.graphql.server.exception.MissingDataLoaderException
import graphql.schema.DataFetchingEnvironment
import org.dataloader.DataLoader
import java.util.concurrent.CompletableFuture

/**
 * Helper method to get a value from a registered DataLoader.
 * The provided key should be the cache key object used to save the value for that particular data loader.
 */
fun <K, V> DataFetchingEnvironment.getValueFromDataLoader(dataLoaderName: String, key: K): CompletableFuture<V> {
    val loader = getLoaderName<K, V>(dataLoaderName)
    return loader.load(key, this.getContext())
}

/**
* Helper method to get values from a registered DataLoader.
*/
fun <K, V> DataFetchingEnvironment.getValuesFromDataLoader(dataLoaderName: String, keys: List<K>): CompletableFuture<List<V>> {
    val loader = getLoaderName<K, V>(dataLoaderName)
    return loader.loadMany(keys, listOf(this.getContext()))
}

private fun <K, V> DataFetchingEnvironment.getLoaderName(dataLoaderName: String) =
    this.getDataLoader<K, V>(dataLoaderName) ?: throw MissingDataLoaderException(dataLoaderName)
