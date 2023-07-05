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

import com.expediagroup.graphql.generator.SchemaGeneratorConfig
import com.expediagroup.graphql.generator.TopLevelObject
import com.expediagroup.graphql.generator.execution.FlowSubscriptionExecutionStrategy
import com.expediagroup.graphql.generator.hooks.FlowSubscriptionSchemaGeneratorHooks
import com.expediagroup.graphql.generator.toSchema
import com.expediagroup.graphql.server.execution.GraphQLRequestHandler
import com.expediagroup.graphql.server.types.GRAPHQL_WS_CONNECTION_ACK
import com.expediagroup.graphql.server.types.GraphQLRequest
import com.expediagroup.graphql.server.types.GraphQLSubscriptionMessage
import com.expediagroup.graphql.server.types.GraphQLSubscriptionStatus
import com.expediagroup.graphql.server.types.SubscriptionMessageComplete
import com.expediagroup.graphql.server.types.SubscriptionMessageConnectionInit
import com.expediagroup.graphql.server.types.SubscriptionMessageInvalid
import com.expediagroup.graphql.server.types.SubscriptionMessageNext
import com.expediagroup.graphql.server.types.SubscriptionMessagePing
import com.expediagroup.graphql.server.types.SubscriptionMessagePong
import com.expediagroup.graphql.server.types.SubscriptionMessageSubscribe
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import graphql.GraphQL
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.spyk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class GraphQLWebSocketServerTest {
    private val mapper = jacksonObjectMapper()

    @Test
    fun `verify graphql-ws subscription protocol`() = runTest {
        val handler = GraphQLRequestHandler(graphQL = testGraphQLEngine())
        val testServer = InMemoryGraphQLSubscriptionServer(requestHandler = handler)

        val session = Channel<String>()
        val responseChannel = testServer.outboundChannel

        val subscriptionJob = launch {
            testServer.handleSubscription(session)
                .collect()
        }

        session.send(mapper.writeValueAsString(SubscriptionMessageConnectionInit()))
        val ack: GraphQLSubscriptionMessage = mapper.readValue(responseChannel.receive())
        assertEquals(GRAPHQL_WS_CONNECTION_ACK, ack.type)

        val id = UUID.randomUUID().toString()
        val request = GraphQLRequest(query = "subscription { counter }")
        val subscriptionOperation = SubscriptionMessageSubscribe(id = id, payload = request)
        session.send(mapper.writeValueAsString(subscriptionOperation))

        for (i in 1..3) {
            val response: GraphQLSubscriptionMessage = mapper.readValue(responseChannel.receive())
            assertTrue(response is SubscriptionMessageNext)
            assertEquals(id, response.id)
            val data = response.payload.data as? Map<*, *>
            assertNotNull(data)
            assertEquals(i, data["counter"])
        }

        assertEquals(SubscriptionMessageComplete(id), mapper.readValue<GraphQLSubscriptionMessage>(responseChannel.receive()))
        subscriptionJob.cancelAndJoin()
    }

    @Test
    fun `verify subscription init timeout`() = runTest {
        val handler = GraphQLRequestHandler(graphQL = testGraphQLEngine())
        val testServer = InMemoryGraphQLSubscriptionServer(
            requestHandler = handler,
            timeoutInMillis = 100
        )

        val session = Channel<String>()
        val responseChannel = testServer.outboundChannel

        val subscriptionJob = launch {
            testServer.handleSubscription(session)
                .collect()
        }
        val timeout = responseChannel.receive()
        delay(300)

        assertEquals(GraphQLSubscriptionStatus.CONNECTION_INIT_TIMEOUT, mapper.readValue<GraphQLSubscriptionStatus>(timeout))
        subscriptionJob.cancelAndJoin()
    }

    @Test
    fun `verify same session cannot init multiple connections`() = runTest {
        val handler = GraphQLRequestHandler(graphQL = testGraphQLEngine())
        val testServer = InMemoryGraphQLSubscriptionServer(requestHandler = handler)

        val session = Channel<String>()
        val responseChannel = testServer.outboundChannel

        val subscriptionJob = launch {
            testServer.handleSubscription(session)
                .collect()
        }

        session.send(mapper.writeValueAsString(SubscriptionMessageConnectionInit()))
        val ack: GraphQLSubscriptionMessage = mapper.readValue(responseChannel.receive())
        assertEquals(GRAPHQL_WS_CONNECTION_ACK, ack.type)

        session.send(mapper.writeValueAsString(SubscriptionMessageConnectionInit()))
        val error: GraphQLSubscriptionStatus = mapper.readValue(responseChannel.receive())
        assertEquals(GraphQLSubscriptionStatus.TOO_MANY_REQUESTS, error)
        subscriptionJob.cancelAndJoin()

        subscriptionJob.cancelAndJoin()
    }

    @Test
    fun `verify session returns FORBIDDEN if we cannot create context`() = runTest {
        val handler = GraphQLRequestHandler(graphQL = testGraphQLEngine())
        val testServer = InMemoryGraphQLSubscriptionServer(
            requestHandler = handler,
            hooks = spyk(InMemorySubscriptionHooks()) {
                coEvery { onConnect(any(), any(), any()) } throws IllegalStateException("unauthorized")
            }
        )

        val session = Channel<String>()
        val responseChannel = testServer.outboundChannel

        val subscriptionJob = launch {
            testServer.handleSubscription(session)
                .collect()
        }

        session.send(mapper.writeValueAsString(SubscriptionMessageConnectionInit()))
        val error: GraphQLSubscriptionStatus = mapper.readValue(responseChannel.receive())
        assertEquals(GraphQLSubscriptionStatus.FORBIDDEN, error)
        subscriptionJob.cancelAndJoin()
    }

    @Test
    fun `verify cannot subscribe if we didn't initialize connection`() = runTest {
        val handler = GraphQLRequestHandler(graphQL = testGraphQLEngine())
        val testServer = InMemoryGraphQLSubscriptionServer(
            requestHandler = handler,
            hooks = spyk(InMemorySubscriptionHooks()) {
                coEvery { onConnect(any(), any(), any()) } throws IllegalStateException("unauthorized")
            }
        )

        val session = Channel<String>()
        val responseChannel = testServer.outboundChannel

        val subscriptionJob = launch {
            testServer.handleSubscription(session)
                .collect()
        }

        session.send(
            mapper.writeValueAsString(
                SubscriptionMessageSubscribe(
                    id = UUID.randomUUID().toString(),
                    payload = GraphQLRequest(query = "subscription { counter }")
                )
            )
        )
        val error: GraphQLSubscriptionStatus = mapper.readValue(responseChannel.receive())
        assertEquals(GraphQLSubscriptionStatus.UNAUTHORIZED, error)
        subscriptionJob.cancelAndJoin()
    }

    @Test
    fun `verify cannot start same subscription twice`() = runTest {
        val handler = GraphQLRequestHandler(graphQL = testGraphQLEngine())
        val testServer = InMemoryGraphQLSubscriptionServer(requestHandler = handler)

        val session = Channel<String>(Channel.BUFFERED)
        val responseChannel = testServer.outboundChannel

        val subscriptionJob = launch {
            testServer.handleSubscription(session)
                .collect()
        }

        session.send(mapper.writeValueAsString(SubscriptionMessageConnectionInit()))
        val ack: GraphQLSubscriptionMessage = mapper.readValue(responseChannel.receive())
        assertEquals(GRAPHQL_WS_CONNECTION_ACK, ack.type)

        val id = UUID.randomUUID().toString()
        val subscriptionOperation = SubscriptionMessageSubscribe(id = id, payload = GraphQLRequest(query = "subscription { counter }"))
        session.send(mapper.writeValueAsString(subscriptionOperation))
        delay(50)
        session.send(mapper.writeValueAsString(subscriptionOperation))

        val error: GraphQLSubscriptionStatus = mapper.readValue(responseChannel.receive())
        assertEquals(4409, error.code)
        assertEquals("Subscriber for $id already exists", error.reason)
        subscriptionJob.cancelAndJoin()
    }

    @Test
    fun `verify ping-pong messages`() = runTest {
        val handler = GraphQLRequestHandler(graphQL = testGraphQLEngine())
        val testServer = InMemoryGraphQLSubscriptionServer(requestHandler = handler)

        val session = Channel<String>()
        val responseChannel = testServer.outboundChannel

        val subscriptionJob = launch {
            testServer.handleSubscription(session)
                .collect()
        }

        session.send(mapper.writeValueAsString(SubscriptionMessagePing()))
        val pong: GraphQLSubscriptionMessage = mapper.readValue(responseChannel.receive())
        assertEquals(SubscriptionMessagePong(), pong)
        subscriptionJob.cancelAndJoin()
    }

    @Test
    fun `verify client can complete subscription`() = runTest {
        val handler = GraphQLRequestHandler(graphQL = testGraphQLEngine())
        val hooks = spyk(InMemorySubscriptionHooks())
        val testServer = InMemoryGraphQLSubscriptionServer(
            requestHandler = handler,
            hooks = hooks
        )

        val session = Channel<String>()
        val responseChannel = testServer.outboundChannel

        val subscriptionJob = launch {
            testServer.handleSubscription(session)
                .collect()
        }

        session.send(mapper.writeValueAsString(SubscriptionMessageConnectionInit()))
        val ack: GraphQLSubscriptionMessage = mapper.readValue(responseChannel.receive())
        assertEquals(GRAPHQL_WS_CONNECTION_ACK, ack.type)

        val id = UUID.randomUUID().toString()
        val request = GraphQLRequest(query = "subscription { counter }")
        val subscriptionOperation = SubscriptionMessageSubscribe(id = id, payload = request)
        session.send(mapper.writeValueAsString(subscriptionOperation))

        val counterResponse: GraphQLSubscriptionMessage = mapper.readValue(responseChannel.receive())
        assertTrue(counterResponse is SubscriptionMessageNext)
        assertEquals(id, counterResponse.id)

        session.send(mapper.writeValueAsString(SubscriptionMessageComplete(id)))
        delay(100)
        subscriptionJob.cancelAndJoin()

        coVerify {
            hooks.onOperationComplete(id, any(), any())
        }
    }

    @Test
    fun `verify unknown message is rejected`() = runTest {
        val handler = GraphQLRequestHandler(graphQL = testGraphQLEngine())
        val testServer = InMemoryGraphQLSubscriptionServer(requestHandler = handler)

        val session = Channel<String>()
        val responseChannel = testServer.outboundChannel

        val subscriptionJob = launch {
            testServer.handleSubscription(session)
                .collect()
        }

        session.send(mapper.writeValueAsString(SubscriptionMessageInvalid()))
        val error: GraphQLSubscriptionStatus = mapper.readValue(responseChannel.receive())
        assertEquals(GraphQLSubscriptionStatus.INVALID_MESSAGE, error)
        subscriptionJob.cancelAndJoin()
    }

    private fun testGraphQLEngine(): GraphQL = GraphQL.newGraphQL(
        toSchema(
            config = SchemaGeneratorConfig(
                supportedPackages = listOf("com.expediagroup.graphql.server.execution.subscription"),
                hooks = FlowSubscriptionSchemaGeneratorHooks()
            ),
            queries = listOf(TopLevelObject(TestQuery())),
            subscriptions = listOf(TopLevelObject(TestSubscription()))
        )
    )
        .subscriptionExecutionStrategy(FlowSubscriptionExecutionStrategy())
        .build()
}

class TestQuery {
    // single query is required for valid schema
    fun hello(): String = "Hello World"
}

class TestSubscription {
    fun counter(): Flow<Int> = flowOf(1, 2, 3)
        .onEach {
            delay(1000)
        }
}
