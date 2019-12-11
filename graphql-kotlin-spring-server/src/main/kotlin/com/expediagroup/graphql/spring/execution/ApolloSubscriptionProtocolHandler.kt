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
    // Data subscriptions are saved by web socket session id + SubscriptionOperationMessage.id
    private val subscriptions = ConcurrentHashMap<String, Subscription>()
    // Mapping from client id to active subscriptions
    private val subscriptionsForClient = ConcurrentHashMap<String, MutableList<String>>()

    private val logger = LoggerFactory.getLogger(ApolloSubscriptionProtocolHandler::class.java)
    private val keepAliveMessage = SubscriptionOperationMessage(type = GQL_CONNECTION_KEEP_ALIVE.type)
    private val errorMessage = SubscriptionOperationMessage(type = GQL_CONNECTION_ERROR.type)

    @Suppress("Detekt.TooGenericExceptionCaught")
    fun handle(payload: String, session: WebSocketSession): Flux<SubscriptionOperationMessage> {
        try {
            val operationMessage: SubscriptionOperationMessage = objectMapper.readValue(payload)

            return when (operationMessage.type) {
                GQL_CONNECTION_INIT.type -> {
                    val flux = Flux.just(SubscriptionOperationMessage(GQL_CONNECTION_ACK.type))
                    val keepAliveInterval = config.subscriptions.keepAliveInterval
                    subscriptionsForClient[session.id] = mutableListOf()
                    if (keepAliveInterval != null) {
                        subscriptionsForClient[session.id]?.add(session.id)
                        // Send the GQL_CONNECTION_KEEP_ALIVE message every interval until the connection is closed or terminated
                        val keepAliveFlux = Flux.interval(Duration.ofMillis(keepAliveInterval))
                                .map { keepAliveMessage }
                                .doOnSubscribe {
                                    subscriptions[session.id] = it
                                }
                        return flux.concatWith(keepAliveFlux)
                    }
                    return flux
                }
                GQL_START.type -> startSubscription(operationMessage, session)
                GQL_STOP.type -> {
                    stopSubscription(operationMessage, session)
                    return Flux.empty()
                }
                GQL_CONNECTION_TERMINATE.type -> {
                    terminateSubscription(session)
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
            return Flux.just(errorMessage)
        }
    }

    @Suppress("Detekt.TooGenericExceptionCaught")
    private fun startSubscription(operationMessage: SubscriptionOperationMessage, session: WebSocketSession): Flux<SubscriptionOperationMessage> {
        if (operationMessage.id == null) {
            logger.error("Operation id is required")
            return Flux.just(errorMessage)
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
                    subscriptions[session.id + operationMessage.id] = it
                    subscriptionsForClient[session.id]?.add(session.id + operationMessage.id)
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
            val key = session.id + operationMessage.id
            subscriptions[key]?.let {
                it.cancel()
                subscriptions.remove(key)
                subscriptionsForClient[session.id]?.remove(key)
            }
        }
    }

    private fun terminateSubscription(session: WebSocketSession) {
        subscriptionsForClient[session.id]?.let { subscriptions ->
            subscriptions.forEach { this.subscriptions[it]?.cancel() }
            subscriptions.forEach { this.subscriptions.remove(it) }
        }
        subscriptionsForClient.remove(session.id)
        session.close()
    }
}
