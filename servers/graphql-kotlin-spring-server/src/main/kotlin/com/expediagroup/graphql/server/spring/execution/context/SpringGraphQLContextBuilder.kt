/*
 * Copyright 2023 Expedia, Inc
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

package com.expediagroup.graphql.server.spring.execution.context

import com.apollographql.federation.graphqljava.tracing.FederatedTracingInstrumentation.FEDERATED_TRACING_HEADER_NAME
import com.expediagroup.graphql.generator.extensions.toGraphQLContext
import com.expediagroup.graphql.server.execution.context.GraphQLContextBuilder
import com.expediagroup.graphql.server.execution.context.GraphQLContextEntryFactory
import com.expediagroup.graphql.server.types.GraphQLServerRequest
import graphql.GraphQLContext
import org.springframework.web.reactive.function.server.ServerRequest

/**
 * interface to handle the Spring [ServerRequest]
 */
interface SpringGraphQLContextBuilder : GraphQLContextBuilder<ServerRequest>

/**
 * Basic implementation of [SpringGraphQLContextBuilder] that populates Apollo tracing header.
 */
open class DefaultSpringGraphQLContextBuilder(
    override val entryFactories: List<GraphQLContextEntryFactory<ServerRequest, Any, *>>
) : SpringGraphQLContextBuilder {

    constructor(vararg entryFactories: GraphQLContextEntryFactory<ServerRequest, Any, *>) : this(entryFactories.toList())

    override suspend fun generateContext(
        request: ServerRequest,
        graphQLRequest: GraphQLServerRequest
    ): GraphQLContext =
        (entryFactories + tracingHeaderEntryFactory)
            .fold(mutableMapOf<Any, Any?>()) { accumulator, entryFactory ->
                accumulator.also {
                    entryFactory.generate(request, graphQLRequest, accumulator)?.let { entry ->
                        accumulator += entry
                    }
                }
            }
            .toGraphQLContext()

    companion object {
        val tracingHeaderEntryFactory: GraphQLContextEntryFactory<ServerRequest, Any, *> =
            GraphQLContextEntryFactory { request, _, _ ->
                request.headers().firstHeader(FEDERATED_TRACING_HEADER_NAME)?.let { headerValue ->
                    Pair(FEDERATED_TRACING_HEADER_NAME, headerValue)
                }
            }
    }
}
