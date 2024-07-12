/*
 * Copyright 2024 Expedia, Inc
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

package com.expediagroup.graphql.dataloader.instrumentation.extensions

import com.expediagroup.graphql.dataloader.KotlinDataLoaderRegistry
import com.expediagroup.graphql.dataloader.instrumentation.exceptions.MissingInstrumentationStateException
import com.expediagroup.graphql.dataloader.instrumentation.exceptions.MissingKotlinDataLoaderRegistryException
import com.expediagroup.graphql.dataloader.instrumentation.syncexhaustion.state.SyncExecutionExhaustedState
import graphql.schema.DataFetchingEnvironment
import org.dataloader.DataLoader
import java.util.concurrent.CompletableFuture

/**
 * Check if all futures collected on [KotlinDataLoaderRegistry.dispatchAll] were handled
 * and if we have more futures than we had when we started to dispatch, if so,
 * means that [DataLoader]s were chained, so we need to dispatch the dataLoaderRegistry.
 */
fun <V> CompletableFuture<V>.dispatchIfNeeded(
    environment: DataFetchingEnvironment
): CompletableFuture<V> {
    val dataLoaderRegistry = environment.dataLoaderRegistry as? KotlinDataLoaderRegistry ?: throw MissingKotlinDataLoaderRegistryException()

    if (dataLoaderRegistry.dataLoadersInvokedOnDispatch()) {
        when {
            environment.graphQlContext.hasKey(SyncExecutionExhaustedState::class) -> {
                environment
                    .graphQlContext.get<SyncExecutionExhaustedState>(SyncExecutionExhaustedState::class)
                    .ifAllSyncExecutionsExhausted {
                        dataLoaderRegistry.dispatchAll()
                    }
            }
            else -> throw MissingInstrumentationStateException()
        }
    }
    return this
}
