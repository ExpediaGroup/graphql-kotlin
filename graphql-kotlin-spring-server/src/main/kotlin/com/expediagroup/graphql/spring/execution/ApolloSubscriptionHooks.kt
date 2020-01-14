/*
 * Copyright 2019 Expedia, Inc
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
package com.expediagroup.graphql.spring.execution

import com.expediagroup.graphql.spring.model.SubscriptionOperationMessage
import kotlinx.coroutines.reactor.mono
import org.springframework.web.reactive.socket.WebSocketSession
import reactor.core.publisher.Mono

/**
 * Implementation of Apollo Subscription Server Lifecycle Events
 * https://www.apollographql.com/docs/graphql-subscriptions/lifecycle-events/
 */
interface ApolloSubscriptionHooks {
    /**
     * Allows validation of connectionParams prior to starting the connection.
     * You can reject the connection by throwing an exception
     */
    fun onConnect(connectionParams: Map<String, String>, session: WebSocketSession, graphQLContext: Any?): Mono<Unit> =
        mono {
            // potential example:
            // val token = connectionParams["Authorization"] ?: throw Exception("Unauthorized")
            // validateToken(token)
        }

    /**
     * Called when the client executes a GraphQL operation
     */
    fun onOperation(
        operationMessage: SubscriptionOperationMessage,
        session: WebSocketSession,
        graphQLContext: Any?
    ): Mono<Unit> = mono { }

    /**
     * Called when client's unsubscribes
     */
    fun onOperationComplete(session: WebSocketSession): Mono<Unit> = mono { }

    /**
     * Called when the client disconnects
     */
    fun onDisconnect(session: WebSocketSession, graphQLContext: Any?): Mono<Unit> = mono { }
}

/**
 * Default implementation of Apollo Subscription Lifecycle Events.
 */
open class SimpleSubscriptionHooks : ApolloSubscriptionHooks
