package com.expediagroup.graphql.dataloader

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import java.util.concurrent.ConcurrentHashMap

class Memoizer<K, V>(
    private val scope: CoroutineScope,
    private val supplier: suspend (K) -> V
) {
    private val cache = ConcurrentHashMap<K, Deferred<V>>()

    fun get(key: K): Deferred<V> = cache.computeIfAbsent(key) {
        scope.async {
            supplier(key)
        }
    }
}

class Test {
    @Test
    fun test() {
        runBlocking {
            val memoizer = Memoizer<String, String>(this) { key ->
                println("Computing value for $key")
                delay(1000) // Simulate expensive computation
                "Computed result for $key"
            }

            val keys = listOf("T1", "T2", "T1", "T2", "T1", "T3", "T2", "T3")

            keys.mapIndexed { index, key ->
                launch {
                    println("[Task $index] Requesting $key")
                    val result = memoizer.get(key).await()
                    println("[Task $index] Got result: $result")
                    // Simulate doing something with the result
                }
            }.joinAll()
        }
    }
}
