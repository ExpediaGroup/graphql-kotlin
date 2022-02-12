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

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.web.reactive.socket.WebSocketHandler
import org.springframework.web.reactive.socket.WebSocketSession
import reactor.core.publisher.Mono

/**
 * Default WebSocket handler for handling GraphQL subscriptions.
 */
class SubscriptionWebSocketHandler(
    private val subscriptionHandler: ApolloSubscriptionProtocolHandler,
    private val objectMapper: ObjectMapper
) : WebSocketHandler {

    @Suppress("ForbiddenVoid")
    override fun handle(session: WebSocketSession): Mono<Void> {
        val response = session.receive()
            .flatMap { subscriptionHandler.handle(it.payloadAsText, session) }
            .map { objectMapper.writeValueAsString(it) }
            .map { session.textMessage(it) }

        return session.send(response)
    }

    override fun getSubProtocols(): List<String> = listOf("graphql-ws")
}
