/*
 * Copyright 2019 Expedia, Inc
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

package com.expediagroup.graphql.spring.execution

import com.expediagroup.graphql.execution.EmptyGraphQLContext
import com.expediagroup.graphql.execution.GraphQLContext
import com.expediagroup.graphql.federation.execution.FederatedGraphQLContext
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.http.server.reactive.ServerHttpResponse

/**
 * Reactor SubscriberContext key for storing GraphQL context.
 */
const val GRAPHQL_CONTEXT_KEY = "graphQLContext"

/**
 * Factory that generates GraphQL context.
 */
interface GraphQLContextFactory<out T : GraphQLContext> {

    /**
     * Generate GraphQL context based on the incoming request and the corresponding response.
     */
    suspend fun generateContext(request: ServerHttpRequest, response: ServerHttpResponse): T
}

/**
 * Factory that generates federated GraphQL context.
 */
interface FederatedGraphQLContextFactory<out T : FederatedGraphQLContext> : GraphQLContextFactory<T>

/**
 * Default context factory that generates empty GraphQL context.
 */
internal object EmptyContextFactory : GraphQLContextFactory<EmptyGraphQLContext> {

    override suspend fun generateContext(request: ServerHttpRequest, response: ServerHttpResponse) = EmptyGraphQLContext()
}

/**
 * Default federated context factory that generates empty GraphQL context.
 */
internal object DefaultFederatedContextFactory : FederatedGraphQLContextFactory<FederatedGraphQLContext> {

    override suspend fun generateContext(request: ServerHttpRequest, response: ServerHttpResponse): FederatedGraphQLContext {
        return object : FederatedGraphQLContext {
            override fun getHTTPRequestHeader(caseInsensitiveHeaderName: String): String? {
                return request.headers.getFirst(caseInsensitiveHeaderName)
            }
        }
    }
}
