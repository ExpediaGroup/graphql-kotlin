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

package com.expediagroup.graphql.dataloader

import org.dataloader.DataLoader
import org.dataloader.DataLoaderFactory
import org.junit.jupiter.api.Test
import reactor.kotlin.core.publisher.toFlux
import java.time.Duration
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class KotlinDataLoaderRegistryTest {
    @Test
    fun `Decorator will keep track of DataLoaders futures`() {
        val stringToUpperCaseDataLoader: KotlinDataLoader<String, String> = object : KotlinDataLoader<String, String> {
            override val dataLoaderName: String = "ToUppercaseDataLoader"
            override fun getDataLoader(): DataLoader<String, String> = DataLoaderFactory.newDataLoader { keys ->
                keys.toFlux().map(String::uppercase).collectList().delayElement(Duration.ofMillis(300)).toFuture()
            }
        }

        val stringToLowerCaseDataLoader: KotlinDataLoader<String, String> = object : KotlinDataLoader<String, String> {
            override val dataLoaderName: String = "ToLowercaseDataLoader"
            override fun getDataLoader(): DataLoader<String, String> = DataLoaderFactory.newDataLoader { keys ->
                keys.toFlux().map(String::lowercase).collectList().delayElement(Duration.ofMillis(300)).toFuture()
            }
        }

        val registry = KotlinDataLoaderRegistryFactory(
            stringToUpperCaseDataLoader, stringToLowerCaseDataLoader
        ).generate()

        registry.getDataLoader<String, String>("ToUppercaseDataLoader").load("touppercase1").handle { _, _ -> }
        registry.getDataLoader<String, String>("ToUppercaseDataLoader").load("touppercase2").handle { _, _ -> }
        registry.getDataLoader<String, String>("ToUppercaseDataLoader").load("touppercase1").handle { _, _ -> }

        registry.getDataLoader<String, String>("ToLowercaseDataLoader").load("TOLOWERCASE1").handle { _, _ -> }
        registry.getDataLoader<String, String>("ToLowercaseDataLoader").load("TOLOWERCASE2").handle { _, _ -> }
        registry.getDataLoader<String, String>("ToLowercaseDataLoader").load("TOLOWERCASE1").handle { _, _ -> }

        val futuresToComplete = registry.getCurrentFutures()
        assertEquals(4, futuresToComplete.size)

        assertEquals(2, futuresToComplete[0].numberOfDependents) // 2 dependants of touppercase1
        assertEquals(1, futuresToComplete[1].numberOfDependents) // 1 dependant of touppercase2

        assertEquals(2, futuresToComplete[2].numberOfDependents) // 2 dependants of TOLOWERCASE1
        assertEquals(1, futuresToComplete[3].numberOfDependents) // 1 dependant of TOLOWERCASE2

        registry.dispatchAll()
        assertFalse(registry.onDispatchFuturesHandled())

        Thread.sleep(500)
        assertTrue(registry.onDispatchFuturesHandled())
    }
}
