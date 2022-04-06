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

import org.dataloader.BatchLoader
import org.dataloader.DataLoader
import org.dataloader.DataLoaderOptions

/**
 * Configuration interface that will create a [DataLoader] instance
 * so we can have common logic around registering the loaders
 * by return type and loading values in the data fetchers.
 */
interface KotlinDataLoader<K, V> {
    val dataLoaderName: String
    fun getBatchLoader(): BatchLoader<K, V>
    fun getOptions(): DataLoaderOptions = DataLoaderOptions.newOptions()
}
