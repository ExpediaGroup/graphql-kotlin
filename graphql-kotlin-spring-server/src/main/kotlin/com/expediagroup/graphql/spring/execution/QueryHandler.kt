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

import com.expediagroup.graphql.spring.exception.SimpleKotlinGraphQLError
import com.expediagroup.graphql.spring.model.GraphQLRequest
import com.expediagroup.graphql.spring.model.GraphQLResponse
import com.expediagroup.graphql.spring.model.toExecutionInput
import com.expediagroup.graphql.spring.model.toGraphQLResponse
import graphql.GraphQL
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.future.await
import kotlinx.coroutines.reactor.ReactorContext
import org.dataloader.DataLoaderRegistry
import kotlin.coroutines.coroutineContext

/**
 * GraphQL query handler.
 */
interface QueryHandler {

    /**
     * Execute GraphQL query in a non-blocking fashion.
     */
    suspend fun executeQuery(request: GraphQLRequest): GraphQLResponse
}

/**
 * Default GraphQL query handler.
 */
open class SimpleQueryHandler(private val graphql: GraphQL, private val dataLoaderRegistry: DataLoaderRegistry? = null) : QueryHandler {

    @Suppress("TooGenericExceptionCaught")
    @ExperimentalCoroutinesApi
    override suspend fun executeQuery(request: GraphQLRequest): GraphQLResponse {
        val reactorContext = coroutineContext[ReactorContext]
        val graphQLContext = reactorContext?.context?.getOrDefault<Any>(GRAPHQL_CONTEXT_KEY, null)
        val input = request.toExecutionInput(graphQLContext, dataLoaderRegistry)

        return try {
            graphql.executeAsync(input)
                .await()
                .toGraphQLResponse()
        } catch (e: Exception) {
            val graphQLError = SimpleKotlinGraphQLError(e)
            GraphQLResponse(errors = listOf(graphQLError))
        }
    }
}
