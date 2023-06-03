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
package com.expediagroup.graphql.server.ktor.subscriptions.graphqlws

import com.expediagroup.graphql.server.execution.GraphQLSubscriptionExecutor
import com.expediagroup.graphql.server.ktor.subscriptions.KtorGraphQLSubscriptionHooks
import com.expediagroup.graphql.server.ktor.subscriptions.TestWebSocketServerSession
import com.expediagroup.graphql.server.types.GraphQLRequest
import com.expediagroup.graphql.server.types.GraphQLResponse
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import graphql.GraphQLContext
import io.ktor.websocket.CloseReason
import io.ktor.websocket.Frame
import io.ktor.websocket.readReason
import io.ktor.websocket.readText
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.newFixedThreadPoolContext
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import java.util.concurrent.atomic.AtomicInteger
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull

@OptIn(DelicateCoroutinesApi::class, ExperimentalCoroutinesApi::class)
class KtorGraphQLWebSocketProtocolHandlerTest {

    private val objectMapper = jacksonObjectMapper()
    private val subscriptionHooks = mockk<KtorGraphQLSubscriptionHooks>(relaxed = true)
    private val subscriptionExecutor = mockk<GraphQLSubscriptionExecutor>()

    private val handler = KtorGraphQLWebSocketProtocolHandler(
        subscriptionExecutor = subscriptionExecutor,
        objectMapper = objectMapper,
        subscriptionHooks = subscriptionHooks,
    )

    private val session = TestWebSocketServerSession(newFixedThreadPoolContext(2, "ws-session"))

    @Test
    fun `runs one subscription`() = doInSession {
        val graphQLContext = mockk<GraphQLContext>("graphQlContext")
        coEvery { subscriptionHooks.onConnect(mapOf("foo" to "bar"), session) } returns graphQLContext

        initConnection("""{"type":"connection_init", "payload": { "foo": "bar" } }""")

        val expectedParsedRequest = GraphQLRequest(query = "subscription { flow }")

        every {
            subscriptionExecutor.executeSubscription(expectedParsedRequest, graphQLContext)
        } returns flowOf(GraphQLResponse(1), GraphQLResponse(2), GraphQLResponse(3))

        incoming.send(Frame.Text("""{"type": "subscribe", "id": "1", "payload": { "query": "subscription { flow }" }}"""))

        while (outgoing.isEmpty) delay(1)

        coVerify {
            subscriptionHooks.onOperation("1", expectedParsedRequest, session, graphQLContext)
        }

        assertEquals("""{"type":"next","id":"1","payload":{"data":1}}""", outgoing.receiveText())
        assertEquals("""{"type":"next","id":"1","payload":{"data":2}}""", outgoing.receiveText())
        assertEquals("""{"type":"next","id":"1","payload":{"data":3}}""", outgoing.receiveText())
        assertEquals("""{"type":"complete","id":"1"}""", outgoing.receiveText())

        coVerify {
            subscriptionHooks.onOperationComplete("1", session, graphQLContext)
        }

        assertNull(outgoing.tryReceive().getOrNull())
    }

