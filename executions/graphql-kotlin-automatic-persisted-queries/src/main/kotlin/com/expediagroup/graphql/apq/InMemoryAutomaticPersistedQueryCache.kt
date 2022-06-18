package com.expediagroup.graphql.apq

import graphql.execution.preparsed.PreparsedDocumentEntry
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap

class InMemoryAutomaticPersistedQueryCache : AutomaticPersistedQueryCache() {

    private val cache: ConcurrentHashMap<Any, PreparsedDocumentEntry> = ConcurrentHashMap()

    override fun getOrFromSupplier(
        key: String,
        supplier: () -> PreparsedDocumentEntry
    ): CompletableFuture<PreparsedDocumentEntry> =
        cache[key]?.let { entry ->
            CompletableFuture.completedFuture(entry)
        } ?: run {
            val entry = supplier.invoke()
            cache[key] = entry
            CompletableFuture.completedFuture(supplier.invoke())
        }
}
