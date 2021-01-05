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

import com.expediagroup.graphql.server.execution.GraphQLRequestHandler
import com.expediagroup.graphql.server.extensions.toExecutionInput
import com.expediagroup.graphql.server.extensions.toGraphQLKotlinType
import com.expediagroup.graphql.server.extensions.toGraphQLResponse
import com.expediagroup.graphql.server.exception.KotlinGraphQLError
import com.expediagroup.graphql.types.GraphQLRequest
import com.expediagroup.graphql.types.GraphQLResponse
import graphql.GraphQL
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.future.await
import kotlinx.coroutines.reactor.ReactorContext
import kotlin.coroutines.coroutineContext

/**
 * Default Spring GraphQL request handler.
 *
 * It handles populating the coroutine context from Reactor
 */
open class SpringGraphQLRequestHandler(
    private val graphql: GraphQL,
    private val dataLoaderRegistryFactory: DataLoaderRegistryFactory? = null
) : GraphQLRequestHandler {

    @Suppress("TooGenericExceptionCaught")
    @ExperimentalCoroutinesApi
    override suspend fun executeRequest(request: GraphQLRequest): GraphQLResponse<*> {
        val reactorContext = coroutineContext[ReactorContext]
        val graphQLContext = reactorContext?.context?.getOrDefault<Any>(GRAPHQL_CONTEXT_KEY, null)
        val input = request.toExecutionInput(graphQLContext, dataLoaderRegistryFactory?.generate())

        return try {
            graphql.executeAsync(input)
                .await()
                .toGraphQLResponse()
        } catch (exception: Exception) {
            val graphKotlinQLError = KotlinGraphQLError(exception)
            GraphQLResponse<Any?>(errors = listOf(graphKotlinQLError.toGraphQLKotlinType()))
        }
    }
}
