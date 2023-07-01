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

import com.expediagroup.graphql.server.execution.GraphQLRequestHandler
import com.expediagroup.graphql.server.execution.subscription.GRAPHQL_WS_PROTOCOL
import com.expediagroup.graphql.server.execution.subscription.GraphQLWebSocketServer
import com.expediagroup.graphql.server.types.GraphQLSubscriptionStatus
import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.reactor.flux
import org.springframework.web.reactive.socket.CloseStatus
import org.springframework.web.reactive.socket.WebSocketHandler
import org.springframework.web.reactive.socket.WebSocketMessage
import org.springframework.web.reactive.socket.WebSocketSession
import reactor.core.publisher.Mono

/**
 * GraphQL Web Socket server implementation for handling subscriptions using *graphql-transport-ws* protocol
 *
 * @see <a href="https://github.com/enisdenjo/graphql-ws/blob/master/PROTOCOL.md">graphql-transport-ws protocol</a>
 */
class SubscriptionWebSocketHandler(
    requestParser: SpringGraphQLSubscriptionRequestParser,
    contextFactory: SpringSubscriptionGraphQLContextFactory,
    subscriptionHooks: SpringGraphQLSubscriptionHooks,
    graphqlHandler: GraphQLRequestHandler,
    initTimeoutMillis: Long,
    objectMapper: ObjectMapper
) : WebSocketHandler, GraphQLWebSocketServer<WebSocketSession, WebSocketMessage>(
    requestParser, contextFactory, subscriptionHooks, graphqlHandler, initTimeoutMillis, objectMapper
) {
    override fun handle(session: WebSocketSession): Mono<Void> = session.send(
        flux {
            handleSubscription(session).collect {
                send(it)
            }
        }
    )

    override suspend fun closeSession(session: WebSocketSession, reason: GraphQLSubscriptionStatus) {
        session.close(CloseStatus(reason.code, reason.reason)).awaitFirst()
    }
    override suspend fun sendSubscriptionMessage(session: WebSocketSession, message: String): WebSocketMessage =
        session.textMessage(message)
    override fun getSubProtocols(): List<String> = listOf(GRAPHQL_WS_PROTOCOL)
}
