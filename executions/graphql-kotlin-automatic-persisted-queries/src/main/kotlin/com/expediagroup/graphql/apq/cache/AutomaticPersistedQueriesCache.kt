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

package com.expediagroup.graphql.apq.cache

import graphql.ExecutionInput
import graphql.execution.preparsed.PreparsedDocumentEntry
import graphql.execution.preparsed.persisted.PersistedQueryCache
import graphql.execution.preparsed.persisted.PersistedQueryCacheMiss
import java.util.concurrent.CompletableFuture

interface AutomaticPersistedQueriesCache : PersistedQueryCache {

    @Deprecated(
        message = "deprecated in favor of async retrieval of PreparsedDocumentEntry",
        replaceWith = ReplaceWith("getPersistedQueryDocumentAsync(persistedQueryId, executionInput, onCacheMiss)")
    )
    override fun getPersistedQueryDocument(
        persistedQueryId: Any,
        executionInput: ExecutionInput,
        onCacheMiss: PersistedQueryCacheMiss
    ): PreparsedDocumentEntry =
        getPersistedQueryDocumentAsync(persistedQueryId, executionInput, onCacheMiss).get()

    override fun getPersistedQueryDocumentAsync(
        persistedQueryId: Any,
        executionInput: ExecutionInput,
        onCacheMiss: PersistedQueryCacheMiss
    ): CompletableFuture<PreparsedDocumentEntry> =
        getOrElse(persistedQueryId.toString()) {
            onCacheMiss.apply(executionInput.query)
        }

    /**
     * Get the [PreparsedDocumentEntry] associated with the [key] from the cache.
     *
     * If the [PreparsedDocumentEntry] is missing in the cache, the [supplier] will provide one,
     * and then it should be added to the cache.
     *
     * @param key The hash of the requested query.
     * @param supplier that will provide the document in case there is a cache miss.
     */
    fun getOrElse(
        key: String,
        supplier: () -> PreparsedDocumentEntry
    ): CompletableFuture<PreparsedDocumentEntry>
}
