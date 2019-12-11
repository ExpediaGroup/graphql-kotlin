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

import com.expediagroup.graphql.spring.GraphQLConfigurationProperties
import com.expediagroup.graphql.spring.model.GraphQLRequest
import com.expediagroup.graphql.spring.model.SubscriptionOperationMessage
import com.expediagroup.graphql.spring.model.SubscriptionOperationMessage.ClientMessages.GQL_CONNECTION_INIT
import com.expediagroup.graphql.spring.model.SubscriptionOperationMessage.ClientMessages.GQL_CONNECTION_TERMINATE
import com.expediagroup.graphql.spring.model.SubscriptionOperationMessage.ClientMessages.GQL_START
import com.expediagroup.graphql.spring.model.SubscriptionOperationMessage.ClientMessages.GQL_STOP
import com.expediagroup.graphql.spring.model.SubscriptionOperationMessage.ServerMessages.GQL_COMPLETE
import com.expediagroup.graphql.spring.model.SubscriptionOperationMessage.ServerMessages.GQL_CONNECTION_ACK
import com.expediagroup.graphql.spring.model.SubscriptionOperationMessage.ServerMessages.GQL_CONNECTION_ERROR
import com.expediagroup.graphql.spring.model.SubscriptionOperationMessage.ServerMessages.GQL_CONNECTION_KEEP_ALIVE
import com.expediagroup.graphql.spring.model.SubscriptionOperationMessage.ServerMessages.GQL_DATA
import com.expediagroup.graphql.spring.model.SubscriptionOperationMessage.ServerMessages.GQL_ERROR
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.convertValue
import com.fasterxml.jackson.module.kotlin.readValue
import org.reactivestreams.Subscription
import org.slf4j.LoggerFactory
import org.springframework.web.reactive.socket.WebSocketSession
import reactor.core.publisher.Flux
import java.time.Duration
import java.util.concurrent.ConcurrentHashMap

/**
 * Implementation of the `graphql-ws` protocol defined by Apollo
 * https://github.com/apollographql/subscriptions-transport-ws/blob/master/PROTOCOL.md
 */
class ApolloSubscriptionProtocolHandler(
    private val config: GraphQLConfigurationProperties,
    private val subscriptionHandler: SubscriptionHandler,
    private val objectMapper: ObjectMapper
) {
    // Sessions are saved by web socket session id
    private val activeSessions = ConcurrentHashMap<String, Subscription>()
    // Operations are saved by web socket session id, then operation id
    private val activeOperations = ConcurrentHashMap<String, ConcurrentHashMap<String, Subscription>>()

    private val logger = LoggerFactory.getLogger(ApolloSubscriptionProtocolHandler::class.java)
    private val keepAliveMessage = SubscriptionOperationMessage(type = GQL_CONNECTION_KEEP_ALIVE.type)
    private val basicConnectionErrorMessage = SubscriptionOperationMessage(type = GQL_CONNECTION_ERROR.type)
    private val acknowledgeMessage = SubscriptionOperationMessage(GQL_CONNECTION_ACK.type)

    @Suppress("Detekt.TooGenericExceptionCaught")
    fun handle(payload: String, session: WebSocketSession): Flux<SubscriptionOperationMessage> {
        try {
            val operationMessage: SubscriptionOperationMessage = objectMapper.readValue(payload)

            return when (operationMessage.type) {
                GQL_CONNECTION_INIT.type -> {
                    val flux = Flux.just(acknowledgeMessage)
                    val keepAliveInterval = config.subscriptions.keepAliveInterval
                    if (keepAliveInterval != null) {
                        // Send the GQL_CONNECTION_KEEP_ALIVE message every interval until the connection is closed or terminated
                        val keepAliveFlux = Flux.interval(Duration.ofMillis(keepAliveInterval))
                            .map { keepAliveMessage }

                        return flux.concatWith(keepAliveFlux)
                    }

                    return flux.doOnSubscribe { activeSessions[session.id] = it }
                }
                GQL_START.type -> startSubscription(operationMessage, session)
                GQL_STOP.type -> {
                    stopSubscription(operationMessage, session)
                    return Flux.empty()
                }
                GQL_CONNECTION_TERMINATE.type -> {
                    terminateSession(session)
                    return Flux.empty()
                }
                else -> {
                    logger.error("Unknown subscription operation $operationMessage")
                    stopSubscription(operationMessage, session)
                    return Flux.just(SubscriptionOperationMessage(type = GQL_CONNECTION_ERROR.type, id = operationMessage.id))
                }
            }
        } catch (exception: Exception) {
            logger.error("Error parsing the subscription message", exception)
            return Flux.just(basicConnectionErrorMessage)
        }
    }

    @Suppress("Detekt.TooGenericExceptionCaught")
    private fun startSubscription(operationMessage: SubscriptionOperationMessage, session: WebSocketSession): Flux<SubscriptionOperationMessage> {
        if (operationMessage.id == null) {
            logger.error("Operation id is required")
            return Flux.just(basicConnectionErrorMessage)
        }

        val payload = operationMessage.payload

        if (payload == null) {
            logger.error("Payload was null instead of a GraphQLRequest object")
            stopSubscription(operationMessage, session)
            return Flux.just(SubscriptionOperationMessage(type = GQL_CONNECTION_ERROR.type, id = operationMessage.id))
        }

        try {
            val request = objectMapper.convertValue<GraphQLRequest>(payload)
            return subscriptionHandler.executeSubscription(request)
                .map {
                    if (it.errors?.isNotEmpty() == true) {
                        SubscriptionOperationMessage(type = GQL_ERROR.type, id = operationMessage.id, payload = it)
                    } else {
                        SubscriptionOperationMessage(type = GQL_DATA.type, id = operationMessage.id, payload = it)
                    }
                }
                .concatWith(Flux.just(SubscriptionOperationMessage(type = GQL_COMPLETE.type, id = operationMessage.id)))
                .doOnSubscribe {
                    logger.trace("WebSocket GraphQL subscription subscribe, WebSocketSessionID=${session.id} OperationMessageID=${operationMessage.id}")
                    activeOperations[session.id]?.put(operationMessage.id, it)
                }
                .doOnCancel { logger.trace("WebSocket GraphQL subscription cancel, WebSocketSessionID=${session.id} OperationMessageID=${operationMessage.id}") }
                .doOnComplete { logger.trace("WebSocket GraphQL subscription complete, WebSocketSessionID=${session.id} OperationMessageID=${operationMessage.id}") }
        } catch (exception: Exception) {
            logger.error("Error running graphql subscription", exception)
            stopSubscription(operationMessage, session)
            return Flux.just(SubscriptionOperationMessage(type = GQL_CONNECTION_ERROR.type, id = operationMessage.id))
        }
    }

    private fun stopSubscription(operationMessage: SubscriptionOperationMessage, session: WebSocketSession) {
        if (operationMessage.id != null) {
            val operationsForSession = activeOperations[session.id]
            operationsForSession?.get(operationMessage.id)?.cancel()
            operationsForSession?.remove(operationMessage.id)
        }
    }

    private fun terminateSession(session: WebSocketSession) {
        activeOperations[session.id]?.forEach { it.value.cancel() }
        activeOperations.remove(session.id)
        activeSessions[session.id]?.cancel()
        activeSessions.remove(session.id)
        session.close()
    }
}
