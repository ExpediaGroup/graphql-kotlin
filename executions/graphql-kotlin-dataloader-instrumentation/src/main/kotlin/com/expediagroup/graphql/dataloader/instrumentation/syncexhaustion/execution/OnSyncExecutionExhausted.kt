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
import graphql.GraphQLContext
import java.util.concurrent.CompletableFuture

/**
 * Defines the contract for the behavior that needs to be executed when a SyncExhaustion is calculated
 */
fun interface OnSyncExecutionExhausted {
    /**
     * this is invoked when all [ExecutionInput] sharing a [GraphQLContext] exhausted their synchronous execution.
     * a synchronous execution is considered exhausted when all data fetchers of all paths were executed up until
     * an scalar leaf or data fetcher that returns a [CompletableFuture]
     *
     * @param executions list of executions that exhausted their sync execution.
     */
    fun invoke(
        executions: List<ExecutionInput>
    )
}