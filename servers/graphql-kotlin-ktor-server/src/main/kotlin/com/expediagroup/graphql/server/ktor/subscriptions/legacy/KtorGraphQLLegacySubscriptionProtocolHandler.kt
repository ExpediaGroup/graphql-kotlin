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

package com.expediagroup.graphql.server.ktor.subscriptions.legacy

import com.expediagroup.graphql.server.execution.GraphQLSubscriptionExecutor
import com.expediagroup.graphql.server.ktor.subscriptions.KtorGraphQLSubscriptionHooks
import com.expediagroup.graphql.server.ktor.subscriptions.KtorGraphQLSubscriptionHandler
import com.expediagroup.graphql.server.ktor.subscriptions.SubscriptionOperationMessage
import com.expediagroup.graphql.server.ktor.subscriptions.legacy.LegacyMessageTypes.GQL_COMPLETE
import com.expediagroup.graphql.server.ktor.subscriptions.legacy.LegacyMessageTypes.GQL_CONNECTION_ACK
import com.expediagroup.graphql.server.ktor.subscriptions.legacy.LegacyMessageTypes.GQL_CONNECTION_ERROR
import com.expediagroup.graphql.server.ktor.subscriptions.legacy.LegacyMessageTypes.GQL_CONNECTION_INIT
import com.expediagroup.graphql.server.ktor.subscriptions.legacy.LegacyMessageTypes.GQL_CONNECTION_KEEP_ALIVE
import com.expediagroup.graphql.server.ktor.subscriptions.legacy.LegacyMessageTypes.GQL_CONNECTION_TERMINATE
import com.expediagroup.graphql.server.ktor.subscriptions.legacy.LegacyMessageTypes.GQL_DATA
import com.expediagroup.graphql.server.ktor.subscriptions.legacy.LegacyMessageTypes.GQL_ERROR
import com.expediagroup.graphql.server.ktor.subscriptions.legacy.LegacyMessageTypes.GQL_START
import com.expediagroup.graphql.server.ktor.subscriptions.legacy.LegacyMessageTypes.GQL_STOP
import com.expediagroup.graphql.server.types.GraphQLRequest
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.convertValue
import com.fasterxml.jackson.module.kotlin.readValue
import graphql.GraphQLContext
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import java.util.concurrent.ConcurrentHashMap

/**
 * Implementation of the `subscriptions-transport-ws` protocol defined by Apollo
 * https://github.com/apollographql/subscriptions-transport-ws/blob/master/PROTOCOL.md
 */
open class KtorGraphQLLegacySubscriptionProtocolHandler(
    private val subscriptionExecutor: GraphQLSubscriptionExecutor,
    private val objectMapper: ObjectMapper,
    private val subscriptionHooks: KtorGraphQLSubscriptionHooks,
    private val keepAliveInterval: Long? = null,
) : KtorGraphQLSubscriptionHandler {
    private val logger = LoggerFactory.getLogger(KtorGraphQLLegacySubscriptionProtocolHandler::class.java)

    private val acknowledgeMessage = objectMapper.writeValueAsString(SubscriptionOperationMessage(GQL_CONNECTION_ACK))
    private val keepAliveMessage = objectMapper.writeValueAsString(SubscriptionOperationMessage(GQL_CONNECTION_KEEP_ALIVE))

    override suspend fun handle(session: WebSocketServerSession) {
        logger.debug("New client connected")
        // Session Init Phase
        val context: GraphQLContext = initializeSession(session) ?: return
        session.sendMessage(acknowledgeMessage)
        val keepAliveTicker = keepAliveInterval?.let {
            session.launch {
                while (true) {
                    session.sendMessage(keepAliveMessage)
                    delay(keepAliveInterval)
                }
            }
        }

        // Connected Phase
        val subscriptions: MutableMap<String, Job> = ConcurrentHashMap<String, Job>()
        try {
            while (session.isActive) {
                val message = session.readMessageOrNull() ?: continue
                when (message.type) {
                    GQL_START -> subscriptions.startSubscription(message, session, context)
                    GQL_STOP -> subscriptions.stopSubscription(message, session, context)
                    GQL_CONNECTION_TERMINATE -> {
                        subscriptionHooks.onDisconnect(session, context)
                        session.close(CloseReason(CloseReason.Codes.NORMAL, "Normal Closure"))
                    }

                    else -> session.connectionError("Unexpected operation ${message.type}")
                }
            }
        } catch (ex: ClosedReceiveChannelException) {
            logger.debug("Channel was closed unexpectedly")
            subscriptionHooks.onDisconnect(session, context)
        } catch (ex: Throwable) {
            logger.error("Error on processing GraphQL subscription session", ex)
            session.closeExceptionally(ex)
        } finally {
            keepAliveTicker?.cancel()
        }
    }

    private suspend fun initializeSession(session: WebSocketServerSession): GraphQLContext? {
        val initMessage = session.readMessageOrNull() ?: return null
        if (initMessage.type != GQL_CONNECTION_INIT) {
            session.connectionError("Session init failed. Excepted to get $GQL_CONNECTION_INIT message first")
            return null
        }
        return try {
            subscriptionHooks.onConnect(initMessage.payload, session)
        } catch (ex: Throwable) {
            session.connectionError(ex.message ?: "Error running onConnect hook for operation")
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
            session.connectionError("Missing id from subscription message")
            return
        }
        if (containsKey(message.id)) {
            logger.debug("Already subscribed to operation {}", message.id)
            return
        }
        if (message.payload == null) {
            logger.debug("Missing payload from subscription message={}", message.id)
            session.connectionError("Missing payload from subscription message", message.id)
            return
        }

        val request = try {
            objectMapper.convertValue<GraphQLRequest>(message.payload)
        } catch (ex: Throwable) {
            logger.error("Error when parsing GraphQL request data", ex)
            session.connectionError("Error when parsing GraphQL request data", message.id)
            return
        }

        try {
            subscriptionHooks.onOperation(message.id, request, session, context)
        } catch (ex: Throwable) {
            logger.error("Error when running onOperation hook for operation={}", message.id, ex)
            session.connectionError(ex.message ?: "Error running onOperation hook for operation", message.id)
            return
        }

        val subscriptionJob = session.launch {
            subscriptionExecutor.executeSubscription(request, context)
                .map {
                    if (it.errors?.isNotEmpty() == true) {
                        SubscriptionOperationMessage(type = GQL_ERROR, id = message.id, payload = it)
                    } else {
                        SubscriptionOperationMessage(type = GQL_DATA, id = message.id, payload = it)
                    }
                }
                .onCompletion {
                    try {
                        subscriptionHooks.onOperationComplete(message.id, session, context)
                    } catch (ex: Throwable) {
                        logger.error("Error on calling onOperationDone hook for operation={}", message.id, ex)
                    }
                    emit(SubscriptionOperationMessage(type = GQL_COMPLETE, id = message.id))
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
            session.connectionError("Missing id from subscription message")
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
            connectionError("Expected to get TEXT but got ${frame.frameType.name}")
            return null
        }
        val messageString = frame.readText()
        logger.debug("Received GraphQL subscription message: {}", messageString)
        return try {
            objectMapper.readValue<SubscriptionOperationMessage>(messageString)
        } catch (ex: Exception) {
            logger.error("Error parsing subscription message", ex)
            connectionError("Error parsing subscription message")
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

    private suspend fun WebSocketSession.connectionError(message: String, id: String? = null) {
        sendMessage(SubscriptionOperationMessage(type = GQL_CONNECTION_ERROR, id = id, payload = mapOf("message" to message)))
    }
}
