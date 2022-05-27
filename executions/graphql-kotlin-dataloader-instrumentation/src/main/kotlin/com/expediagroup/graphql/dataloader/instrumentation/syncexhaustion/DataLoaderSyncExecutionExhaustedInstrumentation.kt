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

package com.expediagroup.graphql.dataloader.instrumentation.syncexhaustion

import com.expediagroup.graphql.dataloader.KotlinDataLoaderRegistry
import com.expediagroup.graphql.dataloader.instrumentation.syncexhaustion.execution.AbstractSyncExecutionExhaustedInstrumentation
import com.expediagroup.graphql.dataloader.instrumentation.syncexhaustion.execution.OnSyncExecutionExhaustedCallback
import com.expediagroup.graphql.dataloader.instrumentation.syncexhaustion.execution.SyncExecutionExhaustedInstrumentationParameters
import graphql.ExecutionInput
import graphql.GraphQLContext
import graphql.execution.instrumentation.Instrumentation
import graphql.schema.DataFetcher
import org.dataloader.DataLoader
import java.util.concurrent.CompletableFuture

/**
 * Custom GraphQL [Instrumentation] that will dispatch all [DataLoader]s inside a [KotlinDataLoaderRegistry]
 * when the synchronous execution of all [ExecutionInput] sharing a [GraphQLContext] was exhausted.
 *
 * A Synchronous Execution is considered Exhausted when all [DataFetcher]s of all paths were executed up until
 * a scalar leaf or a [DataFetcher] that returns a [CompletableFuture]
 */
class DataLoaderSyncExecutionExhaustedInstrumentation : AbstractSyncExecutionExhaustedInstrumentation() {
    override fun getOnSyncExecutionExhaustedCallback(
        parameters: SyncExecutionExhaustedInstrumentationParameters
    ): OnSyncExecutionExhaustedCallback = { executions: List<ExecutionInput> ->
        executions
            .getOrNull(0)
            ?.dataLoaderRegistry
            ?.dispatchAll()
    }
}
