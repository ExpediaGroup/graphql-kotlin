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

package com.expediagroup.graphql.server.execution.subscription

import com.expediagroup.graphql.generator.extensions.get
import com.expediagroup.graphql.generator.extensions.plus
import com.expediagroup.graphql.server.execution.GraphQLRequestHandler
import com.expediagroup.graphql.server.types.GraphQLSubscriptionMessage
import com.expediagroup.graphql.server.types.GraphQLSubscriptionStatus
import com.expediagroup.graphql.server.types.SubscriptionMessageComplete
import com.expediagroup.graphql.server.types.SubscriptionMessageConnectionAck
import com.expediagroup.graphql.server.types.SubscriptionMessageConnectionInit
import com.expediagroup.graphql.server.types.SubscriptionMessageError
import com.expediagroup.graphql.server.types.SubscriptionMessageNext
import com.expediagroup.graphql.server.types.SubscriptionMessagePing
import com.expediagroup.graphql.server.types.SubscriptionMessagePong
import com.expediagroup.graphql.server.types.SubscriptionMessageSubscribe
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import graphql.GraphQLContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicReference
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

const val GRAPHQL_WS_PROTOCOL = "graphql-transport-ws"

/**
 * GraphQL Web Socket server implementation for handling subscriptions using *graphql-transport-ws* protocol
 *
 * @see <a href="https://github.com/enisdenjo/graphql-ws/blob/master/PROTOCOL.md">graphql-transport-ws protocol</a>
 */
