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

import com.expediagroup.graphql.generator.execution.GraphQLContext
import com.expediagroup.graphql.server.extensions.isMutation
import com.expediagroup.graphql.server.extensions.toGraphQLError
import com.expediagroup.graphql.server.extensions.toGraphQLKotlinType
import com.expediagroup.graphql.server.types.GraphQLBatchRequest
import com.expediagroup.graphql.server.types.GraphQLBatchResponse
import com.expediagroup.graphql.server.types.GraphQLRequest
import com.expediagroup.graphql.server.types.GraphQLResponse
import com.expediagroup.graphql.server.types.GraphQLServerResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.supervisorScope
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * A basic server implementation that parses the incoming request and returns a [GraphQLResponse].
 * Subscriptions require more server-specific details and should be implemented separately.
 */
open class GraphQLServer<Request>(
    private val requestParser: GraphQLRequestParser<Request>,
    private val contextFactory: GraphQLContextFactory<*, Request>,
    private val requestHandler: GraphQLRequestHandler
) {

    /**
     * Default execution logic for handling a [Request] and returning a [GraphQLServerResponse].
     *
     * If null is returned, that indicates a problem parsing the request or context.
     * If the request is valid, a [GraphQLServerResponse] should always be returned.
     * In the case of errors or exceptions, return a response with GraphQLErrors populated.
     * If you need custom logic inside this method you can override this class or choose not to use it.
     */
    open suspend fun execute(request: Request): GraphQLServerResponse? = coroutineScope {
        val graphQLRequest = requestParser.parseRequest(request)

        if (graphQLRequest != null) {
            val deprecatedContext = contextFactory.generateContext(request)
            val contextMap = contextFactory.generateContextMap(request)

            val customCoroutineContext: CoroutineContext = (deprecatedContext?.graphQLCoroutineContext() ?: EmptyCoroutineContext) +
                (contextMap[CoroutineContext::class] as? CoroutineContext ?: EmptyCoroutineContext)
            val graphQLExecutionScope = CoroutineScope(coroutineContext + customCoroutineContext + SupervisorJob())
            val graphQLContext = contextMap + mapOf(CoroutineScope::class to graphQLExecutionScope)

            when (graphQLRequest) {
                is GraphQLRequest -> requestHandler.executeRequest(graphQLRequest, deprecatedContext, graphQLContext)
                is GraphQLBatchRequest -> when {
                    graphQLRequest.requests.any(GraphQLRequest::isMutation) -> GraphQLBatchResponse(
                        graphQLRequest.requests.map {
                            requestHandler.executeRequest(it, deprecatedContext, graphQLContext)
                        }
                    )
                    else -> GraphQLBatchResponse(
                        handleConcurrently(graphQLRequest, deprecatedContext, graphQLContext)
                    )
                }
            }
        } else {
            null
        }
    }

    /**
     * Concurrently execute a [batchRequest], a failure in an specific request will not cause the scope
     * to fail and does not affect the other requests. The total execution time will be the time of the slowest
     * request
     */
    private suspend fun handleConcurrently(
        batchRequest: GraphQLBatchRequest,
        context: GraphQLContext?,
        graphQLContext: Map<*, Any>
    ): List<GraphQLResponse<*>> = supervisorScope {
        batchRequest.requests.map { request ->
            async {
                try {
                    requestHandler.executeRequest(request, context, graphQLContext)
                } catch (e: Exception) {
                    GraphQLResponse<Any?>(
                        errors = listOf(e.toGraphQLError().toGraphQLKotlinType())
                    )
                }
            }
        }.awaitAll()
    }
}
