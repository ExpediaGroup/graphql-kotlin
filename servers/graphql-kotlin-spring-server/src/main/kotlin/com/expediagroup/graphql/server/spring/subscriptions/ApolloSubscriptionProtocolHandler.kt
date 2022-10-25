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

import com.expediagroup.graphql.server.spring.GraphQLConfigurationProperties
import com.expediagroup.graphql.server.spring.subscriptions.SubscriptionOperationMessage.ClientMessages.GQL_CONNECTION_INIT
import com.expediagroup.graphql.server.spring.subscriptions.SubscriptionOperationMessage.ClientMessages.GQL_CONNECTION_TERMINATE
import com.expediagroup.graphql.server.spring.subscriptions.SubscriptionOperationMessage.ClientMessages.GQL_START
import com.expediagroup.graphql.server.spring.subscriptions.SubscriptionOperationMessage.ClientMessages.GQL_STOP
import com.expediagroup.graphql.server.spring.subscriptions.SubscriptionOperationMessage.ServerMessages.GQL_CONNECTION_ACK
import com.expediagroup.graphql.server.spring.subscriptions.SubscriptionOperationMessage.ServerMessages.GQL_CONNECTION_ERROR
import com.expediagroup.graphql.server.spring.subscriptions.SubscriptionOperationMessage.ServerMessages.GQL_CONNECTION_KEEP_ALIVE
import com.expediagroup.graphql.server.spring.subscriptions.SubscriptionOperationMessage.ServerMessages.GQL_DATA
import com.expediagroup.graphql.server.spring.subscriptions.SubscriptionOperationMessage.ServerMessages.GQL_ERROR
import com.expediagroup.graphql.server.types.GraphQLRequest
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.convertValue
import com.fasterxml.jackson.module.kotlin.readValue
import kotlinx.coroutines.reactor.asFlux
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import org.springframework.web.reactive.socket.WebSocketSession
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toFlux
import java.time.Duration

/**
 * Implementation of the `graphql-ws` protocol defined by Apollo
 * https://github.com/apollographql/subscriptions-transport-ws/blob/master/PROTOCOL.md
 */
