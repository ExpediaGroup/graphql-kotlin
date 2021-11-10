/*
 * Copyright 2021 Expedia, Inc
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

import com.expediagroup.graphql.server.extensions.concurrentMap
import com.expediagroup.graphql.server.extensions.isMutation
import com.expediagroup.graphql.server.types.GraphQLBatchRequest
import com.expediagroup.graphql.server.types.GraphQLBatchResponse
import com.expediagroup.graphql.server.types.GraphQLRequest
import com.expediagroup.graphql.server.types.GraphQLResponse
import com.expediagroup.graphql.server.types.GraphQLServerResponse

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
    open suspend fun execute(request: Request): GraphQLServerResponse? {
        val graphQLRequest = requestParser.parseRequest(request)

        return if (graphQLRequest != null) {
            val context = contextFactory.generateContext(request)
            when (graphQLRequest) {
                is GraphQLRequest -> requestHandler.executeRequest(graphQLRequest, context)
                is GraphQLBatchRequest -> when {
                    graphQLRequest.requests.any(GraphQLRequest::isMutation) -> GraphQLBatchResponse(
                        graphQLRequest.requests.map {
                            requestHandler.executeRequest(it, context)
                        }
                    )
                    else -> {
                        GraphQLBatchResponse(
                            graphQLRequest.requests.concurrentMap {
                                requestHandler.executeRequest(it, context)
                            }
                        )
                    }

                }
            }
        } else {
            null
        }
    }
}
