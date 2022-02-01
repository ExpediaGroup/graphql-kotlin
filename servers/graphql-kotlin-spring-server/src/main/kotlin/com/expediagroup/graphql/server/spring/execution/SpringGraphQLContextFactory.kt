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

package com.expediagroup.graphql.server.spring.execution

import com.apollographql.federation.graphqljava.tracing.FederatedTracingInstrumentation.FEDERATED_TRACING_HEADER_NAME
import com.expediagroup.graphql.server.execution.GraphQLContextFactory
import org.springframework.web.reactive.function.server.ServerRequest

/**
 * Wrapper class for specifically handling the Spring [ServerRequest]
 */
abstract class SpringGraphQLContextFactory<out T : SpringGraphQLContext> : GraphQLContextFactory<T, ServerRequest>

/**
 * Basic implementation of [SpringGraphQLContextFactory] that populates Apollo tracing header.
 */
open class DefaultSpringGraphQLContextFactory : SpringGraphQLContextFactory<SpringGraphQLContext>() {
    override suspend fun generateContextMap(request: ServerRequest): Map<*, Any> = mutableMapOf<Any, Any>()
        .also { map ->
            request.headers().firstHeader(FEDERATED_TRACING_HEADER_NAME)?.let { headerValue ->
                map[FEDERATED_TRACING_HEADER_NAME] = headerValue
            }
        }
}
