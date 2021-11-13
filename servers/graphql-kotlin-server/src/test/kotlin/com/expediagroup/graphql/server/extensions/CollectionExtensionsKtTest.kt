package com.expediagroup.graphql.server.extensions

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import kotlin.system.measureTimeMillis
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CollectionExtensionsKtTest {
    @Test
    fun `verify concurrentMap is concurrently executed and recovers from errors with fallback`() {
        val time = measureTimeMillis {
            val output = runBlocking {
                (1..100).concurrentMap(
                    {
                        delay(1000)
                        when (it) {
                            99 -> throw Exception("error!")
                            else -> it * 2
                        }
                    },
                    { item: Int, _: Throwable -> item * 100 }
                )
            }
            assertEquals(output[98], 9900)
        }
        println("time (ms): $time")
        assertTrue(time < 3000)
    }
}
