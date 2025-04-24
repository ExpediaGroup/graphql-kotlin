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

package com.expediagroup.graphql.server.execution

import com.expediagroup.graphql.dataloader.KotlinDataLoaderRegistry
import com.expediagroup.graphql.dataloader.KotlinDataLoaderRegistryFactory
import com.expediagroup.graphql.dataloader.instrumentation.syncexhaustion.DataLoaderInstrumentationForSyncExecutionExhausted
import com.expediagroup.graphql.dataloader.instrumentation.syncexhaustion.DataLoaderSyncExecutionExhaustedInstrumentation
import com.expediagroup.graphql.dataloader.instrumentation.syncexhaustion.state.SyncExecutionExhaustedState
import com.expediagroup.graphql.generator.extensions.plus
import com.expediagroup.graphql.server.extensions.containsMutation
import com.expediagroup.graphql.server.extensions.isBatchDataLoaderInstrumentation
import com.expediagroup.graphql.server.extensions.toExecutionInput
import com.expediagroup.graphql.server.extensions.toGraphQLError
import com.expediagroup.graphql.server.extensions.toGraphQLKotlinType
import com.expediagroup.graphql.server.extensions.toGraphQLResponse
import com.expediagroup.graphql.server.types.GraphQLBatchRequest
import com.expediagroup.graphql.server.types.GraphQLBatchResponse
import com.expediagroup.graphql.server.types.GraphQLRequest
import com.expediagroup.graphql.server.types.GraphQLResponse
import com.expediagroup.graphql.server.types.GraphQLServerRequest
import com.expediagroup.graphql.server.types.GraphQLServerResponse
import graphql.ExecutionResult
import graphql.GraphQL
import graphql.GraphQLContext
import graphql.execution.instrumentation.ChainedInstrumentation
import graphql.execution.instrumentation.Instrumentation
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.future.await
import kotlinx.coroutines.supervisorScope
import org.dataloader.instrumentation.DataLoaderInstrumentation

open class GraphQLRequestHandler(
    private val graphQL: GraphQL,
    private val dataLoaderRegistryFactory: KotlinDataLoaderRegistryFactory
) {

    private val batchDataLoaderInstrumentationType: Class<Instrumentation>? =
        graphQL.instrumentation?.let { instrumentation ->
            when {
                instrumentation is ChainedInstrumentation -> {
                    instrumentation.instrumentations
                        .firstOrNull(Instrumentation::isBatchDataLoaderInstrumentation)
                        ?.javaClass
                }

                instrumentation.isBatchDataLoaderInstrumentation() -> instrumentation.javaClass
                else -> null
            }
        }

    /**
     * Execute a GraphQL request in a non-blocking fashion.
     * This should only be used for queries and mutations.
     * Subscriptions require more specific server logic and will need to be handled separately.
     */
    open suspend fun executeRequest(
        graphQLRequest: GraphQLServerRequest,
        graphQLContext: GraphQLContext = GraphQLContext.getDefault()
    ): GraphQLServerResponse {
        return when (graphQLRequest) {
            is GraphQLRequest -> {
                val (batchContext, dataLoaderInstrumentation) = getBatchContext(1)
                val batchGraphQLContext = graphQLContext + batchContext
                val dataLoaderRegistry = dataLoaderRegistryFactory.generate(batchGraphQLContext, dataLoaderInstrumentation)
                execute(graphQLRequest, batchGraphQLContext, dataLoaderRegistry)
            }
            is GraphQLBatchRequest -> {
                if (graphQLRequest.containsMutation()) {
                    val (batchContext, dataLoaderInstrumentation) = getBatchContext(1)
                    val batchGraphQLContext = graphQLContext + batchContext
                    val dataLoaderRegistry = dataLoaderRegistryFactory.generate(batchGraphQLContext, dataLoaderInstrumentation)
                    executeSequentially(graphQLRequest, batchGraphQLContext, dataLoaderRegistry)
                } else {
                    val (batchContext, dataLoaderInstrumentation) = getBatchContext(graphQLRequest.requests.size)
                    val batchGraphQLContext = graphQLContext + batchContext
                    val dataLoaderRegistry = dataLoaderRegistryFactory.generate(batchGraphQLContext, dataLoaderInstrumentation)
                    executeConcurrently(graphQLRequest, batchGraphQLContext, dataLoaderRegistry)
                }
            }
        }
    }

    private suspend fun execute(
        graphQLRequest: GraphQLRequest,
        batchGraphQLContext: GraphQLContext,
        dataLoaderRegistry: KotlinDataLoaderRegistry
    ): GraphQLResponse<*> =
        try {
            graphQL.executeAsync(
                graphQLRequest.toExecutionInput(batchGraphQLContext, dataLoaderRegistry)
            ).await().toGraphQLResponse()
        } catch (exception: Exception) {
            val error = exception.toGraphQLError()
            GraphQLResponse<Any?>(errors = listOf(error.toGraphQLKotlinType()))
        }

    private suspend fun executeSequentially(
        batchRequest: GraphQLBatchRequest,
        batchGraphQLContext: GraphQLContext,
        dataLoaderRegistry: KotlinDataLoaderRegistry,
    ): GraphQLBatchResponse =
        GraphQLBatchResponse(
            batchRequest.requests.map { request ->
                execute(request, batchGraphQLContext, dataLoaderRegistry)
            }
        )

    private suspend fun executeConcurrently(
        batchRequest: GraphQLBatchRequest,
        batchGraphQLContext: GraphQLContext,
        dataLoaderRegistry: KotlinDataLoaderRegistry,
    ): GraphQLBatchResponse {
        val responses = supervisorScope {
            batchRequest.requests.map { request ->
                async {
                    execute(request, batchGraphQLContext, dataLoaderRegistry)
                }
            }.awaitAll()
        }
        return GraphQLBatchResponse(responses)
    }

    private fun getBatchContext(batchSize: Int): Pair<GraphQLContext, DataLoaderInstrumentation?> {
        return when (batchDataLoaderInstrumentationType) {
            DataLoaderSyncExecutionExhaustedInstrumentation::class.java -> {
                val syncExecutionExhaustedState = SyncExecutionExhaustedState(batchSize)
                GraphQLContext.of(
                    mapOf(SyncExecutionExhaustedState::class to syncExecutionExhaustedState)
                ) to DataLoaderInstrumentationForSyncExecutionExhausted(syncExecutionExhaustedState)
            }

            else -> GraphQLContext.getDefault() to null
        }
    }

    /**
     * Execute a GraphQL subscription operation in a non-blocking fashion.
     */
    open fun executeSubscription(
        graphQLRequest: GraphQLRequest,
        graphQLContext: GraphQLContext,
    ): Flow<GraphQLResponse<*>> {
        val dataLoaderRegistry = dataLoaderRegistryFactory.generate(graphQLContext)
        val input = graphQLRequest.toExecutionInput(graphQLContext, dataLoaderRegistry)
        val executionResult = graphQL.execute(input)

        val resultFlow: Flow<ExecutionResult> = executionResult
            .getData<Flow<ExecutionResult>?>() ?: flowOf(executionResult)

        return resultFlow
            .map { result -> result.toGraphQLResponse() }
            .catch { throwable ->
                val error = throwable.toGraphQLError()
                emit(GraphQLResponse<Any?>(errors = listOf(error.toGraphQLKotlinType())))
            }
    }
}
