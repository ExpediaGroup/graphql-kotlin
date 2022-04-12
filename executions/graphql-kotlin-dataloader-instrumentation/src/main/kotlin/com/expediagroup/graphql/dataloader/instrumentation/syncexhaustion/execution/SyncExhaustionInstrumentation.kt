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

package com.expediagroup.graphql.dataloader.instrumentation.syncexhaustion.execution

import graphql.ExecutionInput

/**
 * Defines the contract for the behavior that needs to be executed when sync execution is exhausted
 */
interface SyncExhaustionInstrumentation {
    /**
     * This is invoked each time instrumentation attempts to calculate state, this can be called from either
     * `beginFieldField.dispatch` or `beginFieldFetch.complete`.
     *
     * @param parameters contains information of which [ExecutionInput] caused the calculation
     * @return an instance of [OnSyncExecutionExhausted] that will be invoked when the synchronous
     * execution was exhausted
     */
    fun calculateSyncExecutionExhaustion(
        parameters: SyncExhaustionInstrumentationParameters
    ): OnSyncExecutionExhausted
}
