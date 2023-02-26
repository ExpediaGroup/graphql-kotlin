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

package com.expediagroup.graphql.server.ktor

import com.apollographql.federation.graphqljava.tracing.FederatedTracingInstrumentation.FEDERATED_TRACING_HEADER_NAME
import com.expediagroup.graphql.generator.extensions.toGraphQLContext
import com.expediagroup.graphql.server.execution.GraphQLContextFactory
import io.ktor.server.request.ApplicationRequest
import graphql.GraphQLContext
import io.ktor.server.request.header

/**
 * Wrapper class for specifically handling the Ktor [ApplicationRequest]
 */
abstract class KtorGraphQLContextFactory : GraphQLContextFactory<ApplicationRequest>

/**
 * Basic implementation of [KtorGraphQLContextFactory] that populates Apollo tracing header.
 */
open class DefaultKtorGraphQLContextFactory : KtorGraphQLContextFactory() {
    override suspend fun generateContext(request: ApplicationRequest): GraphQLContext =
        mutableMapOf<Any, Any>().also { map ->
            request.header(FEDERATED_TRACING_HEADER_NAME)?.let { headerValue ->
                map[FEDERATED_TRACING_HEADER_NAME] = headerValue
            }
        }.toGraphQLContext()
}
