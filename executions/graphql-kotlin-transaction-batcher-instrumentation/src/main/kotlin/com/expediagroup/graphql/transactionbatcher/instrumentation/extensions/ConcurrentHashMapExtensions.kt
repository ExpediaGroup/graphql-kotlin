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

package com.expediagroup.graphql.transactionbatcher.instrumentation.extensions

import java.util.concurrent.ConcurrentHashMap

/**
 * if [ConcurrentHashMap] contains a given [key] execute a given [block] function while
 * holding the monitor of the given object lock
 *
 * @param key to check if there is a value associated with
 * @param block function to execute over the associated value while holding his monitor
 * @return result of [block] function that was executed over the associated values
 */
internal fun <K, V, R> ConcurrentHashMap<K, V>.synchronizeIfPresent(key: K, block: (V) -> R): R? =
    this[key]?.let { value ->
        synchronized(value) {
            block(value)
        }
    }
