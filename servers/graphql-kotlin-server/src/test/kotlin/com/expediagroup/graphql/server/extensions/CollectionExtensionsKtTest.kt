package com.expediagroup.graphql.server.extensions

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class CollectionExtensionsKtTest {
    @Test
    fun `verify concurrentMap is concurrently executed and recovers from errors with fallback`() {
        val output = runBlocking {
            (1..100).concurrentMap(
                { number ->
                    delay(1000)
                    when (number) {
                        99 -> throw Exception("error!")
                        else -> number * 2
                    }
                },
                { item: Int, _: Throwable -> item * 100 }
            )
        }
        assertEquals(output[98], 9900)
    }
}
