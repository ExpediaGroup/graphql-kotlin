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

package com.expediagroup.graphql.server.execution.context

import com.expediagroup.graphql.generator.extensions.toGraphQLContext
import com.expediagroup.graphql.server.types.GraphQLServerRequest
import graphql.GraphQLContext

/**
 * Factory that generates a Pair that will be part of a GraphQL context.
 */
fun interface GraphQLContextEntryFactory<Request, K: Any, V> {
    suspend fun generate(
        request: Request,
        graphQLRequest: GraphQLServerRequest,
        accumulator: Map<Any, Any?>
    ): Pair<K, V>?
}

/**
 * Builder that takes list of [GraphQLContextEntryFactory] used to generate entries for a GraphQL context
 */
interface GraphQLContextBuilder<Request> : GraphQLContextProvider<Request> {

    val entryFactories: List<GraphQLContextEntryFactory<Request, Any, *>>

    override suspend fun generateContext(
        request: Request,
        graphQLRequest: GraphQLServerRequest,
    ): GraphQLContext =
        entryFactories.fold(mutableMapOf<Any, Any?>()) { accumulator, entryFactory ->
            accumulator.also {
                entryFactory.generate(request, graphQLRequest, accumulator)?.let { entry ->
                    accumulator += entry
                }
            }
        }.toGraphQLContext()
}
