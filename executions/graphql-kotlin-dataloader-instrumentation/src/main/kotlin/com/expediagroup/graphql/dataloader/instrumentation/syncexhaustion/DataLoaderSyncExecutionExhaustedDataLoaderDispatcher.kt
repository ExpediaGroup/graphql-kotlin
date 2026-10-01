/*
 * Copyright 2025 Expedia, Inc
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

import com.expediagroup.graphql.dataloader.instrumentation.syncexhaustion.state.DataLoaderRegistryState
import com.expediagroup.graphql.dataloader.instrumentation.syncexhaustion.state.SyncExecutionExhaustedState
import org.dataloader.DataLoader
import org.dataloader.instrumentation.DataLoaderInstrumentation
import org.dataloader.instrumentation.DataLoaderInstrumentationContext
import java.util.concurrent.atomic.AtomicInteger

/**
 * Custom [DataLoaderInstrumentation] implementation that helps to calculate the state of [DataLoader]s in the
 * [DataLoaderRegistryState] that lives inside the [syncExecutionExhaustedState]
 */
class DataLoaderSyncExecutionExhaustedDataLoaderDispatcher(
    private val syncExecutionExhaustedState: SyncExecutionExhaustedState
) : DataLoaderInstrumentation {
    override fun beginLoad(
        dataLoader: DataLoader<*, *>,
        key: Any,
        loadContext: Any?
    ): DataLoaderInstrumentationContext<Any?> =
        object : DataLoaderInstrumentationContext<Any?> {
            /**
             * Counter the load was added to, cleared on completion so the load is only decreased once
             */
            private var loadCounter: AtomicInteger? = null

            override fun onDispatched() {
                loadCounter = syncExecutionExhaustedState.trackDataLoaderLoad()
            }
            override fun onCompleted(result: Any?, t: Throwable?) {
                loadCounter?.let(syncExecutionExhaustedState::onDataLoaderLoadCompleted)
                loadCounter = null
            }
        }
}
