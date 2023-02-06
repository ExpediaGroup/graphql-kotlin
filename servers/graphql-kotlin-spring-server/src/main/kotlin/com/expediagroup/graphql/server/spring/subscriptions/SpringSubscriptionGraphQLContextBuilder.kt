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

package com.expediagroup.graphql.server.spring.subscriptions

import com.expediagroup.graphql.server.execution.context.GraphQLContextBuilder
import com.expediagroup.graphql.server.execution.context.GraphQLContextEntryProducer
import org.springframework.web.reactive.socket.WebSocketSession

/**
 * Spring specific code to generate the context for a subscription request
 */
interface SpringSubscriptionGraphQLContextBuilder : GraphQLContextBuilder<WebSocketSession>

/**
 * Basic implementation of [SpringSubscriptionGraphQLContextBuilder]
 */
class DefaultSpringSubscriptionGraphQLContextBuilder(
    override val producers: List<GraphQLContextEntryProducer<WebSocketSession, Any, *>>
) : SpringSubscriptionGraphQLContextBuilder {
    constructor(vararg entryFactories: GraphQLContextEntryProducer<WebSocketSession, Any, *>) : this(entryFactories.toList())
}
