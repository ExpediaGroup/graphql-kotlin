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

package com.expediagroup.graphql.dataloader.instrumentation.syncexhaustion

import com.expediagroup.graphql.dataloader.instrumentation.syncexhaustion.state.SyncExecutionExhaustedState
import io.mockk.mockk
import org.dataloader.DataLoader
import org.junit.jupiter.api.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DataLoaderSyncExecutionExhaustedDataLoaderDispatcherTest {
    private val syncExecutionExhaustedState = SyncExecutionExhaustedState(1) { error("no execution started, registry should not be dispatched") }
    private val dispatcher = DataLoaderSyncExecutionExhaustedDataLoaderDispatcher(syncExecutionExhaustedState)
    private val dataLoader = mockk<DataLoader<*, *>>()

    @Test
    fun `Dispatcher should track a load from dispatch until completion`() {
        val load = dispatcher.beginLoad(dataLoader, "a", null)

        load.onDispatched()
        assertTrue(syncExecutionExhaustedState.dataLoadersLoadInvokedAfterDispatchAll())

        load.onCompleted("value-a", null)
        assertFalse(syncExecutionExhaustedState.dataLoadersLoadInvokedAfterDispatchAll())
    }

    @Test
    fun `Dispatcher should decrease a load only once when its completion is notified twice`() {
        val load = dispatcher.beginLoad(dataLoader, "a", null)
        load.onDispatched()
        load.onCompleted("value-a", null)
        load.onCompleted("value-a", null)

        dispatcher.beginLoad(dataLoader, "b", null).onDispatched()

        assertTrue(syncExecutionExhaustedState.dataLoadersLoadInvokedAfterDispatchAll())
    }

    @Test
    fun `Dispatcher should ignore the completion of a load that was never dispatched`() {
        dispatcher.beginLoad(dataLoader, "a", null).onCompleted(null, IllegalStateException("load failed"))

        dispatcher.beginLoad(dataLoader, "b", null).onDispatched()

        assertTrue(syncExecutionExhaustedState.dataLoadersLoadInvokedAfterDispatchAll())
    }
}
