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

import graphql.GraphQLContext
import org.springframework.web.reactive.socket.WebSocketSession

/**
 * Implementation of Apollo Subscription Server Lifecycle Events
 * https://www.apollographql.com/docs/graphql-subscriptions/lifecycle-events/
 */
interface ApolloSubscriptionHooks {
    /**
     * Allows validation of connectionParams prior to starting the connection.
     * You can reject the connection by throwing an exception.
     * If you need to forward state to execution, update and return the context map.
     */
    fun onConnectWithContext(
        connectionParams: Map<String, String>,
        session: WebSocketSession,
        graphQLContext: GraphQLContext
    ): GraphQLContext = graphQLContext

    /**
     * Called when the client executes a GraphQL operation.
     * The context can not be updated here, it is read only.
     */
    fun onOperationWithContext(
        operationMessage: SubscriptionOperationMessage,
        session: WebSocketSession,
        graphQLContext: GraphQLContext
    ): Unit = Unit

    /**
     * Called when client's unsubscribes
     */
    fun onOperationComplete(session: WebSocketSession): Unit = Unit

    /**
     * Called when the client disconnects
     */
    fun onDisconnect(session: WebSocketSession): Unit = Unit
}

/**
 * Default implementation of Apollo Subscription Lifecycle Events.
 */
open class SimpleSubscriptionHooks : ApolloSubscriptionHooks
