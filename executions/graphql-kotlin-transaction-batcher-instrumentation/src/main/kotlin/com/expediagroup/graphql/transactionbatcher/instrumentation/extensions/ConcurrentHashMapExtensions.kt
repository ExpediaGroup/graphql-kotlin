package com.expediagroup.graphql.transactionbatcher.instrumentation.extensions

import java.util.concurrent.ConcurrentHashMap

fun <K, V, R> ConcurrentHashMap<K, V>.synchronizeIfPresent(key: K, block: (V) -> R): R? =
    this[key]?.let { value ->
        synchronized(value) {
            block(value)
        }
    }
