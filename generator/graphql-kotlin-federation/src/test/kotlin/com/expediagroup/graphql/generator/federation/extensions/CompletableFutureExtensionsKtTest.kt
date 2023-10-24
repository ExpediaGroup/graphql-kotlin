/*
 * Copyright 2023 Expedia, Inc
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

package com.expediagroup.graphql.generator.federation.extensions

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.future.asCompletableFuture
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import reactor.kotlin.core.publisher.toMono
import java.time.Duration
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionException
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

@OptIn(DelicateCoroutinesApi::class)
class CompletableFutureExtensionsKtTest {
    @Test
    fun `joinAll asynchronously collects a list of completable futures of elements into a completable future list of elements`() {
        val firstPromise = "first promise".toMono().delayElement(Duration.ofMillis(500)).toFuture()
        val secondPromise = GlobalScope.async {
            delay(400)
            "second promise"
        }.asCompletableFuture()
        val thirdPromise = CompletableFuture.completedFuture("third promise")

        val result = listOf(firstPromise, secondPromise, thirdPromise).joinAll().join()

        assertEquals(3, result.size)
        assertEquals("first promise", result[0])
        assertEquals("second promise", result[1])
        assertEquals("third promise", result[2])
    }

    @Test
    fun `joinAll throws an exception with a completableFuture completes exceptionally`() {
        val firstPromise = "first promise".toMono().delayElement(Duration.ofMillis(500)).toFuture()
        val secondPromise = GlobalScope.async {
            delay(400)
            "second promise"
        }.asCompletableFuture()
        val thirdPromise = CompletableFuture.supplyAsync {
            throw Exception("async exception")
        }

        assertThrows<CompletionException> {
            listOf(firstPromise, secondPromise, thirdPromise).joinAll().join()
        }
    }

    @Test
    fun `allSettled asynchronously collects a list of completable futures of elements into a completable future list of elements`() {
        val firstPromise = "first promise".toMono().delayElement(Duration.ofMillis(500)).toFuture()
        val secondPromise = GlobalScope.async {
            delay(400)
            "second promise"
        }.asCompletableFuture()
        val thirdPromise = CompletableFuture.completedFuture("third promise")

        val result = listOf(firstPromise, secondPromise, thirdPromise).allSettled().join()

        assertEquals(3, result.size)
        assertEquals("first promise", result[0].getOrNull())
        assertEquals("second promise", result[1].getOrNull())
        assertEquals("third promise", result[2].getOrNull())
    }

    @Test
    fun `allSettled asynchronously collects a list of completable futures of elements even if a completable future completes exceptionally`() {
        val firstPromise = "first promise".toMono().delayElement(Duration.ofMillis(500)).toFuture()
        val secondPromise = GlobalScope.async {
            delay(400)
            "second promise"
        }.asCompletableFuture()
        val thirdPromise = CompletableFuture.supplyAsync {
            throw Exception("async exception")
        }

        val result = listOf(firstPromise, secondPromise, thirdPromise).allSettled().join()

        assertEquals(3, result.size)
        assertEquals("first promise", result[0].getOrNull())
        assertEquals("second promise", result[1].getOrNull())
        assertTrue(result[2].isFailure)
        assertIs<CompletionException>(result[2].exceptionOrNull())
        assertEquals("async exception", result[2].exceptionOrNull()?.cause?.message)
    }
}
