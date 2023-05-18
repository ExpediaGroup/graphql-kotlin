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

package com.expediagroup.graphql.server.ktor.subscriptions.graphqlws

import com.expediagroup.graphql.server.execution.GraphQLSubscriptionExecutor
import com.expediagroup.graphql.server.ktor.subscriptions.KtorGraphQLSubscriptionHandler
import com.expediagroup.graphql.server.ktor.subscriptions.KtorGraphQLSubscriptionHooks
import com.expediagroup.graphql.server.ktor.subscriptions.SubscriptionOperationMessage
import com.expediagroup.graphql.server.types.GraphQLRequest
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.convertValue
import com.fasterxml.jackson.module.kotlin.readValue
import graphql.GraphQLContext
import io.ktor.server.websocket.WebSocketServerSession
import io.ktor.websocket.CloseReason
import io.ktor.websocket.Frame
import io.ktor.websocket.WebSocketSession
import io.ktor.websocket.close
import io.ktor.websocket.closeExceptionally
import io.ktor.websocket.readText
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import java.util.concurrent.ConcurrentHashMap

/**
 * Implementation of the `graphql-transport-ws` protocol
 * https://github.com/enisdenjo/graphql-ws/blob/master/PROTOCOL.md
 */
open class KtorGraphQLWebSocketProtocolHandler(
    private val subscriptionExecutor: GraphQLSubscriptionExecutor,
    private val objectMapper: ObjectMapper,
    private val subscriptionHooks: KtorGraphQLSubscriptionHooks,
) : KtorGraphQLSubscriptionHandler {
    private val logger = LoggerFactory.getLogger(KtorGraphQLWebSocketProtocolHandler::class.java)

    private val acknowledgeMessage = objectMapper.writeValueAsString(SubscriptionOperationMessage(MessageTypes.GQL_CONNECTION_ACK))
    private val pongMessage = objectMapper.writeValueAsString(SubscriptionOperationMessage(MessageTypes.GQL_PONG))

    override suspend fun handle(session: WebSocketServerSession) {
        logger.debug("New client connected")
        // Session Init Phase
        val context: GraphQLContext = initializeSession(session) ?: return
        session.sendMessage(acknowledgeMessage)

        // Connected Phase
        val subscriptions: MutableMap<String, Job> = ConcurrentHashMap<String, Job>()
        try {
            while (session.isActive) {
                val message = session.readMessageOrNull() ?: continue
                when (message.type) {
                    MessageTypes.GQL_PING -> session.sendMessage(pongMessage)
                    MessageTypes.GQL_PONG -> {}
                    MessageTypes.GQL_SUBSCRIBE -> subscriptions.startSubscription(message, session, context)
                    MessageTypes.GQL_COMPLETE -> subscriptions.stopSubscription(message, session, context)
                    else -> session.closeAsInvalidMessage("Unexpected operation ${message.type}")
                }
            }
        } catch (ex: ClosedReceiveChannelException) {
            logger.debug("Client disconnected")
            subscriptionHooks.onDisconnect(session, context)
        } catch (ex: Throwable) {
            logger.error("Error on processing GraphQL subscription session", ex)
            session.closeExceptionally(ex)
        }
    }

    private suspend fun initializeSession(session: WebSocketServerSession): GraphQLContext? {
        val initMessage = session.readMessageOrNull() ?: return null
        if (initMessage.type != MessageTypes.GQL_CONNECTION_INIT) {
            session.close(CloseReason(4401, "Unauthorized"))
            return null
        }
        return try {
            subscriptionHooks.onConnect(initMessage.payload, session)
        } catch (ex: Throwable) {
            logger.debug("Got error from onConnect hook, closing session", ex)
            session.close(CloseReason(4403, "Forbidden"))
            null
        }
    }

    private suspend fun MutableMap<String, Job>.startSubscription(
        message: SubscriptionOperationMessage,
        session: WebSocketServerSession,
        context: GraphQLContext
    ) {
        if (message.id == null) {
            logger.debug("Missing id from subscription message")
            session.closeAsInvalidMessage("Missing id from subscription message")
            return
        }
        if (containsKey(message.id)) {
            logger.debug("Already subscribed to operation {}", message.id)
            session.close(CloseReason(4409, "Subscriber for ${message.id} already exists"))
            return
        }
        if (message.payload == null) {
            logger.debug("Missing payload from subscription message={}", message.id)
            session.closeAsInvalidMessage("Missing payload from subscription message")
            return
        }

        val request = try {
            objectMapper.convertValue<GraphQLRequest>(message.payload)
        } catch (ex: Throwable) {
            logger.error("Error when parsing GraphQL request data", ex)
            session.closeAsInvalidMessage("Error when parsing GraphQL request data")
            return
        }

        try {
            subscriptionHooks.onOperation(message.id, request, session, context)
        } catch (ex: Throwable) {
            logger.error("Error when running onOperation hook for operation={}", message.id, ex)
            session.closeAsInvalidMessage(ex.message ?: "Error running onOperation hook for operation=${message.id}")
            return
        }

        val subscriptionJob = session.launch {
            subscriptionExecutor.executeSubscription(request, context)
                .map {
                    if (it.errors?.isNotEmpty() == true) {
                        SubscriptionOperationMessage(type = MessageTypes.GQL_ERROR, id = message.id, payload = it)
                    } else {
                        SubscriptionOperationMessage(type = MessageTypes.GQL_NEXT, id = message.id, payload = it)
                    }
                }
                .onCompletion {
                    try {
                        subscriptionHooks.onOperationComplete(message.id, session, context)
                    } catch (ex: Throwable) {
                        logger.error("Error on calling onOperationDone hook for operation={}", message.id, ex)
                    }
                    emit(SubscriptionOperationMessage(type = MessageTypes.GQL_COMPLETE, id = message.id))
                }
                .collect { session.sendMessage(it) }
        }

        put(message.id, subscriptionJob)

        subscriptionJob.invokeOnCompletion { remove(message.id) }
    }

    private suspend fun MutableMap<String, Job>.stopSubscription(
        message: SubscriptionOperationMessage,
        session: WebSocketServerSession,
        context: GraphQLContext,
    ) {
        if (message.id == null) {
            session.closeAsInvalidMessage("Missing id from subscription message")
            return
        }
        val subscriptionJob = remove(message.id) ?: run {
            logger.debug("Operation not found by id={}", message.id)
            return
        }
        try {
            subscriptionHooks.onOperationComplete(message.id, session, context)
        } catch (ex: Throwable) {
            logger.error("Error on calling onOperationDone hook for operation={}", message.id, ex)
        } finally {
            subscriptionJob.cancel()
        }
    }

    private suspend fun WebSocketSession.readMessageOrNull(): SubscriptionOperationMessage? {
        val frame = incoming.receive()
        if (frame !is Frame.Text) {
            closeAsInvalidMessage("Expected to get TEXT but got ${frame.frameType.name}")
            return null
        }
        val messageString = frame.readText()
        logger.debug("Received GraphQL subscription message: {}", messageString)
        return try {
            objectMapper.readValue<SubscriptionOperationMessage>(messageString)
        } catch (ex: Exception) {
            logger.error("Error parsing subscription message", ex)
            closeAsInvalidMessage(ex.message ?: "Error parsing subscription message")
            null
        }
    }

    private suspend fun WebSocketSession.sendMessage(message: SubscriptionOperationMessage) {
        val json = objectMapper.writeValueAsString(message)
        sendMessage(json)
    }

    private suspend fun WebSocketSession.sendMessage(message: String) {
        logger.debug("Sending GraphQL server message {}", message)
        outgoing.send(Frame.Text(message))
    }

    private suspend fun WebSocketSession.closeAsInvalidMessage(message: String) = close(CloseReason(4400, message))
}
