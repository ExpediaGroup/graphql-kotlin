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

import com.expediagroup.graphql.server.execution.GraphQLRequestHandler
import com.expediagroup.graphql.server.types.GraphQLSubscriptionStatus
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withTimeoutOrNull

class InMemoryGraphQLSubscriptionServer(
    requestHandler: GraphQLRequestHandler,
    requestParser: InMemorySubscriptionRequestParser = InMemorySubscriptionRequestParser(),
    contextFactory: InMemorySubscriptionContextFactory = InMemorySubscriptionContextFactory(),
    hooks: InMemorySubscriptionHooks = InMemorySubscriptionHooks(),
    timeoutInMillis: Long = 1000
) : GraphQLWebSocketServer<Channel<String>, String>(
    requestParser, contextFactory, hooks, requestHandler, timeoutInMillis
) {
    val outboundChannel = Channel<String>(Channel.BUFFERED)

    override suspend fun closeSession(session: Channel<String>, reason: GraphQLSubscriptionStatus) {
        outboundChannel.send("""{"code":${reason.code},"reason":"${reason.reason}"}""")
        session.cancel()
    }

    override suspend fun sendSubscriptionMessage(session: Channel<String>, message: String): String {
        outboundChannel.send(message)
        return message
    }
}

class InMemorySubscriptionRequestParser : GraphQLSubscriptionRequestParser<Channel<String>> {
    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun parseRequestFlow(session: Channel<String>): Flow<String> = flow {
        try {
            while (!session.isClosedForReceive) {
                withTimeoutOrNull(100) {
                    session.receive()
                }?.let { message ->
                    emit(message)
                }
            }
        } catch (e: ClosedReceiveChannelException) {
            // do nothing, client closed session
        }
    }
}

class InMemorySubscriptionContextFactory : GraphQLSubscriptionContextFactory<Channel<String>>

class InMemorySubscriptionHooks : GraphQLSubscriptionHooks<Channel<String>>
