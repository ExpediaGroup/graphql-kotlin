package com.expediagroup.graphql.apq

import graphql.ExecutionInput
import graphql.execution.preparsed.PreparsedDocumentEntry
import graphql.execution.preparsed.persisted.PersistedQueryCache
import graphql.execution.preparsed.persisted.PersistedQueryCacheMiss
import graphql.execution.preparsed.persisted.PersistedQueryNotFound
import graphql.execution.preparsed.persisted.PersistedQuerySupport
import java.util.concurrent.CompletableFuture

abstract class AutomaticPersistedQueryCache : PersistedQueryCache {

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

    @Throws(PersistedQueryNotFound::class)
    override fun getPersistedQueryDocumentAsync(
        persistedQueryId: Any,
        executionInput: ExecutionInput,
        onCacheMiss: PersistedQueryCacheMiss
    ): CompletableFuture<PreparsedDocumentEntry> =
        getOrFromSupplier(persistedQueryId.toString()) {
           when {
               executionInput.query.isBlank() || executionInput.query == PersistedQuerySupport.PERSISTED_QUERY_MARKER -> {
                   throw PersistedQueryNotFound(persistedQueryId)
               }
               else -> {
                   onCacheMiss.apply(executionInput.query)
               }
           }
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
    abstract fun getOrFromSupplier(
        key: String,
        supplier: () -> PreparsedDocumentEntry
    ): CompletableFuture<PreparsedDocumentEntry>
}
