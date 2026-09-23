/*
 * Copyright 2026 Expedia, Inc
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

package com.expediagroup.graphql.dataloader.instrumentation.syncexhaustion.state

import org.junit.jupiter.api.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DataLoaderRegistryStateTest {
    private val state = DataLoaderRegistryState()

    @Test
    fun `State should start with no loads to dispatch and no dispatched loads pending`() {
        assertFalse(state.dataLoadersLoadInvokedAfterDispatchAll())
        assertTrue(state.onDispatchAllFuturesCompleted())
    }

    @Test
    fun `State should report a load invoked after the last dispatch until it is included in a snapshot`() {
        state.trackDataLoaderLoad()
        assertTrue(state.dataLoadersLoadInvokedAfterDispatchAll())
        assertTrue(state.onDispatchAllFuturesCompleted())

        state.takeSnapshot()
        assertFalse(state.dataLoadersLoadInvokedAfterDispatchAll())
        assertFalse(state.onDispatchAllFuturesCompleted())
    }

    @Test
    fun `State should complete the dispatched loads when every load included in the snapshot completes`() {
        val first = state.trackDataLoaderLoad()
        val second = state.trackDataLoaderLoad()
        state.takeSnapshot()

        state.onDataLoaderLoadCompleted(first)
        assertFalse(state.onDispatchAllFuturesCompleted())

        state.onDataLoaderLoadCompleted(second)
        assertTrue(state.onDispatchAllFuturesCompleted())
    }

    @Test
    fun `State should not be affected by a load that completes before the next snapshot`() {
        state.takeSnapshot()

        val cacheHit = state.trackDataLoaderLoad()
        state.onDataLoaderLoadCompleted(cacheHit)

        assertFalse(state.dataLoadersLoadInvokedAfterDispatchAll())
        assertTrue(state.onDispatchAllFuturesCompleted())

        state.trackDataLoaderLoad()
        assertTrue(state.dataLoadersLoadInvokedAfterDispatchAll())
        assertTrue(state.onDispatchAllFuturesCompleted())
    }

    @Test
    fun `State should keep waiting for dispatched loads when a load invoked after the snapshot completes first`() {
        val dispatched = state.trackDataLoaderLoad()
        state.takeSnapshot()

        val cacheHit = state.trackDataLoaderLoad()
        state.onDataLoaderLoadCompleted(cacheHit)
        assertFalse(state.onDispatchAllFuturesCompleted())

        state.onDataLoaderLoadCompleted(dispatched)
        assertTrue(state.onDispatchAllFuturesCompleted())
    }

    @Test
    fun `State should decrease the snapshot a load was included in even when newer loads were invoked`() {
        val dispatched = state.trackDataLoaderLoad()
        state.takeSnapshot()
        val pending = state.trackDataLoaderLoad()

        state.onDataLoaderLoadCompleted(dispatched)
        assertTrue(state.onDispatchAllFuturesCompleted())
        assertTrue(state.dataLoadersLoadInvokedAfterDispatchAll())

        state.onDataLoaderLoadCompleted(pending)
        assertFalse(state.dataLoadersLoadInvokedAfterDispatchAll())
    }

    @Test
    @Suppress("DEPRECATION")
    fun `State should keep the previous behavior for the deprecated load callbacks`() {
        state.onDataLoaderLoadDispatched()
        assertTrue(state.dataLoadersLoadInvokedAfterDispatchAll())

        state.takeSnapshot()
        assertFalse(state.onDispatchAllFuturesCompleted())

        state.onDataLoaderLoadCompleted()
        assertTrue(state.onDispatchAllFuturesCompleted())
    }
}
