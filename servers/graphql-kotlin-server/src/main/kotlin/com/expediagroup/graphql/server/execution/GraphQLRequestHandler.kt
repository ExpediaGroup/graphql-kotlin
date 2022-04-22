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
import com.expediagroup.graphql.generator.execution.GraphQLContext
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
        context: GraphQLContext? = null,
        graphQLContext: Map<*, Any> = emptyMap<Any, Any>()
    ): GraphQLServerResponse =
        when (graphQLRequest) {
            is GraphQLRequest -> {
                val (dataLoaderRegistry, batchGraphQLContext) = getBatchContext(1, graphQLContext)
                execute(graphQLRequest, context, batchGraphQLContext, dataLoaderRegistry)
            }
            is GraphQLBatchRequest -> {
                if (graphQLRequest.containsMutation()) {
                    val (dataLoaderRegistry, batchGraphQLContext) = getBatchContext(1, graphQLContext)
                    executeSequentially(graphQLRequest, context, batchGraphQLContext, dataLoaderRegistry)
                } else {
                    val (dataLoaderRegistry, batchGraphQLContext) = getBatchContext(graphQLRequest.requests.size, graphQLContext)
                    executeConcurrently(graphQLRequest, context, batchGraphQLContext, dataLoaderRegistry)
                }
            }
        }

    private suspend fun execute(
        graphQLRequest: GraphQLRequest,
        context: GraphQLContext? = null,
        batchGraphQLContext: Map<*, Any>,
        registryForExecutionInput: KotlinDataLoaderRegistry?
    ): GraphQLResponse<*> =
        try {
            graphQL.executeAsync(
                graphQLRequest.toExecutionInput(context, registryForExecutionInput, batchGraphQLContext)
            ).await().toGraphQLResponse()
        } catch (exception: Exception) {
            val error = exception.toGraphQLError()
            GraphQLResponse<Any?>(errors = listOf(error.toGraphQLKotlinType()))
        }

    private suspend fun executeSequentially(
        batchRequest: GraphQLBatchRequest,
        context: GraphQLContext?,
        batchGraphQLContext: Map<*, Any>,
        registryForExecutionInput: KotlinDataLoaderRegistry?
    ): GraphQLBatchResponse =
        GraphQLBatchResponse(
            batchRequest.requests.map { request ->
                execute(request, context, batchGraphQLContext, registryForExecutionInput)
            }
        )

    private suspend fun executeConcurrently(
        batchRequest: GraphQLBatchRequest,
        context: GraphQLContext?,
        batchGraphQLContext: Map<*, Any>,
        registryForExecutionInput: KotlinDataLoaderRegistry?
    ): GraphQLBatchResponse {
        val responses = supervisorScope {
            batchRequest.requests.map { request ->
                async {
                    execute(request, context, batchGraphQLContext, registryForExecutionInput)
                }
            }.awaitAll()
        }
        return GraphQLBatchResponse(responses)
    }

    private fun getBatchContext(
        batchSize: Int,
        graphQLContext: Map<*, Any>
    ): Pair<KotlinDataLoaderRegistry?, Map<*, Any>> {
        val dataLoaderRegistry = dataLoaderRegistryFactory?.generate() ?: return Pair(null, graphQLContext)

        val batchContext = when (batchDataLoaderInstrumentationType) {
            DataLoaderLevelDispatchedInstrumentation::class.java -> mapOf(
                KotlinDataLoaderRegistry::class to dataLoaderRegistry,
                ExecutionLevelDispatchedState::class to ExecutionLevelDispatchedState(batchSize)
            )
            DataLoaderSyncExecutionExhaustedInstrumentation::class.java -> mapOf(
                KotlinDataLoaderRegistry::class to dataLoaderRegistry,
                SyncExecutionExhaustedState::class to SyncExecutionExhaustedState(batchSize, dataLoaderRegistry)
            )
            else -> null
        }

        val batchGraphQLContext = graphQLContext + (batchContext ?: emptyMap())

        // if no batchContext provide dataLoaderRegistry to be added to the ExecutionInput
        val registryForExecutionInput = when (batchContext) {
            null -> dataLoaderRegistry
            else -> null
        }

        return Pair(
            registryForExecutionInput,
            batchGraphQLContext
        )
    }
}