abstract class GraphQLWebSocketServer<Session, Message>(
    private val requestParser: GraphQLSubscriptionRequestParser<Session>,
    private val contextFactory: GraphQLSubscriptionContextFactory<Session>,
    private val subscriptionHooks: GraphQLSubscriptionHooks<Session>,
    private val requestHandler: GraphQLRequestHandler,
    private val initTimeoutMillis: Long,
    private val objectMapper: ObjectMapper = jacksonObjectMapper()
) {
    private val logger: Logger = LoggerFactory.getLogger(GraphQLWebSocketServer::class.java)

    @OptIn(FlowPreview::class)
    suspend fun handleSubscription(session: Session): Flow<Message> = coroutineScope {
        val subscriptions = ConcurrentHashMap<String, Job>()
        val graphqlContext = AtomicReference<GraphQLContext?>()

        requestParser.parseRequestFlow(session).map { objectMapper.readValue<GraphQLSubscriptionMessage>(it) }
            .flatMapConcat { message ->
                channelFlow<GraphQLSubscriptionMessage> {
                    when (message) {
                        is SubscriptionMessageConnectionInit -> {
                            try {
                                val context = contextFactory.generateContext(session, message.payload).let { it ->
                                    subscriptionHooks.onConnect(message.payload, session, it)
                                }

                                val customCoroutineContext = (context.get<CoroutineContext>() ?: EmptyCoroutineContext)
                                val graphQLExecutionScope = CoroutineScope(
                                    coroutineContext + customCoroutineContext + SupervisorJob()
                                )
                                val graphQLContextWithCoroutineScope = context + mapOf(
                                    CoroutineScope::class to graphQLExecutionScope
                                )

                                if (!graphqlContext.compareAndSet(null, graphQLContextWithCoroutineScope)) {
                                    cancelSubscription(session, GraphQLSubscriptionStatus.TOO_MANY_REQUESTS)
                                } else {
                                    send(SubscriptionMessageConnectionAck())
                                }
                            } catch (e: Throwable) {
                                logger.warn("Error thrown when processing connection-init message", e)
                                cancelSubscription(session, GraphQLSubscriptionStatus.FORBIDDEN)
                            }
                        }

                        is SubscriptionMessageSubscribe -> {
                            val context = graphqlContext.get()
                            if (context == null) {
                                cancelSubscription(session, GraphQLSubscriptionStatus.UNAUTHORIZED)
                                return@channelFlow
                            }

                            if (subscriptions.containsKey(message.id)) {
                                logger.warn("Operation ${message.id} is already subscribed to")
                                cancelSubscription(session, GraphQLSubscriptionStatus.conflict(message.id))
                                return@channelFlow
                            }

                            try {
                                subscriptionHooks.onOperation(message.id, message.payload, session, context)
                            } catch (e: Throwable) {
                                logger.error("Error thrown when running onOperation subscription hook for operation=${message.id}")
                                cancelSubscription(session, GraphQLSubscriptionStatus.INVALID_MESSAGE)
                                return@channelFlow
                            }

                            val subscriptionJob = coroutineScope {
                                launch {
                                    requestHandler.executeSubscription(message.payload, context)
                                        .map {
                                            val errors = it.errors
                                            if (!errors.isNullOrEmpty()) {
                                                SubscriptionMessageError(id = message.id, payload = errors)
                                            } else {
                                                SubscriptionMessageNext(id = message.id, payload = it)
                                            }
                                        }
                                        .onCompletion {
                                            if (it == null) {
                                                try {
                                                    subscriptionHooks.onOperationComplete(message.id, session, context)
                                                } catch (ex: Throwable) {
                                                    logger.error("Error when executing onOperationComplete hook for operation={}", message.id, ex)
                                                }
                                                emit(SubscriptionMessageComplete(id = message.id))
                                            }
                                        }
                                        .collect {
                                            send(it)
                                        }
                                }
                            }
                            subscriptions[message.id] = subscriptionJob
                            subscriptionJob.invokeOnCompletion {
                                subscriptions.remove(message.id)
                            }
                        }

                        is SubscriptionMessagePing -> {
                            logger.debug("received subscription ping message")
                            send(SubscriptionMessagePong())
                        }

                        is SubscriptionMessageComplete -> {
                            logger.debug("subscription id={} completed", message.id)
                            val subscriptionJob = subscriptions.remove(message.id) ?: run {
                                logger.debug("subscription id={} not found", message.id)
                                return@channelFlow
                            }

                            try {
                                subscriptionHooks.onOperationComplete(message.id, session, graphqlContext.get())
                            } catch (ex: Throwable) {
                                logger.error("exception when calling onOperationComplete hook for operation={}", message.id, ex)
                            } finally {
                                subscriptionJob.cancel()
                            }
                        }

                        else -> {
                            logger.warn("Invalid message received $message")
                            cancelSubscription(session, GraphQLSubscriptionStatus.INVALID_MESSAGE)
                        }
                    }
                }
                    .map {
                        logger.debug("Subscription response {}", it)
                        sendSubscriptionMessage(session, objectMapper.writeValueAsString(it))
                    }
                    .catch {
                        logger.warn("Error occurred when processing the subscription", it)
                    }.onStart {
                        launch {
                            delay(initTimeoutMillis)
                            if (graphqlContext.get() == null) {
                                closeSession(session, GraphQLSubscriptionStatus.CONNECTION_INIT_TIMEOUT)
                            }
                            cancel()
                        }
                    }.onCompletion {
                        if (it == null) {
                            try {
                                subscriptionHooks.onDisconnect(session, graphqlContext.get())
                            } catch (e: Throwable) {
                                logger.error("Error thrown when executing onDisconnect subscription hook", e)
                            }
                        }
                    }
            }
    }

    private fun cancelSubscription(session: Session, reason: GraphQLSubscriptionStatus, context: GraphQLContext? = null) {
        logger.warn("Closing session - {}", reason.reason)
        try {
            subscriptionHooks.onDisconnect(session, context)
        } catch (e: Throwable) {
            logger.error("Error thrown when executing onDisconnect subscription hook", e)
        }
    }

    abstract suspend fun closeSession(session: Session, reason: GraphQLSubscriptionStatus)

    abstract suspend fun sendSubscriptionMessage(session: Session, message: String): Message
}