   //  @Test
    fun `runs two subscriptions consequently`() = doInSession {
        val graphQLContext = mockk<GraphQLContext>("graphQlContext")
        coEvery { subscriptionHooks.onConnect(mapOf("foo" to "bar"), session) } returns graphQLContext

        initConnection("""{"type":"connection_init", "payload": { "foo": "bar" } }""")

        val expectedParsedRequest1 = GraphQLRequest(query = "subscription { flow }")
        val expectedParsedRequest2 = GraphQLRequest(query = "subscription { counter }")

        every {
            subscriptionExecutor.executeSubscription(expectedParsedRequest1, graphQLContext)
        } answers {
            coVerify { subscriptionHooks.onOperation("1", expectedParsedRequest1, session, graphQLContext) }
            flowOf(GraphQLResponse(1), GraphQLResponse(2), GraphQLResponse(3))
        }
        every {
            subscriptionExecutor.executeSubscription(expectedParsedRequest2, graphQLContext)
        } answers {
            coVerify { subscriptionHooks.onOperation("1", expectedParsedRequest2, session, graphQLContext) }
            flowOf(GraphQLResponse(4), GraphQLResponse(5), GraphQLResponse(6))
        }

        incoming.send(Frame.Text("""{"type": "subscribe", "id": "1", "payload": { "query": "subscription { flow }" }}"""))

        assertEquals("""{"type":"next","id":"1","payload":{"data":1}}""", outgoing.receiveText())
        assertEquals("""{"type":"next","id":"1","payload":{"data":2}}""", outgoing.receiveText())
        assertEquals("""{"type":"next","id":"1","payload":{"data":3}}""", outgoing.receiveText())
        assertEquals("""{"type":"complete","id":"1"}""", outgoing.receiveText())

        coVerify {
            subscriptionHooks.onOperationComplete("1", session, graphQLContext)
        }

        incoming.send(Frame.Text("""{"type": "subscribe", "id": "1", "payload": { "query": "subscription { counter }" }}"""))

        assertEquals("""{"type":"next","id":"1","payload":{"data":4}}""", outgoing.receiveText())
        assertEquals("""{"type":"next","id":"1","payload":{"data":5}}""", outgoing.receiveText())
        assertEquals("""{"type":"next","id":"1","payload":{"data":6}}""", outgoing.receiveText())
        assertEquals("""{"type":"complete","id":"1"}""", outgoing.receiveText())

        coVerify {
            subscriptionHooks.onOperationComplete("1", session, graphQLContext)
        }

        assertNull(outgoing.tryReceive().getOrNull())
    }

