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

package com.expediagroup.graphql.server.spring.subscriptions

import com.expediagroup.graphql.dataloader.KotlinDataLoaderRegistryFactory
import com.expediagroup.graphql.server.extensions.toExecutionInput
import com.expediagroup.graphql.server.extensions.toGraphQLError
import com.expediagroup.graphql.server.extensions.toGraphQLKotlinType
import com.expediagroup.graphql.server.extensions.toGraphQLResponse
import com.expediagroup.graphql.server.types.GraphQLRequest
import com.expediagroup.graphql.server.types.GraphQLResponse
import graphql.ExecutionResult
import graphql.GraphQL
import graphql.GraphQLContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

/**
 * Default Spring implementation of GraphQL subscription handler.
 */
open class SpringGraphQLSubscriptionHandler(
    private val graphQL: GraphQL,
    private val dataLoaderRegistryFactory: KotlinDataLoaderRegistryFactory? = null
) {

    open fun executeSubscription(
        graphQLRequest: GraphQLRequest,
        graphQLContext: GraphQLContext = GraphQLContext.of(emptyMap<Any, Any>())
    ): Flow<GraphQLResponse<*>> {
        val dataLoaderRegistry = dataLoaderRegistryFactory?.generate()
        val input = graphQLRequest.toExecutionInput(graphQLContext, dataLoaderRegistry)

        return graphQL.execute(input)
            .getData<Flow<ExecutionResult>>()
            .map { result -> result.toGraphQLResponse() }
            .catch { throwable ->
                val error = throwable.toGraphQLError()
                emit(GraphQLResponse<Any?>(errors = listOf(error.toGraphQLKotlinType())))
            }
    }
}
