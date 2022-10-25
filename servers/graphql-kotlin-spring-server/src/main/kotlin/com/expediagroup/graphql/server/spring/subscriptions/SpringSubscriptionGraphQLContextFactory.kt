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

package com.expediagroup.graphql.server.spring.subscriptions

import com.expediagroup.graphql.generator.extensions.toGraphQLContext
import com.expediagroup.graphql.server.execution.GraphQLContextFactory
import graphql.GraphQLContext
import org.springframework.web.reactive.socket.WebSocketSession

/**
 * Spring specific code to generate the context for a subscription request
 */
abstract class SpringSubscriptionGraphQLContextFactory : GraphQLContextFactory<WebSocketSession>

/**
 * Basic implementation of [SpringSubscriptionGraphQLContextFactory] that just returns null
 */
class DefaultSpringSubscriptionGraphQLContextFactory : SpringSubscriptionGraphQLContextFactory() {
    override suspend fun generateContext(request: WebSocketSession): GraphQLContext = emptyMap<Any, Any>().toGraphQLContext()
}
