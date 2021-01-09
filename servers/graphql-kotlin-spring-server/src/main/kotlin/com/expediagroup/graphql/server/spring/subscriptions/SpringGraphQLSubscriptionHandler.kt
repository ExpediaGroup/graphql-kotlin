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

package com.expediagroup.graphql.server.spring.subscriptions

import com.expediagroup.graphql.execution.GraphQLContext
import com.expediagroup.graphql.server.exception.KotlinGraphQLError
import com.expediagroup.graphql.server.extensions.toExecutionInput
import com.expediagroup.graphql.server.extensions.toGraphQLKotlinType
import com.expediagroup.graphql.server.extensions.toGraphQLResponse
import com.expediagroup.graphql.types.GraphQLRequest
import com.expediagroup.graphql.types.GraphQLResponse
import graphql.ExecutionResult
import graphql.GraphQL
import org.reactivestreams.Publisher
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toFlux

/**
 * Default Spring implementation of GraphQL subscription handler.
 */
open class SpringGraphQLSubscriptionHandler(private val graphQL: GraphQL) {

    fun executeSubscription(graphQLRequest: GraphQLRequest, graphQLContext: GraphQLContext?): Flux<GraphQLResponse<*>> = Mono.subscriberContext()
        .flatMapMany {
            graphQL.execute(graphQLRequest.toExecutionInput(graphQLContext))
                .getData<Publisher<ExecutionResult>>()
                .toFlux()
                .map { result -> result.toGraphQLResponse() }
                .onErrorResume { throwable ->
                    val error = KotlinGraphQLError(throwable).toGraphQLKotlinType()
                    Flux.just(GraphQLResponse<Any>(errors = listOf(error)))
                }
        }
}
