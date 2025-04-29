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

import com.expediagroup.graphql.dataloader.instrumentation.syncexhaustion.state.SyncExecutionExhaustedState
import org.dataloader.DataLoader
import org.dataloader.instrumentation.DataLoaderInstrumentation
import org.dataloader.instrumentation.DataLoaderInstrumentationContext

class DataLoaderSyncExecutionExhaustedDataLoaderDispatcher(
    private val syncExecutionExhaustedState: SyncExecutionExhaustedState
): DataLoaderInstrumentation {

    private val contextForSyncExecutionExhausted: DataLoaderInstrumentationContext<Any?> =
        object: DataLoaderInstrumentationContext<Any?> {
            override fun onDispatched() {
                syncExecutionExhaustedState.onDataLoaderPromiseDispatched()
            }
            override fun onCompleted(result: Any?, t: Throwable?) {
                syncExecutionExhaustedState.onDataLoaderPromiseCompleted(result, t)
            }
        }

    override fun beginLoad(
        dataLoader: DataLoader<*, *>,
        key: Any,
        loadContext: Any?
    ): DataLoaderInstrumentationContext<Any?> =
        contextForSyncExecutionExhausted
}
