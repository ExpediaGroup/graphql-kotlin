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
import com.expediagroup.graphql.server.spring.subscriptions.SubscriptionOperationMessage.ServerMessages.GQL_COMPLETE
import graphql.GraphQLContext
import org.reactivestreams.Subscription
import org.springframework.web.reactive.socket.WebSocketSession
import reactor.core.publisher.Mono
import java.util.concurrent.ConcurrentHashMap

internal class ApolloSubscriptionSessionState {

    // Sessions are saved by web socket session id
    internal val activeKeepAliveSessions = ConcurrentHashMap<String, Subscription>()

    // Operations are saved by web socket session id, then operation id
    internal val activeOperations = ConcurrentHashMap<String, ConcurrentHashMap<String, Subscription>>()

    // The graphQL context is saved by web socket session id
    private val cachedGraphQLContext = ConcurrentHashMap<String, GraphQLContext>()

    /**
     * Save the context created from the factory and possibly updated in the onConnect hook.
     * This allows us to include some initial state to be used when handling all the messages.
     * This will be removed in [terminateSession].
     */
    fun saveContext(session: WebSocketSession, graphQLContext: GraphQLContext) {
        cachedGraphQLContext[session.id] = graphQLContext
    }

    /**
     * Return the graphQL context for this session.
     */
    fun getGraphQLContext(session: WebSocketSession): GraphQLContext = cachedGraphQLContext[session.id] ?: emptyMap<Any, Any>().toGraphQLContext()

    /**
     * Save the session that is sending keep alive messages.
     * This will override values without cancelling the subscription, so it is the responsibility of the consumer to cancel.
     * These messages will be stopped on [terminateSession].
     */
    fun saveKeepAliveSubscription(session: WebSocketSession, subscription: Subscription) {
        activeKeepAliveSessions[session.id] = subscription
    }

    /**
     * Save the operation that is sending data to the client.
     * This will override values without cancelling the subscription so it is the responsibility of the consumer to cancel.
     * These messages will be stopped on [stopOperation].
     */
    fun saveOperation(session: WebSocketSession, operationMessage: SubscriptionOperationMessage, subscription: Subscription) {
        val id = operationMessage.id
        if (id != null) {
            val operationsForSession: ConcurrentHashMap<String, Subscription> = activeOperations.getOrPut(session.id) { ConcurrentHashMap() }
            operationsForSession[id] = subscription
        }
    }

    /**
     * Send the [GQL_COMPLETE] message.
     * This can happen when the publisher finishes or if the client manually sends the stop message.
     */
    fun completeOperation(session: WebSocketSession, operationMessage: SubscriptionOperationMessage): Mono<SubscriptionOperationMessage> {
        return getCompleteMessage(operationMessage)
            .doFinally { removeActiveOperation(session, operationMessage.id, cancelSubscription = false) }
    }

    /**
     * Stop the subscription sending data and send the [GQL_COMPLETE] message.
     * Does NOT terminate the session.
     */
    fun stopOperation(session: WebSocketSession, operationMessage: SubscriptionOperationMessage): Mono<SubscriptionOperationMessage> {
        return getCompleteMessage(operationMessage)
            .doFinally { removeActiveOperation(session, operationMessage.id, cancelSubscription = true) }
    }

    private fun getCompleteMessage(operationMessage: SubscriptionOperationMessage): Mono<SubscriptionOperationMessage> {
        val id = operationMessage.id
        if (id != null) {
            return Mono.just(SubscriptionOperationMessage(type = GQL_COMPLETE.type, id = id))
        }
        return Mono.empty()
    }

    /**
     * Remove active running subscription from the cache and cancel if needed
     */
    private fun removeActiveOperation(session: WebSocketSession, id: String?, cancelSubscription: Boolean) {
        val operationsForSession = activeOperations[session.id]
        val subscription = operationsForSession?.get(id)
        if (subscription != null) {
            if (cancelSubscription) {
                subscription.cancel()
            }
            operationsForSession.remove(id)
            if (operationsForSession.isEmpty()) {
                activeOperations.remove(session.id)
            }
        }
    }

    /**
     * Terminate the session, cancelling the keep alive messages and all operations active for this session.
     */
    fun terminateSession(session: WebSocketSession) {
        activeOperations[session.id]?.forEach { (_, subscription) -> subscription.cancel() }
        activeOperations.remove(session.id)
        cachedGraphQLContext.remove(session.id)
        activeKeepAliveSessions[session.id]?.cancel()
        activeKeepAliveSessions.remove(session.id)
        session.close()
    }

    /**
     * Looks up the operation for the client, to check if it already exists
     */
    fun doesOperationExist(session: WebSocketSession, operationMessage: SubscriptionOperationMessage): Boolean =
        activeOperations[session.id]?.containsKey(operationMessage.id) ?: false
}
