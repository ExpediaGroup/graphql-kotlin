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
import kotlin.coroutines.coroutineContext

/**
 * The query handler is the first level that accepts both GraphQL query and mutation requests. This is the top level execution
 * which may trigger many more data fetchers in your schema, each of which may have their own exception handling.
 */
interface QueryHandler {

    /**
     * Execute GraphQL request (query or mutation) in a non-blocking fashion.
     */
    suspend fun executeQuery(request: GraphQLRequest): GraphQLResponse
}

/**
* Default GraphQL query handler that executes queries and mutations asynchronously. All methods are open for extension if you want to
 * override just parts of the request handler behaviour.
 */
open class SimpleQueryHandler(private val graphql: GraphQL, private val dataLoaderRegistryFactory: DataLoaderRegistryFactory? = null) : QueryHandler {

    /**
     * Execute a request passing in the GraphQL context from the coroutine context and the [DataLoaderRegistryFactory] if not null.
     */
    @Suppress("TooGenericExceptionCaught")
    @ExperimentalCoroutinesApi
    override suspend fun executeQuery(request: GraphQLRequest): GraphQLResponse {
        val reactorContext = coroutineContext[ReactorContext]
        val graphQLContext = reactorContext?.context?.getOrDefault<Any>(GRAPHQL_CONTEXT_KEY, null)
        val input = request.toExecutionInput(graphQLContext, dataLoaderRegistryFactory?.generate())

        return try {
            graphql.executeAsync(input)
                .await()
                .toGraphQLResponse()
        } catch (exception: Exception) {
            handleException(exception)
        }
    }

    /**
     * If [executeQuery] catches an exception while executing the request, this method will be called and map the exception to a basic [GraphQLResponse] with errors.
     * If you wish to change the request exception handler behaviour but not the execution, you can just override this method.
     */
    open fun handleException(exception: Exception): GraphQLResponse {
        val graphQLError = SimpleKotlinGraphQLError(exception)
        return GraphQLResponse(errors = listOf(graphQLError))
    }
}
