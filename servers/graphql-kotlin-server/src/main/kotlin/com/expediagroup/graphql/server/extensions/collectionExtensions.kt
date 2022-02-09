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

package com.expediagroup.graphql.server.extensions

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.supervisorScope

suspend fun <A, B> Iterable<A>.concurrentMap(
    transform: suspend (A) -> B,
    fallback: (A, exception: Exception) -> B
): List<B> = supervisorScope {
    map { item ->
        async {
            try {
                transform(item)
            } catch (e: Exception) {
                fallback(item, e)
            }
        }
    }.awaitAll()
}
