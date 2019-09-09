/*
 * Copyright 2019 Expedia Group, Inc.
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

package com.expediagroup.graphql.boot.server

import com.expediagroup.graphql.boot.server.exception.SimpleKotlinGraphQLError
import com.expediagroup.graphql.boot.server.model.GraphQLRequest
import com.expediagroup.graphql.boot.server.model.GraphQLResponse
import com.expediagroup.graphql.boot.server.model.toExecutionInput
import com.expediagroup.graphql.boot.server.model.toGraphQLResponse
import graphql.ErrorType
import graphql.GraphQL
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.future.await
import kotlinx.coroutines.reactor.ReactorContext
import kotlin.coroutines.coroutineContext

const val GRAPHQL_CONTEXT_KEY = "graphQLContext"

interface QueryHandler {
    suspend fun executeQuery(request: GraphQLRequest): GraphQLResponse
}

open class SimpleQueryHandler(private val graphql: GraphQL) : QueryHandler {

    @Suppress("TooGenericExceptionCaught")
    @ExperimentalCoroutinesApi
    override suspend fun executeQuery(request: GraphQLRequest): GraphQLResponse {
        val reactorContext = coroutineContext[ReactorContext]
        val graphQLContext = reactorContext?.context?.getOrDefault<Any>(GRAPHQL_CONTEXT_KEY, null)
        val input = request.toExecutionInput(graphQLContext)

        return try {
            graphql.executeAsync(input)
                .await()
                .toGraphQLResponse()
        } catch (e: Exception) {
            val graphQLError = SimpleKotlinGraphQLError(e, ErrorType.DataFetchingException)
            GraphQLResponse(errors = listOf(graphQLError))
        }
    }
}
