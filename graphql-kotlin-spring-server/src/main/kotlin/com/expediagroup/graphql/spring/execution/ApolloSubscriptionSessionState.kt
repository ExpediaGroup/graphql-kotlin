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
import org.reactivestreams.Subscription
import org.springframework.web.reactive.socket.WebSocketSession
import java.util.concurrent.ConcurrentHashMap

internal class ApolloSubscriptionSessionState {

    // Sessions are saved by web socket session id
    internal val activeKeepAliveSessions = ConcurrentHashMap<String, Subscription>()

    // Operations are saved by web socket session id, then operation id
    internal val activeOperations = ConcurrentHashMap<String, ConcurrentHashMap<String, Subscription>>()

    /**
     * Save the session that is sending keep alive messages.
     * This will override values without cancelling the subscription so it is the responsbility of the consumer to cancel.
     * These messages will be stopped on [terminateSession].
     */
    fun saveKeepAliveSubscription(session: WebSocketSession, subscription: Subscription) {
        activeKeepAliveSessions[session.id] = subscription
    }

    /**
     * Save the operation that is sending data to the client.
     * This will override values without cancelling the subscription so it is the responsbility of the consumer to cancel.
     * These messages will be stopped on [stopOperation].
     */
    fun saveOperation(session: WebSocketSession, operationMessage: SubscriptionOperationMessage, subscription: Subscription) {
        if (operationMessage.id != null) {
            val operationsForSession: ConcurrentHashMap<String, Subscription> = activeOperations.getOrPut(session.id) { ConcurrentHashMap() }
            operationsForSession[operationMessage.id] = subscription
        }
    }

    /**
     * Stop the subscription sending data. Does NOT terminate the session.
     */
    fun stopOperation(session: WebSocketSession, operationMessage: SubscriptionOperationMessage) {
        if (operationMessage.id != null) {
            val operationsForSession = activeOperations[session.id]
            operationsForSession?.get(operationMessage.id)?.cancel()
            operationsForSession?.remove(operationMessage.id)
        }
    }

    /**
     * Terminate the session, cancelling the keep alive messages and all operations active for this session.
     */
    fun terminateSession(session: WebSocketSession) {
        activeOperations[session.id]?.forEach { _, subscription -> subscription.cancel() }
        activeOperations.remove(session.id)
        activeKeepAliveSessions[session.id]?.cancel()
        activeKeepAliveSessions.remove(session.id)
        session.close()
    }
}
