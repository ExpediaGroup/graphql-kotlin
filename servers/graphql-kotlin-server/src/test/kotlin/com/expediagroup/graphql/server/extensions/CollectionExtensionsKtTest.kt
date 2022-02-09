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
