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

package com.expediagroup.graphql.server.ktor.subscriptions

import com.expediagroup.graphql.server.execution.GraphQLRequestHandler
import com.expediagroup.graphql.server.execution.subscription.GraphQLWebSocketServer
import com.expediagroup.graphql.server.types.GraphQLSubscriptionStatus
import com.fasterxml.jackson.databind.ObjectMapper
import io.ktor.server.websocket.WebSocketServerSession
import io.ktor.websocket.CloseReason
import io.ktor.websocket.Frame
import io.ktor.websocket.close

/**
 * Ktor GraphQL Web Socket server implementation for handling subscriptions using *graphql-transport-ws* protocol
 *
 * @see <a href="https://github.com/enisdenjo/graphql-ws/blob/master/PROTOCOL.md">graphql-transport-ws protocol</a>
 */
class KtorGraphQLWebSocketServer(
    requestParser: KtorGraphQLSubscriptionRequestParser,
    contextFactory: KtorGraphQLSubscriptionContextFactory,
    subscriptionHooks: KtorGraphQLSubscriptionHooks,
    requestHandler: GraphQLRequestHandler,
    initTimeoutMillis: Long,
    objectMapper: ObjectMapper
) : GraphQLWebSocketServer<WebSocketServerSession, Unit>(
    requestParser, contextFactory, subscriptionHooks, requestHandler, initTimeoutMillis, objectMapper
) {
    override suspend fun closeSession(session: WebSocketServerSession, reason: GraphQLSubscriptionStatus) {
        session.close(CloseReason(reason.code.toShort(), reason.reason))
    }

    override suspend fun sendSubscriptionMessage(session: WebSocketServerSession, message: String) {
        session.outgoing.send(Frame.Text(message))
    }
}
