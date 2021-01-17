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

import com.expediagroup.graphql.types.GraphQLRequest

/**
 * GraphQL server request abstraction that provides a convenient way to handle both single and batch requests.
 */
sealed class GraphQLServerRequest<T : Any>(val request: T)

/**
 * Wrapper that holds single GraphQLRequest to be processed within an HTTP request.
 */
class GraphQLSingleRequest(graphQLRequest: GraphQLRequest) : GraphQLServerRequest<GraphQLRequest>(graphQLRequest)

/**
 * Wrapper that holds list of GraphQLRequests to be processed together within a single HTTP request.
 */
class GraphQLBatchRequest(requests: List<GraphQLRequest>) : GraphQLServerRequest<List<GraphQLRequest>>(requests)
