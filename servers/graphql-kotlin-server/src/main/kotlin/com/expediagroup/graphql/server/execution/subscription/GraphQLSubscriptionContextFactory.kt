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

package com.expediagroup.graphql.server.execution.subscription

import com.expediagroup.graphql.generator.extensions.toGraphQLContext
import graphql.GraphQLContext

/**
 * Factory that generates a GraphQL context for GraphQL WS subscriptions.
 */
interface GraphQLSubscriptionContextFactory<Session> {
    /**
     * Generate GraphQL context based on the WebSocket session and connection-init params.
     * If no context should be generated and used in the request, return context from empty map.
     */
    suspend fun generateContext(session: Session, params: Any?): GraphQLContext = emptyMap<Any, Any>().toGraphQLContext()
}
