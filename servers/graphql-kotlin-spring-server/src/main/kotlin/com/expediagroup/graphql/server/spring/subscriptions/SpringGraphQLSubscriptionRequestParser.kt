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

import com.expediagroup.graphql.server.execution.subscription.GraphQLSubscriptionRequestParser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow
import org.springframework.web.reactive.socket.WebSocketSession

/**
 * Spring specific version of WebSocket subscription request parser.
 */
interface SpringGraphQLSubscriptionRequestParser : GraphQLSubscriptionRequestParser<WebSocketSession>

class DefaultWebSocketGraphQLRequestParser : SpringGraphQLSubscriptionRequestParser {
    override suspend fun parseRequestFlow(session: WebSocketSession): Flow<String> = session.receive()
        .map { it.payloadAsText }
        .asFlow()
}
