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
import com.expediagroup.graphql.spring.model.SubscriptionOperationMessage.ServerMessages.GQL_CONNECTION_ACK
import com.expediagroup.graphql.spring.model.SubscriptionOperationMessage.ServerMessages.GQL_CONNECTION_ERROR
import com.expediagroup.graphql.spring.model.SubscriptionOperationMessage.ServerMessages.GQL_CONNECTION_KEEP_ALIVE
import com.expediagroup.graphql.spring.model.SubscriptionOperationMessage.ServerMessages.GQL_DATA
import com.expediagroup.graphql.spring.model.SubscriptionOperationMessage.ServerMessages.GQL_ERROR
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.convertValue
import com.fasterxml.jackson.module.kotlin.readValue
import org.slf4j.LoggerFactory
import org.springframework.web.reactive.socket.WebSocketSession
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Duration

/**
 * Implementation of the `graphql-ws` protocol defined by Apollo
 * https://github.com/apollographql/subscriptions-transport-ws/blob/master/PROTOCOL.md
 */
class ApolloSubscriptionProtocolHandler(
    private val config: GraphQLConfigurationProperties,
    private val subscriptionHandler: SubscriptionHandler,
    private val objectMapper: ObjectMapper,
    private val subscriptionLifecycleEvents: ApolloSubscriptionLifecycleEvents
) {
    private val sessionState = ApolloSubscriptionSessionState()
    private val logger = LoggerFactory.getLogger(ApolloSubscriptionProtocolHandler::class.java)
    private val keepAliveMessage = SubscriptionOperationMessage(type = GQL_CONNECTION_KEEP_ALIVE.type)
    private val basicConnectionErrorMessage = SubscriptionOperationMessage(type = GQL_CONNECTION_ERROR.type)
    private val acknowledgeMessage = SubscriptionOperationMessage(GQL_CONNECTION_ACK.type)

    @Suppress("Detekt.TooGenericExceptionCaught")
    fun handle(payload: String, session: WebSocketSession): Flux<SubscriptionOperationMessage> {
        try {
            val operationMessage: SubscriptionOperationMessage = objectMapper.readValue(payload)
            logger.debug("GraphQL subscription client message, sessionId=${session.id} operationMessage=$operationMessage")
            return Mono.subscriberContext().flatMapMany<SubscriptionOperationMessage> { reactorContext ->
                val graphQLContext = reactorContext.getOrDefault<Any>(GRAPHQL_CONTEXT_KEY, null)
                when (operationMessage.type) {
                    GQL_CONNECTION_INIT.type -> {
                        val connectionParams: Map<String, String> = operationMessage.payload as? Map<String, String> ?: emptyMap()
                        subscriptionLifecycleEvents.onConnect(connectionParams, session, graphQLContext)
                        val acknowledgeMessageFlux = Flux.just(acknowledgeMessage)
                        val keepAliveFlux = getKeepAliveFlux(session)
                        return@flatMapMany acknowledgeMessageFlux.concatWith(keepAliveFlux)
                    }
                    GQL_START.type -> {
                        subscriptionLifecycleEvents.onOperation(operationMessage, session, graphQLContext)
                        return@flatMapMany startSubscription(operationMessage, session)
                    }
                    GQL_STOP.type -> {
                        subscriptionLifecycleEvents.onOperationComplete(session)
                        return@flatMapMany sessionState.stopOperation(session, operationMessage)
                    }
                    GQL_CONNECTION_TERMINATE.type -> {
                        subscriptionLifecycleEvents.onDisconnect(session, graphQLContext)
                        sessionState.terminateSession(session)
                        return@flatMapMany Flux.empty()
                    }
                    else -> {
                        logger.error("Unknown subscription operation $operationMessage")
                        sessionState.stopOperation(session, operationMessage)
                        return@flatMapMany Flux.just(SubscriptionOperationMessage(type = GQL_CONNECTION_ERROR.type, id = operationMessage.id))
                    }
                }
            }
        } catch (exception: Exception) {
            logger.error("Error parsing the subscription message", exception)
            return Flux.just(basicConnectionErrorMessage)
        }
    }

    /**
     * If the keep alive configuration is set, send a message back to client at every interval until the session is terminated.
     * Otherwise just return empty flux to append to the acknowledge message.
     */
    private fun getKeepAliveFlux(session: WebSocketSession): Flux<SubscriptionOperationMessage> {
        val keepAliveInterval: Long? = config.subscriptions.keepAliveInterval
        if (keepAliveInterval != null) {
            return Flux.interval(Duration.ofMillis(keepAliveInterval))
                .map { keepAliveMessage }
                .doOnSubscribe { sessionState.saveKeepAliveSubscription(session, it) }
        }

        return Flux.empty()
    }

    @Suppress("Detekt.TooGenericExceptionCaught")
    private fun startSubscription(operationMessage: SubscriptionOperationMessage, session: WebSocketSession): Flux<SubscriptionOperationMessage> {
        if (operationMessage.id == null) {
            logger.error("GraphQL subscription operation id is required")
            return Flux.just(basicConnectionErrorMessage)
        }

        if (sessionState.operationExists(session, operationMessage)) {
            logger.info("Already subscribed to operation ${operationMessage.id} for session ${session.id}")
            return Flux.empty()
        }

        val payload = operationMessage.payload

        if (payload == null) {
            logger.error("GraphQL subscription payload was null instead of a GraphQLRequest object")
            sessionState.stopOperation(session, operationMessage)
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
                .doOnSubscribe { sessionState.saveOperation(session, operationMessage, it) }
        } catch (exception: Exception) {
            logger.error("Error running graphql subscription", exception)
            // Do not terminate the session, just stop the operation messages
            sessionState.stopOperation(session, operationMessage)
            return Flux.just(SubscriptionOperationMessage(type = GQL_CONNECTION_ERROR.type, id = operationMessage.id))
        }
    }
}
