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
import graphql.execution.reactive.CompletionStageMappingPublisher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asPublisher
import org.reactivestreams.Publisher
import java.util.concurrent.CompletionStage
import java.util.function.Function

/**
 * [SubscriptionExecutionStrategy] replacement that and allows schema subscription functions
 * to return either a [Flow] or a [Publisher], and converts [Flow]s to [Publisher]s.
 */
class FlowSubscriptionExecutionStrategy(dfe: DataFetcherExceptionHandler) : BaseFlowSubscriptionExecutionStrategy<Publisher<out Any>>(dfe) {
    constructor() : this(SimpleDataFetcherExceptionHandler())

    override fun convertToSupportedFlow(publisherOrFlow: Any?): Publisher<out Any>? {
        return when (publisherOrFlow) {
            is Publisher<*> -> publisherOrFlow
            // below explicit cast is required due to the type erasure and Kotlin declaration-site variance vs Java use-site variance
            is Flow<*> -> (publisherOrFlow as? Flow<Any>)?.asPublisher()
            else -> null
        }
    }

    override fun getSubscriberAdapter(executionContext: ExecutionContext, parameters: ExecutionStrategyParameters): (Publisher<out Any>?) -> ExecutionResult {
        return { publisher ->
            if (publisher == null) {
                ExecutionResultImpl(null, executionContext.errors)
            } else {
                val mapperFunction = Function<Any, CompletionStage<ExecutionResult>> { eventPayload: Any? ->
                    executeSubscriptionEvent(
                        executionContext,
                        parameters,
                        eventPayload
                    )
                }
                // we need explicit cast as Kotlin Flow is covariant (Flow<out T> vs Publisher<T>)
                val mapSourceToResponse = CompletionStageMappingPublisher<ExecutionResult, Any>(publisher as Publisher<Any>, mapperFunction)
                ExecutionResultImpl(mapSourceToResponse, executionContext.errors)
            }
        }
    }
}
