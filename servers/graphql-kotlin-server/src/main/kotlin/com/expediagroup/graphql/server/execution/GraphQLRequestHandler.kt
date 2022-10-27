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

package com.expediagroup.graphql.server.execution

import com.expediagroup.graphql.dataloader.KotlinDataLoaderRegistry
import com.expediagroup.graphql.dataloader.KotlinDataLoaderRegistryFactory
import com.expediagroup.graphql.dataloader.instrumentation.level.DataLoaderLevelDispatchedInstrumentation
import com.expediagroup.graphql.dataloader.instrumentation.level.state.ExecutionLevelDispatchedState
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
import graphql.GraphQL
import graphql.GraphQLContext
import graphql.execution.instrumentation.ChainedInstrumentation
import graphql.execution.instrumentation.Instrumentation
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.future.await
import kotlinx.coroutines.supervisorScope

open class GraphQLRequestHandler(
    private val graphQL: GraphQL,
    private val dataLoaderRegistryFactory: KotlinDataLoaderRegistryFactory? = null
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
        graphQLContext: GraphQLContext = GraphQLContext.of(emptyMap<Any, Any>())
    ): GraphQLServerResponse {
        val dataLoaderRegistry = dataLoaderRegistryFactory?.generate()
        return when (graphQLRequest) {
            is GraphQLRequest -> {
                val batchGraphQLContext = graphQLContext + getBatchContext(1, dataLoaderRegistry)
                execute(graphQLRequest, batchGraphQLContext, dataLoaderRegistry)
            }
            is GraphQLBatchRequest -> {
                if (graphQLRequest.containsMutation()) {
                    val batchGraphQLContext = graphQLContext + getBatchContext(1, dataLoaderRegistry)
                    executeSequentially(graphQLRequest, batchGraphQLContext, dataLoaderRegistry)
                } else {
                    val batchGraphQLContext = graphQLContext + getBatchContext(graphQLRequest.requests.size, dataLoaderRegistry)
                    executeConcurrently(graphQLRequest, batchGraphQLContext, dataLoaderRegistry)
                }
            }
        }
    }

    private suspend fun execute(
        graphQLRequest: GraphQLRequest,
        batchGraphQLContext: GraphQLContext,
        dataLoaderRegistry: KotlinDataLoaderRegistry?
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
        dataLoaderRegistry: KotlinDataLoaderRegistry?,
    ): GraphQLBatchResponse =
        GraphQLBatchResponse(
            batchRequest.requests.map { request ->
                execute(request, batchGraphQLContext, dataLoaderRegistry)
            }
        )

    private suspend fun executeConcurrently(
        batchRequest: GraphQLBatchRequest,
        batchGraphQLContext: GraphQLContext,
        dataLoaderRegistry: KotlinDataLoaderRegistry?,
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

    private fun getBatchContext(
        batchSize: Int,
        dataLoaderRegistry: KotlinDataLoaderRegistry?
    ): Map<*, Any> {
        if (dataLoaderRegistry == null) {
            return emptyMap<Any, Any>()
        }

        val batchContext = when (batchDataLoaderInstrumentationType) {
            DataLoaderLevelDispatchedInstrumentation::class.java -> mapOf(
                ExecutionLevelDispatchedState::class to ExecutionLevelDispatchedState(batchSize)
            )
            DataLoaderSyncExecutionExhaustedInstrumentation::class.java -> mapOf(
                SyncExecutionExhaustedState::class to SyncExecutionExhaustedState(batchSize, dataLoaderRegistry)
            )
            else -> emptyMap<Any, Any>()
        }
        return batchContext
    }
}
