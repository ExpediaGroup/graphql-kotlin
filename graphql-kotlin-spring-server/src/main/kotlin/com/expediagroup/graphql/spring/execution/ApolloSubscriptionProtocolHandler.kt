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

import com.expediagroup.graphql.spring.model.GraphQLRequest
import com.expediagroup.graphql.spring.model.SubscriptionOperationMessage
import com.expediagroup.graphql.spring.model.SubscriptionOperationMessage.ClientMessages.GQL_CONNECTION_INIT
import com.expediagroup.graphql.spring.model.SubscriptionOperationMessage.ClientMessages.GQL_CONNECTION_TERMINATE
import com.expediagroup.graphql.spring.model.SubscriptionOperationMessage.ClientMessages.GQL_START
import com.expediagroup.graphql.spring.model.SubscriptionOperationMessage.ClientMessages.GQL_STOP
import com.expediagroup.graphql.spring.model.SubscriptionOperationMessage.ServerMessages.GQL_COMPLETE
import com.expediagroup.graphql.spring.model.SubscriptionOperationMessage.ServerMessages.GQL_CONNECTION_ACK
import com.expediagroup.graphql.spring.model.SubscriptionOperationMessage.ServerMessages.GQL_CONNECTION_ERROR
import com.expediagroup.graphql.spring.model.SubscriptionOperationMessage.ServerMessages.GQL_DATA
import com.expediagroup.graphql.spring.model.SubscriptionOperationMessage.ServerMessages.GQL_ERROR
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.convertValue
import com.fasterxml.jackson.module.kotlin.readValue
import org.slf4j.LoggerFactory
import org.springframework.web.reactive.socket.WebSocketSession
import reactor.core.publisher.Flux

class ApolloSubscriptionProtocolHandler(
    private val subscriptionHandler: SubscriptionHandler,
    private val objectMapper: ObjectMapper
) {

    private val logger = LoggerFactory.getLogger(ApolloSubscriptionProtocolHandler::class.java)

    @Suppress("Detekt.TooGenericExceptionCaught")
    fun handle(payload: String, session: WebSocketSession): Flux<SubscriptionOperationMessage> {
        try {
            val operationMessage: SubscriptionOperationMessage = objectMapper.readValue(payload)

            return when {
                operationMessage.type == GQL_CONNECTION_INIT.type -> Flux.just(SubscriptionOperationMessage(GQL_CONNECTION_ACK.type))
                operationMessage.type == GQL_START.type -> startSubscription(operationMessage, session)
                operationMessage.type == GQL_CONNECTION_TERMINATE.type || operationMessage.type == GQL_STOP.type -> {
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
                .doOnSubscribe { logger.trace("WebSocket GraphQL subscription subscribe, WebSocketID=${session.id} OperationMessageID=${operationMessage.id}") }
                .doOnCancel { logger.trace("WebSocket GraphQL subscription cancel, WebSocketID=${session.id} OperationMessageID=${operationMessage.id}") }
                .doOnComplete { logger.trace("WebSocket GraphQL subscription complete, WebSocketID=${session.id} OperationMessageID=${operationMessage.id}") }
                .doFinally {
                    val completeMessage = SubscriptionOperationMessage(type = GQL_COMPLETE.type, id = operationMessage.id)
                    val text = objectMapper.writeValueAsString(completeMessage)
                    session.send(Flux.just(session.textMessage(text)))
                    session.close()
                }
        } catch (exception: Exception) {
            logger.error("Error running graphql subscription", exception)
            Flux.just(SubscriptionOperationMessage(type = GQL_CONNECTION_ERROR.type, id = operationMessage.id))
        }
    }
}