    @Test
    fun `runs two subscriptions simultaneously`() = doInSession {
        val graphQLContext = mockk<GraphQLContext>("graphQlContext")
        coEvery { subscriptionHooks.onConnect(mapOf("foo" to "bar"), session) } returns graphQLContext

        initConnection("""{"type":"connection_init", "payload": { "foo": "bar" } }""")

        val expectedParsedRequest1 = GraphQLRequest(query = "subscription { flow }")
        val expectedParsedRequest2 = GraphQLRequest(query = "subscription { counter }")

        every {
            subscriptionExecutor.executeSubscription(expectedParsedRequest1, graphQLContext)
        } answers {
            coVerify { subscriptionHooks.onOperation("1", expectedParsedRequest1, session, graphQLContext) }
            flowOf(GraphQLResponse(1), GraphQLResponse(2), GraphQLResponse(3))
        }
        every {
            subscriptionExecutor.executeSubscription(expectedParsedRequest2, graphQLContext)
        } answers {
            coVerify { subscriptionHooks.onOperation("2", expectedParsedRequest2, session, graphQLContext) }
            flowOf(GraphQLResponse(4), GraphQLResponse(5), GraphQLResponse(6))
        }

        incoming.send(Frame.Text("""{"type": "subscribe", "id": "1", "payload": { "query": "subscription { flow }" }}"""))
        incoming.send(Frame.Text("""{"type": "subscribe", "id": "2", "payload": { "query": "subscription { counter }" }}"""))

        val allMessages = (0..7).map { outgoing.receiveText() }.toList()

        assertEquals(
            expected = listOf(
                """{"type":"next","id":"1","payload":{"data":1}}""",
                """{"type":"next","id":"1","payload":{"data":2}}""",
                """{"type":"next","id":"1","payload":{"data":3}}""",
                """{"type":"complete","id":"1"}""",
            ),
            actual = allMessages.filter { it.contains(""""id":"1"""") },
        )
        assertEquals(
            expected = listOf(
                """{"type":"next","id":"2","payload":{"data":4}}""",
                """{"type":"next","id":"2","payload":{"data":5}}""",
                """{"type":"next","id":"2","payload":{"data":6}}""",
                """{"type":"complete","id":"2"}""",
            ),
            actual = allMessages.filter { it.contains(""""id":"2"""") },
        )

        coVerify {
            subscriptionHooks.onOperationComplete("1", session, graphQLContext)
        }
        coVerify {
            subscriptionHooks.onOperationComplete("2", session, graphQLContext)
        }

        assertNull(outgoing.tryReceive().getOrNull())
    }

    @Test
    fun `stops running subscription if requested`() = doInSession {
        val graphQLContext = mockk<GraphQLContext>("graphQlContext")
        coEvery { subscriptionHooks.onConnect(any(), session) } returns graphQLContext
        initConnection()

        val expectedParsedRequest = GraphQLRequest(query = "subscription { counter }")

        val counter = AtomicInteger()
        every {
            subscriptionExecutor.executeSubscription(expectedParsedRequest, graphQLContext)
        } returns flow {
            emit(GraphQLResponse(counter.incrementAndGet()))
            delay(1000)
        }

        incoming.send(Frame.Text("""{"type": "subscribe", "id": "1", "payload": { "query": "subscription { counter }" }}"""))

        while (outgoing.isEmpty) delay(1)
        coVerify {
            subscriptionHooks.onOperation("1", expectedParsedRequest, session, graphQLContext)
        }

        assertEquals("""{"type":"next","id":"1","payload":{"data":1}}""", outgoing.receiveText())

        incoming.send(Frame.Text("""{"type": "complete", "id": "1"}}"""))

        coVerify {
            subscriptionHooks.onOperationComplete("1", session, graphQLContext)
        }

        assertNull(outgoing.tryReceive().getOrNull())
    }

    @Test
    fun `does not fail if onOperationComplete fails`() = doInSession {
        val graphQLContext = mockk<GraphQLContext>("graphQlContext")
        coEvery { subscriptionHooks.onConnect(any(), session) } returns graphQLContext

        initConnection()

        val expectedParsedRequest = GraphQLRequest(query = "subscription { flow }")
        coEvery {
            subscriptionHooks.onOperationComplete("1", session, graphQLContext)
        } throws RuntimeException("should not fail")

        every {
            subscriptionExecutor.executeSubscription(expectedParsedRequest, graphQLContext)
        } returns flowOf(GraphQLResponse(1), GraphQLResponse(2), GraphQLResponse(3))

        incoming.send(Frame.Text("""{"type": "subscribe", "id": "1", "payload": { "query": "subscription { flow }" }}"""))

        assertEquals("""{"type":"next","id":"1","payload":{"data":1}}""", outgoing.receiveText())
        assertEquals("""{"type":"next","id":"1","payload":{"data":2}}""", outgoing.receiveText())
        assertEquals("""{"type":"next","id":"1","payload":{"data":3}}""", outgoing.receiveText())
        assertEquals("""{"type":"complete","id":"1"}""", outgoing.receiveText())

        coVerify {
            subscriptionHooks.onOperationComplete("1", session, graphQLContext)
        }

        assertNull(outgoing.tryReceive().getOrNull())
    }

    @Test
    fun `closes with 4409 if subscribed on the same operation twice`() = doInSession {
        val graphQLContext = mockk<GraphQLContext>("graphQlContext")
        coEvery { subscriptionHooks.onConnect(any(), session) } returns graphQLContext
        initConnection()

        val expectedParsedRequest = GraphQLRequest(query = "subscription { counter }")

        val counter = AtomicInteger()
        every {
            subscriptionExecutor.executeSubscription(expectedParsedRequest, graphQLContext)
        } returns flow {
            emit(GraphQLResponse(counter.incrementAndGet()))
            delay(1000)
        }

        incoming.send(Frame.Text("""{"type": "subscribe", "id": "999", "payload": { "query": "subscription { counter }" }}"""))

        while (outgoing.isEmpty) delay(1)
        coVerify {
            subscriptionHooks.onOperation("999", expectedParsedRequest, session, graphQLContext)
        }
        assertEquals("""{"type":"next","id":"999","payload":{"data":1}}""", outgoing.receiveText())

        incoming.send(Frame.Text("""{"type": "subscribe", "id": "999", "payload": { "query": "subscription { anotherQuery }" }}"""))

        val closeMsg = outgoing.receiveClose()
        assertEquals(CloseReason(4409, "Subscriber for 999 already exists"), closeMsg)
    }

    @Test
    fun `closes with 4400 code on unknown operation`() = doInSession {
        val graphQLContext = mockk<GraphQLContext>("graphQlContext")
        coEvery { subscriptionHooks.onConnect(any(), session) } returns graphQLContext
        initConnection()

        incoming.send(Frame.Text("""{"type": "unknown", "id": "1", "payload": { "query": "subscription { counter }" }}"""))

        val closeMsg = outgoing.receiveClose()
        assertEquals(CloseReason(4400, "Unexpected operation unknown"), closeMsg)
    }

    @Test
    fun `closes with 4400 code on invalid json query`() = doInSession {
        val graphQLContext = mockk<GraphQLContext>("graphQlContext")
        coEvery { subscriptionHooks.onConnect(any(), session) } returns graphQLContext
        initConnection()

        incoming.send(Frame.Text("""{"type": "subscribe", "id": "1", "payload": 42}"""))

        val closeMsg = outgoing.receiveClose()
        assertEquals(CloseReason(4400, "Error when parsing GraphQL request data"), closeMsg)
    }

    @Test
    fun `closes with 4400 if there is an error thrown from onOperation hook (1)`() = doInSession {
        val graphQLContext = mockk<GraphQLContext>("graphQlContext")
        coEvery { subscriptionHooks.onConnect(any(), session) } returns graphQLContext
        initConnection()

        val expectedParsedRequest = GraphQLRequest(query = "subscription { counter }")
        coEvery {
            subscriptionHooks.onOperation("4", expectedParsedRequest, session, graphQLContext)
        } throws RuntimeException("message from exception")

        incoming.send(Frame.Text("""{"type": "subscribe", "id": "4", "payload": { "query": "subscription { counter }" }}"""))

        val closeMsg = outgoing.receiveClose()
        assertEquals(CloseReason(4400, "message from exception"), closeMsg)
    }

    @Test
    fun `closes with 4400 if there is an error thrown from onOperation hook (2)`() = doInSession {
        val graphQLContext = mockk<GraphQLContext>("graphQlContext")
        coEvery { subscriptionHooks.onConnect(any(), session) } returns graphQLContext
        initConnection()

        val expectedParsedRequest = GraphQLRequest(query = "subscription { counter }")
        coEvery {
            subscriptionHooks.onOperation("4", expectedParsedRequest, session, graphQLContext)
        } throws RuntimeException()

        incoming.send(Frame.Text("""{"type": "subscribe", "id": "4", "payload": { "query": "subscription { counter }" }}"""))

        val closeMsg = outgoing.receiveClose()
        assertEquals(CloseReason(4400, "Error running onOperation hook for operation=4"), closeMsg)
    }

    @Test
    fun `calls onDisconnect hook when disconnected by client`() = doInSession {
        val graphQLContext = mockk<GraphQLContext>("graphQlContext")
        coEvery { subscriptionHooks.onConnect(any(), session) } returns graphQLContext
        initConnection()

        incoming.close()

        coVerify {
            subscriptionHooks.onDisconnect(session, graphQLContext)
        }
    }

    @Test
    fun `closes with 4401 code when trying to execute request without ConnectionInit`() = doInSession {
        incoming.send(Frame.Text("""{"type": "subscribe", "id": "3", "payload": { "query": "subscription { counter(limit: 5) }" }}"""))

        val closeMsg = outgoing.receiveClose()
        assertEquals(CloseReason(4401, "Unauthorized"), closeMsg)
    }

    @Test
    fun `closes with 4403 code when onConnect hook has thrown an error`() = doInSession {
        coEvery {
            subscriptionHooks.onConnect(any(), session)
        } throws RuntimeException("should close session")

        incoming.send(Frame.Text(CONNECTION_INIT_MGS))

        val closeMsg = outgoing.receiveClose()
        assertEquals(CloseReason(4403, "Forbidden"), closeMsg)
    }

    @Test
    fun `responds to pings with pongs`() = doInSession {
        initConnection()

        incoming.send(Frame.Text(PING_MGS))
        val pong = outgoing.receiveText()
        assertEquals("""{"type":"pong"}""", pong)
    }

    @Test
    fun `closes with 4401 code when missing id on subscribe`() = doInSession {
        initConnection()

        incoming.send(Frame.Text("""{"type": "subscribe", "payload": { "query": "subscription { counter(limit: 5) }" }}"""))

        val closeMsg = outgoing.receiveClose()
        assertEquals(CloseReason(4400, "Missing id from subscription message"), closeMsg)
    }

    @Test
    fun `closes with 4401 code when missing payload on subscribe`() = doInSession {
        initConnection()

        incoming.send(Frame.Text("""{"type": "subscribe", "id": "123" }}"""))

        val closeMsg = outgoing.receiveClose()
        assertEquals(CloseReason(4400, "Missing payload from subscription message"), closeMsg)
    }

    @Test
    fun `closes with 4401 code when missing id on complete`() = doInSession {
        val graphQLContext = mockk<GraphQLContext>("graphQlContext")
        coEvery { subscriptionHooks.onConnect(any(), session) } returns graphQLContext
        initConnection()

        val expectedParsedRequest = GraphQLRequest(query = "subscription { counter }")
        val counter = AtomicInteger()
        every {
            subscriptionExecutor.executeSubscription(expectedParsedRequest, graphQLContext)
        } returns flow {
            emit(GraphQLResponse(counter.incrementAndGet()))
            delay(1000)
        }

        incoming.send(Frame.Text("""{"type": "subscribe", "id": "1", "payload": { "query": "subscription { counter }" }}"""))
        assertEquals("""{"type":"next","id":"1","payload":{"data":1}}""", outgoing.receiveText())

        incoming.send(Frame.Text("""{"type": "complete"}}"""))

        val closeMsg = outgoing.receiveClose()
        assertEquals(CloseReason(4400, "Missing id from subscription message"), closeMsg)
    }

    @Test
    fun `does not fail if client requested to cancel unknown id`() = doInSession {
        val graphQLContext = mockk<GraphQLContext>("graphQlContext")
        coEvery { subscriptionHooks.onConnect(mapOf("foo" to "bar"), session) } returns graphQLContext

        initConnection("""{"type":"connection_init", "payload": { "foo": "bar" } }""")

        incoming.send(Frame.Text("""{"type": "complete", "id": "1"}}"""))
        incoming.send(Frame.Text("""{"type": "complete", "id": "2"}}"""))
        incoming.send(Frame.Text("""{"type": "complete", "id": "3"}}"""))

        val expectedParsedRequest = GraphQLRequest(query = "subscription { flow }")

        every {
            subscriptionExecutor.executeSubscription(expectedParsedRequest, graphQLContext)
        } returns flowOf(GraphQLResponse(1), GraphQLResponse(2), GraphQLResponse(3))

        incoming.send(Frame.Text("""{"type": "subscribe", "id": "1", "payload": { "query": "subscription { flow }" }}"""))

        while (outgoing.isEmpty) delay(1)

        coVerify {
            subscriptionHooks.onOperation("1", expectedParsedRequest, session, graphQLContext)
        }

        assertEquals("""{"type":"next","id":"1","payload":{"data":1}}""", outgoing.receiveText())
        assertEquals("""{"type":"next","id":"1","payload":{"data":2}}""", outgoing.receiveText())
        assertEquals("""{"type":"next","id":"1","payload":{"data":3}}""", outgoing.receiveText())
        assertEquals("""{"type":"complete","id":"1"}""", outgoing.receiveText())

        coVerify {
            subscriptionHooks.onOperationComplete("1", session, graphQLContext)
        }

        assertNull(outgoing.tryReceive().getOrNull())
    }

    @Test
    fun `closes with 4400 code on unexpected frame type`() = doInSession {
        initConnection()

        incoming.send(Frame.Binary(true, ByteArray(0)))

        val closeMsg = outgoing.receiveClose()
        assertEquals(CloseReason(4400, "Expected to get TEXT but got BINARY"), closeMsg)
    }

    @Test
    fun `closes with 4400 code on parse error`() = doInSession {
        initConnection()

        incoming.send(Frame.Text("{}"))

        val closeMsg = outgoing.receiveClose()
        assertEquals(4400, closeMsg?.code)
    }

    private fun doInSession(block: suspend TestWebSocketServerSession.() -> Unit): Unit = runBlocking {
        val handlerJob = launch { handler.handle(session) }
        try {
            block.invoke(session)
            handlerJob.cancelAndJoin()
        } finally {
            session.closeChannels()
        }
    }

    private suspend fun TestWebSocketServerSession.initConnection(initMsg: String = """{"type": "connection_init"}""") {
        incoming.send(Frame.Text(initMsg))
        val ack = outgoing.receiveText()
        assertEquals("""{"type":"connection_ack"}""", ack)
    }

    private suspend fun Channel<Frame>.receiveText(): String {
        val frame = receive()
        assertIs<Frame.Text>(frame)
        return frame.readText()
    }

    private suspend fun Channel<Frame>.receiveClose(): CloseReason? {
        val frame = receive()
        assertIs<Frame.Close>(frame)
        return frame.readReason()
    }

    companion object {
        const val CONNECTION_INIT_MGS = """{"type": "connection_init"}"""
        const val PING_MGS = """{"type":"ping"}"}}"""
    }
}
