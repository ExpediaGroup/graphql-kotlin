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
import graphql.ExecutionResult
import graphql.GraphQL
import org.reactivestreams.Publisher
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toFlux

/**
 * GraphQL subscription handler.
 */
interface SubscriptionHandler {

    /**
     * Execute GraphQL subscription request and return a Reactor Flux Publisher that emits 0 to N [GraphQLResponse]s.
     */
    fun executeSubscription(graphQLRequest: GraphQLRequest): Flux<GraphQLResponse>
}

/**
 * Default implementation of GraphQL subscription handler.
 */
open class SimpleSubscriptionHandler(private val graphQL: GraphQL) : SubscriptionHandler {

    override fun executeSubscription(graphQLRequest: GraphQLRequest): Flux<GraphQLResponse> = Mono.subscriberContext()
        .flatMapMany { reactorContext ->
            val graphQLContext = reactorContext.getOrDefault<Any>(GRAPHQL_CONTEXT_KEY, null)
            graphQL.execute(graphQLRequest.toExecutionInput(graphQLContext))
                .getData<Publisher<ExecutionResult>>()
                .toFlux()
                .map { result -> result.toGraphQLResponse() }
                .onErrorResume { error -> Flux.just(GraphQLResponse(errors = listOf(SimpleKotlinGraphQLError(error)))) }
        }
}
