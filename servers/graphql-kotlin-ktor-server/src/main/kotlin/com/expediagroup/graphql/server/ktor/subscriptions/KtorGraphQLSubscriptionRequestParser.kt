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

package com.expediagroup.graphql.server.ktor.subscriptions

import com.expediagroup.graphql.server.execution.subscription.GraphQLSubscriptionRequestParser
import com.expediagroup.graphql.server.types.GraphQLSubscriptionStatus
import io.ktor.server.websocket.WebSocketServerSession
import io.ktor.websocket.CloseReason
import io.ktor.websocket.Frame
import io.ktor.websocket.close
import io.ktor.websocket.readText
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.isActive
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Ktor specific version of WebSocket subscription request parser.
 */
interface KtorGraphQLSubscriptionRequestParser : GraphQLSubscriptionRequestParser<WebSocketServerSession>

class DefaultKtorGraphQLSubscriptionRequestParser : KtorGraphQLSubscriptionRequestParser {

    private val logger: Logger = LoggerFactory.getLogger(DefaultKtorGraphQLSubscriptionRequestParser::class.java)

    override suspend fun parseRequestFlow(session: WebSocketServerSession): Flow<String> =
        flow {
            try {
                while (session.isActive) {
                    val frame = session.incoming.receive()
                    if (frame !is Frame.Text) {
                        val invalidStatus = GraphQLSubscriptionStatus.INVALID_MESSAGE
                        session.close(CloseReason(code = invalidStatus.code.toShort(), message = invalidStatus.reason))
                        continue
                    } else {
                        val messageString = frame.readText()
                        emit(messageString)
                    }
                }
            } catch (e: ClosedReceiveChannelException) {
                logger.debug("Client disconnected")
            }
        }
}
