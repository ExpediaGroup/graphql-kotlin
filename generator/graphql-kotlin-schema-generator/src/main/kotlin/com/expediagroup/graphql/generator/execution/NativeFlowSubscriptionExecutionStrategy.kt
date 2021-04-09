/*
 * Copyright 2020 Expedia, Inc
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

package com.expediagroup.graphql.generator.execution

import graphql.ExecutionResult
import graphql.ExecutionResultImpl
import graphql.execution.DataFetcherExceptionHandler
import graphql.execution.ExecutionContext
import graphql.execution.ExecutionStrategyParameters
import graphql.execution.SimpleDataFetcherExceptionHandler
import graphql.execution.SubscriptionExecutionStrategy
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.future.await
import kotlinx.coroutines.reactive.asFlow
import org.reactivestreams.Publisher

/**
 * [SubscriptionExecutionStrategy] replacement that and allows schema subscription functions
 * to return either a [Flow] or a [Publisher], and converts [Publisher]s to [Flow]s.
 */
class NativeFlowSubscriptionExecutionStrategy(dfe: DataFetcherExceptionHandler) : BaseFlowSubscriptionExecutionStrategy<Flow<*>>(dfe) {
    constructor() : this(SimpleDataFetcherExceptionHandler())

    override fun convertToSupportedFlow(publisherOrFlow: Any?): Flow<*>? {
        return when (publisherOrFlow) {
            is Publisher<*> -> publisherOrFlow.asFlow()
            // below explicit cast is required due to the type erasure and Kotlin declaration-site variance vs Java use-site variance
            is Flow<*> -> publisherOrFlow
            else -> null
        }
    }

    override fun getSubscriberAdapter(executionContext: ExecutionContext, parameters: ExecutionStrategyParameters): (Flow<*>?) -> ExecutionResult {
        return { sourceFlow ->
            if (sourceFlow == null) {
                ExecutionResultImpl(null, executionContext.errors)
            } else {
                val returnFlow = sourceFlow.map {
                    executeSubscriptionEvent(executionContext, parameters, it).await()
                }
                ExecutionResultImpl(returnFlow, executionContext.errors)
            }
        }
    }
}
