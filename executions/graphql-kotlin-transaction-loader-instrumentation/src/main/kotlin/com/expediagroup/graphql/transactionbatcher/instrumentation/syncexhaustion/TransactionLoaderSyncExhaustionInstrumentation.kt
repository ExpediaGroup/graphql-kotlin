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

package com.expediagroup.graphql.transactionbatcher.instrumentation.syncexhaustion

import com.expediagroup.graphql.transactionbatcher.instrumentation.syncexhaustion.execution.AbstractSyncExhaustionInstrumentation
import com.expediagroup.graphql.transactionbatcher.instrumentation.syncexhaustion.execution.SyncExhaustionInstrumentationContext
import com.expediagroup.graphql.transactionbatcher.instrumentation.syncexhaustion.execution.SyncExhaustionInstrumentationParameters
import graphql.ExecutionInput
import graphql.GraphQLContext
import graphql.execution.instrumentation.Instrumentation
import org.dataloader.DataLoaderRegistry
import org.dataloader.DataLoader

/**
 * Custom GraphQL [Instrumentation] that will dispatch all [DataLoader]s inside a [DataLoaderRegistry]
 * located in a [GraphQLContext], which can be shared among multiple GraphQL operations.
 */
class TransactionLoaderSyncExhaustionInstrumentation : AbstractSyncExhaustionInstrumentation() {
    override fun calculateSyncExhaustionState(
        parameters: SyncExhaustionInstrumentationParameters
    ): SyncExhaustionInstrumentationContext = object : SyncExhaustionInstrumentationContext {
        override fun onSyncExecutionExhausted(executions: List<ExecutionInput>) {
            parameters
                .executionContext
                .graphQLContext.get<DataLoaderRegistry>(DataLoaderRegistry::class)
                ?.dispatchAll()
        }
    }
}
