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

package com.expediagroup.graphql.server.spring.execution

import com.expediagroup.graphql.server.execution.GraphQLRequestHandler
import com.expediagroup.graphql.server.execution.GraphQLServer
import graphql.GraphQLContext
import org.springframework.web.reactive.function.server.ServerRequest

/**
 * Server object that requires the other Spring specific server implementations.
 */
open class SpringGraphQLServer(
    requestParser: SpringGraphQLRequestParser,
    contextFactory: SpringGraphQLContextFactory,
    requestHandler: GraphQLRequestHandler
) : GraphQLServer<ServerRequest>(
    requestParser,
    CacheControlAwareContextFactory(contextFactory),
    requestHandler
)

/**
 * Delegates context creation and, if the route requested `Cache-Control` calculation, adds the request
 * [CacheControlPolicies] to the generated [GraphQLContext].
 */
private class CacheControlAwareContextFactory(
    private val delegate: SpringGraphQLContextFactory
) : SpringGraphQLContextFactory() {
    override suspend fun generateContext(request: ServerRequest): GraphQLContext =
        delegate.generateContext(request).also { context ->
            (request.attribute(CACHE_CONTROL_POLICIES_ATTRIBUTE).orElse(null) as? CacheControlPolicies)?.let { policies ->
                context.put(CacheControlPolicies::class, policies)
            }
        }
}
