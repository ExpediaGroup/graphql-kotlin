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

package com.expediagroup.graphql.dataloader.instrumentation.level

import com.expediagroup.graphql.dataloader.KotlinDataLoaderRegistry
import com.expediagroup.graphql.dataloader.instrumentation.level.execution.AbstractExecutionLevelDispatchedInstrumentation
import com.expediagroup.graphql.dataloader.instrumentation.level.execution.ExecutionLevelDispatchedInstrumentationParameters
import com.expediagroup.graphql.dataloader.instrumentation.level.execution.OnLevelDispatchedCallback
import com.expediagroup.graphql.dataloader.instrumentation.level.state.Level
import graphql.ExecutionInput
import graphql.GraphQLContext
import graphql.execution.instrumentation.Instrumentation
import graphql.schema.DataFetcher
import org.dataloader.DataLoader

/**
 * Custom GraphQL [Instrumentation] that will dispatch all [DataLoader]s inside a [KotlinDataLoaderRegistry]
 * when certain [Level] is dispatched for all [ExecutionInput] sharing a [GraphQLContext]
 *
 * A level is considered Dispatched when all [DataFetcher]s of a particular level of all [ExecutionInput]s
 * were dispatched
 */
class DataLoaderLevelDispatchedInstrumentation : AbstractExecutionLevelDispatchedInstrumentation() {
    override fun getOnLevelDispatchedCallback(
        parameters: ExecutionLevelDispatchedInstrumentationParameters
    ): OnLevelDispatchedCallback = { _, executions: List<ExecutionInput> ->
        executions
            .getOrNull(0)
            ?.dataLoaderRegistry
            ?.dispatchAll()
    }
}
