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

class ApolloSubscriptionProtocolHandler(
    private val config: GraphQLConfigurationProperties,
    private val subscriptionHandler: SubscriptionHandler,
    private val objectMapper: ObjectMapper
) {
    // Keep Alive subscriptions are saved by web socket session id since they are sent on connection init
    private val keepAliveSubscriptions = ConcurrentHashMap<String, Subscription>()
    // Data subscriptions are saved by SubscriptionOperationMessage.id
    private val subscriptions = ConcurrentHashMap<String, Subscription>()

    private val logger = LoggerFactory.getLogger(ApolloSubscriptionProtocolHandler::class.java)
    private val keepAliveMessage = SubscriptionOperationMessage(type = GQL_CONNECTION_KEEP_ALIVE.type)

    @Suppress("Detekt.TooGenericExceptionCaught")
    fun handle(payload: String, session: WebSocketSession): Flux<SubscriptionOperationMessage> {
        try {
            val operationMessage: SubscriptionOperationMessage = objectMapper.readValue(payload)

            return when {
                operationMessage.type == GQL_CONNECTION_INIT.type -> {
                    val flux = Flux.just(SubscriptionOperationMessage(GQL_CONNECTION_ACK.type))
                    val keepAliveInterval = config.subscriptions.keepAliveInterval
                    if (keepAliveInterval != null) {
                        // Send the GQL_CONNECTION_KEEP_ALIVE message every interval until the connection is closed or terminated
                        val keepAliveFlux = Flux.interval(Duration.ofMillis(keepAliveInterval))
                            .map { keepAliveMessage }
                            .doOnSubscribe {
                                keepAliveSubscriptions[session.id] = it
                            }
                        return flux.concatWith(keepAliveFlux)
                    }

                    return flux
                }
                operationMessage.type == GQL_START.type -> startSubscription(operationMessage, session)
                operationMessage.type == GQL_STOP.type -> {
                    stopSubscription(operationMessage, session, false)
                    Flux.empty()
                }
                operationMessage.type == GQL_CONNECTION_TERMINATE.type -> {
                    stopSubscription(operationMessage, session, true)
                    session.close()
                    Flux.empty()
                }
                else -> {
                    logger.error("Unknown subscription operation $operationMessage")
                    Flux.just(SubscriptionOperationMessage(type = GQL_CONNECTION_ERROR.type, id = operationMessage.id))
                }
            }
        } catch (exception: Exception) {
            logger.error("Error parsing the subscription message", exception)
            return Flux.just(SubscriptionOperationMessage(type = GQL_CONNECTION_ERROR.type))
        }
    }

    @Suppress("Detekt.TooGenericExceptionCaught")
    private fun startSubscription(operationMessage: SubscriptionOperationMessage, session: WebSocketSession): Flux<SubscriptionOperationMessage> {
        if (operationMessage.id == null) {
            logger.error("Operation id is required")
            return Flux.just(SubscriptionOperationMessage(type = GQL_CONNECTION_ERROR.type))
        }

        val payload = operationMessage.payload

        if (payload == null) {
            logger.error("Payload was null instead of a GraphQLRequest object")
            return Flux.just(SubscriptionOperationMessage(type = GQL_CONNECTION_ERROR.type, id = operationMessage.id))
        }

        return try {
            val request = objectMapper.convertValue<GraphQLRequest>(payload)
            subscriptionHandler.executeSubscription(request)
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
                    subscriptions[operationMessage.id] = it
                }
                .doOnCancel { logger.trace("WebSocket GraphQL subscription cancel, WebSocketSessionID=${session.id} OperationMessageID=${operationMessage.id}") }
                .doOnComplete { logger.trace("WebSocket GraphQL subscription complete, WebSocketSessionID=${session.id} OperationMessageID=${operationMessage.id}") }
        } catch (exception: Exception) {
            logger.error("Error running graphql subscription", exception)
            Flux.just(SubscriptionOperationMessage(type = GQL_CONNECTION_ERROR.type, id = operationMessage.id))
        }
    }

    private fun stopSubscription(operationMessage: SubscriptionOperationMessage, session: WebSocketSession, terminate: Boolean) {
        if (operationMessage.id != null) {
            if (terminate) {
                keepAliveSubscriptions[session.id]?.cancel()
            }
            subscriptions[operationMessage.id]?.cancel()
        }
    }
}
