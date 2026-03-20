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

package com.expediagroup.graphql.generator.execution

import graphql.ExecutionResult
import graphql.ExecutionResultImpl
import graphql.execution.DataFetcherExceptionHandler
import graphql.execution.ExecutionContext
import graphql.execution.ExecutionStrategyParameters
import graphql.execution.FetchedValue
import graphql.execution.SimpleDataFetcherExceptionHandler
import graphql.execution.SubscriptionExecutionStrategy
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.asPublisher
import org.reactivestreams.Publisher
import java.util.concurrent.CompletableFuture

/**
 * [SubscriptionExecutionStrategy] subclass that additionally allows schema subscription functions
 * to return a kotlinx [Flow].
 *
 * This delegates all execution logic — instrumentation, data loader dispatch, error handling,
 * event processing — to the parent. It diverges in two places only:
 *
 * 1. [execute] — converts the [Publisher]<[ExecutionResult]> produced by the parent back into
 *    a kotlinx [Flow]<[ExecutionResult]> as the execution result data, to preserve the
 *    coroutine-friendly API for callers.
 * 2. [fetchField] — converts any kotlinx [Flow] returned by a data fetcher into a
 *    reactive-streams [Publisher] before the parent's [createSourceEventStream] sees it,
 *    since the parent natively handles [Publisher] but not [Flow].
 */
class FlowSubscriptionExecutionStrategy(dfe: DataFetcherExceptionHandler) : SubscriptionExecutionStrategy(dfe) {
    constructor() : this(SimpleDataFetcherExceptionHandler())

    override fun execute(
        executionContext: ExecutionContext,
        parameters: ExecutionStrategyParameters
    ): CompletableFuture<ExecutionResult> =
        super.execute(executionContext, parameters).thenApply { executionResult ->
            val publisher = executionResult.getData<Publisher<ExecutionResult>?>()
            if (publisher != null) {
                ExecutionResultImpl(publisher.asFlow(), executionResult.errors)
            } else {
                executionResult
            }
        }

    override fun fetchField(
        executionContext: ExecutionContext,
        parameters: ExecutionStrategyParameters
    ): Any {
        val result = super.fetchField(executionContext, parameters)
        @Suppress("UNCHECKED_CAST")
        return when {
            result is CompletableFuture<*> -> (result as CompletableFuture<Any?>).thenApply { value -> convertFlowToPublisher(value) }
            else -> convertFlowToPublisher(result)
        } as Any
    }

    /**
     * Converts any kotlinx [Flow] in the fetch result to a reactive-streams [Publisher] so that
     * the parent [SubscriptionExecutionStrategy] can handle it natively. Handles both a bare [Flow]
     * and a [Flow] wrapped inside a [FetchedValue]; all other values are passed through unchanged.
     */
    @Suppress("UNCHECKED_CAST")
    private fun convertFlowToPublisher(value: Any?): Any? = when (value) {
        is Flow<*> -> (value as Flow<Any>).asPublisher()
        is FetchedValue if value.fetchedValue is Flow<*> ->
            FetchedValue((value.fetchedValue as Flow<Any>).asPublisher(), value.errors, value.localContext)

        else -> value
    }
}
