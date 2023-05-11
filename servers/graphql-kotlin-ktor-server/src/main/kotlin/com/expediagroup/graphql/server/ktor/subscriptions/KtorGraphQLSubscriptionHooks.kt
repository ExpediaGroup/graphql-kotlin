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

import com.expediagroup.graphql.generator.extensions.toGraphQLContext
import com.expediagroup.graphql.server.types.GraphQLRequest
import graphql.GraphQLContext
import io.ktor.server.websocket.*
import io.ktor.websocket.*

/**
 * Implementation of Apollo Subscription Server Lifecycle Events
 * https://www.apollographql.com/docs/graphql-subscriptions/lifecycle-events/
 */
interface KtorGraphQLSubscriptionHooks {
    /**
     * Allows validation of connectionParams prior to starting the connection.
     * You can reject the connection by throwing an exception.
     */
    fun onConnect(
        connectionParams: Any?,
        session: WebSocketServerSession,
    ): GraphQLContext = emptyMap<Any, Any>().toGraphQLContext()

    /**
     * Called when the client executes a GraphQL operation.
     * The context here is what returned from [onConnect] earlier.
     */
    fun onOperation(
        operationId: String,
        payload: GraphQLRequest,
        session: WebSocketSession,
        graphQLContext: GraphQLContext,
    ): Unit = Unit

    /**
     * Called when client unsubscribes
     */
    fun onOperationComplete(
        operationId: String,
        session: WebSocketSession,
        graphQLContext: GraphQLContext,
    ): Unit = Unit

    /**
     * Called when the client disconnects
     */
    fun onDisconnect(
        session: WebSocketSession,
        graphQLContext: GraphQLContext
    ): Unit = Unit
}

/**
 * Default implementation of Apollo Subscription Lifecycle Events (No-op).
 */
open class DefaultKtorGraphQLLegacySubscriptionHooks : KtorGraphQLSubscriptionHooks
