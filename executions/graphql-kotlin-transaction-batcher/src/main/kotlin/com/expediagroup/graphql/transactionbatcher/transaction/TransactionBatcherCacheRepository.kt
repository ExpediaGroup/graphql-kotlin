package com.expediagroup.graphql.transactionbatcher.transaction

import java.util.concurrent.ConcurrentHashMap

class TransactionBatcherCacheRepository {
    private val cache = ConcurrentHashMap<String, Any>()

    fun set(key: String, value: Any) = cache.put(key, value)

    fun get(key: String): Any? = cache[key]
}