class ApolloSubscriptionProtocolHandler(
    private val config: GraphQLConfigurationProperties,
    private val contextFactory: SpringSubscriptionGraphQLContextFactory,
    private val subscriptionHandler: SpringGraphQLSubscriptionHandler,
    private val objectMapper: ObjectMapper,
    private val subscriptionHooks: ApolloSubscriptionHooks
) {
    private val sessionState = ApolloSubscriptionSessionState()
    private val logger = LoggerFactory.getLogger(ApolloSubscriptionProtocolHandler::class.java)
    private val keepAliveMessage = SubscriptionOperationMessage(type = GQL_CONNECTION_KEEP_ALIVE.type)
    private val basicConnectionErrorMessage = SubscriptionOperationMessage(type = GQL_CONNECTION_ERROR.type)
    private val acknowledgeMessage = SubscriptionOperationMessage(GQL_CONNECTION_ACK.type)

    @Suppress("Detekt.TooGenericExceptionCaught")
    fun handle(payload: String, session: WebSocketSession): Flux<SubscriptionOperationMessage> {
        val operationMessage = convertToMessageOrNull(payload) ?: return Flux.just(basicConnectionErrorMessage)
        logger.debug("GraphQL subscription client message, sessionId=${session.id} operationMessage=$operationMessage")

        return try {
            when (operationMessage.type) {
                GQL_CONNECTION_INIT.type -> onInit(operationMessage, session)
                GQL_START.type -> startSubscription(operationMessage, session)
                GQL_STOP.type -> onStop(operationMessage, session)
                GQL_CONNECTION_TERMINATE.type -> onDisconnect(session)
                else -> onUnknownOperation(operationMessage, session)
            }
        } catch (exception: Exception) {
            onException(exception)
        }
    }

    @Suppress("Detekt.TooGenericExceptionCaught")
    private fun convertToMessageOrNull(payload: String): SubscriptionOperationMessage? {
        return try {
            objectMapper.readValue(payload)
        } catch (exception: Exception) {
            logger.error("Error parsing the subscription message", exception)
            null
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
    private fun startSubscription(
        operationMessage: SubscriptionOperationMessage,
        session: WebSocketSession
    ): Flux<SubscriptionOperationMessage> {
        val graphQLContext = sessionState.getGraphQLContext(session)

        subscriptionHooks.onOperationWithContext(operationMessage, session, graphQLContext)

        if (operationMessage.id == null) {
            logger.error("GraphQL subscription operation id is required")
            return Flux.just(basicConnectionErrorMessage)
        }

        if (sessionState.doesOperationExist(session, operationMessage)) {
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
            return subscriptionHandler.executeSubscription(request, graphQLContext)
                .asFlux()
                .map {
                    if (it.errors?.isNotEmpty() == true) {
                        SubscriptionOperationMessage(type = GQL_ERROR.type, id = operationMessage.id, payload = it)
                    } else {
                        SubscriptionOperationMessage(type = GQL_DATA.type, id = operationMessage.id, payload = it)
                    }
                }
                .concatWith(onComplete(operationMessage, session).toFlux())
                .doOnSubscribe { sessionState.saveOperation(session, operationMessage, it) }
        } catch (exception: Exception) {
            logger.error("Error running graphql subscription", exception)
            // Do not terminate the session, just stop the operation messages
            sessionState.stopOperation(session, operationMessage)
            return Flux.just(SubscriptionOperationMessage(type = GQL_CONNECTION_ERROR.type, id = operationMessage.id))
        }
    }

    private fun onInit(operationMessage: SubscriptionOperationMessage, session: WebSocketSession): Flux<SubscriptionOperationMessage> {
        saveContext(operationMessage, session)
        val acknowledgeMessage = Mono.just(acknowledgeMessage)
        val keepAliveFlux = getKeepAliveFlux(session)
        return acknowledgeMessage.concatWith(keepAliveFlux)
            .onErrorReturn(getConnectionErrorMessage(operationMessage))
    }

    /**
     * Generate the context and save it for all future messages.
     */
    private fun saveContext(operationMessage: SubscriptionOperationMessage, session: WebSocketSession) {
        runBlocking {
            val connectionParams = castToMapOfStringString(operationMessage.payload)
            val graphQLContext = contextFactory.generateContext(session)
            val onConnectGraphQLContext = subscriptionHooks.onConnectWithContext(connectionParams, session, graphQLContext)
            sessionState.saveContext(session, onConnectGraphQLContext)
        }
    }

    /**
     * Called with the publisher has completed on its own.
     */
    private fun onComplete(
        operationMessage: SubscriptionOperationMessage,
        session: WebSocketSession
    ): Mono<SubscriptionOperationMessage> {
        subscriptionHooks.onOperationComplete(session)
        return sessionState.completeOperation(session, operationMessage)
    }

    /**
     * Called with the client has called stop manually, or on error, and we need to cancel the publisher
     */
    private fun onStop(
        operationMessage: SubscriptionOperationMessage,
        session: WebSocketSession
    ): Flux<SubscriptionOperationMessage> {
        subscriptionHooks.onOperationComplete(session)
        return sessionState.stopOperation(session, operationMessage).toFlux()
    }

    private fun onDisconnect(session: WebSocketSession): Flux<SubscriptionOperationMessage> {
        subscriptionHooks.onDisconnect(session)
        sessionState.terminateSession(session)
        return Flux.empty()
    }

    private fun onUnknownOperation(operationMessage: SubscriptionOperationMessage, session: WebSocketSession): Flux<SubscriptionOperationMessage> {
        logger.error("Unknown subscription operation $operationMessage")
        sessionState.stopOperation(session, operationMessage)
        return Flux.just(getConnectionErrorMessage(operationMessage))
    }

    private fun onException(exception: Exception): Flux<SubscriptionOperationMessage> {
        logger.error("Error parsing the subscription message", exception)
        return Flux.just(basicConnectionErrorMessage)
    }

    private fun getConnectionErrorMessage(operationMessage: SubscriptionOperationMessage): SubscriptionOperationMessage {
        return SubscriptionOperationMessage(type = GQL_CONNECTION_ERROR.type, id = operationMessage.id)
    }
}
