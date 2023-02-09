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

package com.expediagroup.graphql.server.ktor.execution.context

import com.apollographql.federation.graphqljava.tracing.FederatedTracingInstrumentation
import com.expediagroup.graphql.generator.extensions.toGraphQLContext
import com.expediagroup.graphql.server.execution.context.GraphQLContextBuilder
import com.expediagroup.graphql.server.execution.context.GraphQLContextEntryProducer
import com.expediagroup.graphql.server.types.GraphQLServerRequest
import graphql.GraphQLContext
import io.ktor.server.request.ApplicationRequest
import io.ktor.server.request.header

/**
 *  Ktor specific [ApplicationRequest] context builder
 */
interface KtorGraphQLContextBuilder : GraphQLContextBuilder<ApplicationRequest>

/**
 * Basic implementation of [KtorGraphQLContextBuilder] that populates Apollo tracing header.
 */
open class DefaultKtorGraphQLContextBuilder(
    override val producers: List<GraphQLContextEntryProducer<ApplicationRequest, Any, *>>
) : KtorGraphQLContextBuilder {

    constructor(vararg entryFactories: GraphQLContextEntryProducer<ApplicationRequest, Any, *>) : this(entryFactories.toList())

    override suspend fun generateContext(
        request: ApplicationRequest,
        graphQLRequest: GraphQLServerRequest
    ): GraphQLContext =
        (producers + tracingHeaderEntryProducer)
            .fold(mutableMapOf<Any, Any?>()) { accumulator, entryFactory ->
                accumulator.also {
                    entryFactory.invoke(request, graphQLRequest, accumulator)?.let { entry ->
                        accumulator += entry
                    }
                }
            }
            .toGraphQLContext()

    companion object {
        val tracingHeaderEntryProducer: GraphQLContextEntryProducer<ApplicationRequest, Any, *> =
            GraphQLContextEntryProducer { request, _, _ ->
                request.header(FederatedTracingInstrumentation.FEDERATED_TRACING_HEADER_NAME)?.let { headerValue ->
                    FederatedTracingInstrumentation.FEDERATED_TRACING_HEADER_NAME to headerValue
                }
            }
    }
}
