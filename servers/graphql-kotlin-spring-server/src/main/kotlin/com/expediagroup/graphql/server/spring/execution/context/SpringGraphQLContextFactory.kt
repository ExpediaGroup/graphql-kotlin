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
import com.expediagroup.graphql.server.execution.context.GraphQLContextFactory
import com.expediagroup.graphql.server.types.GraphQLServerRequest
import graphql.GraphQLContext
import org.springframework.web.reactive.function.server.ServerRequest

/**
 * interface to handle the Spring [ServerRequest]
 */
interface SpringGraphQLContextFactory : GraphQLContextFactory<ServerRequest>

/**
 * Basic implementation of [SpringGraphQLContextFactory] that populates Apollo tracing header.
 */
open class DefaultSpringGraphQLContextFactory : SpringGraphQLContextFactory {
    override suspend fun generateContext(
        request: ServerRequest,
        graphQLRequest: GraphQLServerRequest
    ): GraphQLContext =
        mutableMapOf<Any, Any>().also { map ->
            request.headers().firstHeader(FEDERATED_TRACING_HEADER_NAME)?.let { headerValue ->
                map[FEDERATED_TRACING_HEADER_NAME] = headerValue
            }
        }.toGraphQLContext()
}
