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
import com.expediagroup.graphql.server.extensions.isDataLoaderRelatedInstrumentation
import com.expediagroup.graphql.server.extensions.isMutation
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
import graphql.ExecutionInput
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

    private val batchingInstrumentationStrategy: Class<Instrumentation>? by lazy {
        val instrumentation = graphQL.instrumentation
        when {
            instrumentation is ChainedInstrumentation -> {
                instrumentation.instrumentations
                    .firstOrNull(Instrumentation::isDataLoaderRelatedInstrumentation)
                    ?.javaClass
            }
            instrumentation.isDataLoaderRelatedInstrumentation() -> instrumentation.javaClass
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
            is GraphQLRequest -> execute(
                graphQLRequest.toExecutionInput(context, dataLoaderRegistryFactory?.generate(), graphQLContext)
            )
            is GraphQLBatchRequest -> when {
                graphQLRequest.requests.none(GraphQLRequest::isMutation) -> {
                    val dataLoaderRegistry = dataLoaderRegistryFactory?.generate()
                    val batchingInstrumentationStateContext = dataLoaderRegistry?.let {
                        when (batchingInstrumentationStrategy) {
                            DataLoaderLevelDispatchedInstrumentation::class.java -> mapOf(
                                KotlinDataLoaderRegistry::class to dataLoaderRegistry,
                                ExecutionLevelDispatchedState::class to ExecutionLevelDispatchedState(
                                    graphQLRequest.requests.size
                                )
                            )
                            DataLoaderSyncExecutionExhaustedInstrumentation::class.java -> mapOf(
                                KotlinDataLoaderRegistry::class to dataLoaderRegistry,
                                SyncExecutionExhaustedState::class to SyncExecutionExhaustedState(
                                    graphQLRequest.requests.size,
                                    dataLoaderRegistry
                                )
                            )
                            else -> null
                        }
                    } ?: emptyMap()
                    val contextWithBatchingInstrumentationState = graphQLContext + batchingInstrumentationStateContext

                    GraphQLBatchResponse(
                        execute(
                            graphQLRequest.requests.map {
                                it.toExecutionInput(
                                    context,
                                    // if there is a DataLoaderInstrumentation avoid providing the DataLoaderRegistry
                                    // since it will be stored in the GraphQLContext
                                    when {
                                        batchingInstrumentationStrategy != null -> null
                                        else -> dataLoaderRegistry
                                    },
                                    contextWithBatchingInstrumentationState
                                )
                            }
                        )
                    )
                }
                else -> GraphQLBatchResponse(
                    graphQLRequest.requests.map {
                        execute(
                            it.toExecutionInput(context, dataLoaderRegistryFactory?.generate(), graphQLContext)
                        )
                    }
                )
            }
        }

    private suspend fun execute(
        executionInput: ExecutionInput
    ): GraphQLResponse<*> =
        try {
            graphQL.executeAsync(executionInput).await().toGraphQLResponse()
        } catch (exception: Exception) {
            val error = exception.toGraphQLError()
            GraphQLResponse<Any?>(errors = listOf(error.toGraphQLKotlinType()))
        }

    private suspend fun execute(
        executionInputs: List<ExecutionInput>
    ): List<GraphQLResponse<*>> =
        supervisorScope {
            executionInputs.map { executionInput ->
                async {
                    execute(executionInput)
                }
            }.awaitAll()
        }
}
