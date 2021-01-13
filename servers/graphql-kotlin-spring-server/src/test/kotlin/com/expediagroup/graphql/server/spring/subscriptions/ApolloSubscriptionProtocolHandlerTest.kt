/*
 * Copyright 2021 Expedia, Inc
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

import com.expediagroup.graphql.generator.execution.DefaultGraphQLContext
import com.expediagroup.graphql.server.spring.GraphQLConfigurationProperties
import com.expediagroup.graphql.server.spring.subscriptions.SubscriptionOperationMessage.ClientMessages.GQL_CONNECTION_INIT
import com.expediagroup.graphql.server.spring.subscriptions.SubscriptionOperationMessage.ClientMessages.GQL_CONNECTION_TERMINATE
import com.expediagroup.graphql.server.spring.subscriptions.SubscriptionOperationMessage.ClientMessages.GQL_START
import com.expediagroup.graphql.server.spring.subscriptions.SubscriptionOperationMessage.ClientMessages.GQL_STOP
import com.expediagroup.graphql.server.spring.subscriptions.SubscriptionOperationMessage.ServerMessages.GQL_COMPLETE
import com.expediagroup.graphql.server.spring.subscriptions.SubscriptionOperationMessage.ServerMessages.GQL_CONNECTION_ACK
import com.expediagroup.graphql.server.spring.subscriptions.SubscriptionOperationMessage.ServerMessages.GQL_CONNECTION_ERROR
import com.expediagroup.graphql.server.spring.subscriptions.SubscriptionOperationMessage.ServerMessages.GQL_DATA
import com.expediagroup.graphql.server.spring.subscriptions.SubscriptionOperationMessage.ServerMessages.GQL_ERROR
import com.expediagroup.graphql.types.GraphQLError
import com.expediagroup.graphql.types.GraphQLRequest
import com.expediagroup.graphql.types.GraphQLResponse
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.mockk.verifyOrder
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.reactor.mono
import org.junit.jupiter.api.Test
import org.springframework.web.reactive.socket.WebSocketSession
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.time.Duration
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@ExperimentalCoroutinesApi
class ApolloSubscriptionProtocolHandlerTest {

    private val objectMapper = jacksonObjectMapper()
    private val subscriptionHooks = SimpleSubscriptionHooks()
    private fun SubscriptionOperationMessage.toJson() = objectMapper.writeValueAsString(this)
    private val nullContextFactory: SpringSubscriptionGraphQLContextFactory<*> = mockk {
        coEvery { generateContext(any()) } returns null
    }
    private val simpleInitMessage = SubscriptionOperationMessage(GQL_CONNECTION_INIT.type)

    @Test
    fun `Return GQL_CONNECTION_ERROR when payload is not a SubscriptionOperationMessage`() {
        val config: GraphQLConfigurationProperties = mockk()
        val session: WebSocketSession = mockk()
        val subscriptionHandler: SpringGraphQLSubscriptionHandler = mockk()

        val handler = ApolloSubscriptionProtocolHandler(config, nullContextFactory, subscriptionHandler, objectMapper, subscriptionHooks)
        val flux = handler.handle("", session)

        val message = flux.blockFirst(Duration.ofSeconds(2))
        assertNotNull(message)
        assertEquals(expected = GQL_CONNECTION_ERROR.type, actual = message.type)
    }

    @Test
    fun `Return GQL_CONNECTION_ERROR when SubscriptionOperationMessage type is not valid`() {
        val config: GraphQLConfigurationProperties = mockk()
        val operationMessage = SubscriptionOperationMessage("", id = "abc").toJson()
        val session: WebSocketSession = mockk {
            every { id } returns "abc"
        }
        val subscriptionHandler: SpringGraphQLSubscriptionHandler = mockk()

        val handler = ApolloSubscriptionProtocolHandler(config, nullContextFactory, subscriptionHandler, objectMapper, subscriptionHooks)
        val flux = handler.handle(operationMessage, session)

        val message = flux.blockFirst(Duration.ofSeconds(2))
        assertNotNull(message)
        assertEquals(expected = GQL_CONNECTION_ERROR.type, actual = message.type)
        assertEquals(expected = "abc", actual = message.id)
    }

    @Test
    fun `Return GQL_CONNECTION_ACK when sending GQL_CONNECTION_INIT and keep alive is off`() {
        val config: GraphQLConfigurationProperties = mockk {
            every { subscriptions } returns mockk {
                every { keepAliveInterval } returns null
            }
        }
        val session: WebSocketSession = mockk {
            every { id } returns "123"
        }
        val subscriptionHandler: SpringGraphQLSubscriptionHandler = mockk()

        val handler = ApolloSubscriptionProtocolHandler(config, nullContextFactory, subscriptionHandler, objectMapper, subscriptionHooks)
        val flux = handler.handle(simpleInitMessage.toJson(), session)

        val message = flux.blockFirst(Duration.ofSeconds(2))
        assertNotNull(message)
        assertEquals(expected = GQL_CONNECTION_ACK.type, actual = message.type)
    }

    @Test
    fun `Return only GQL_CONNECTION_ACK when sending GQL_CONNECTION_INIT and keep alive is on but no id is sent`() {
        val config: GraphQLConfigurationProperties = mockk {
            every { subscriptions } returns mockk {
                every { keepAliveInterval } returns 500
            }
        }
        val session: WebSocketSession = mockk {
            every { id } returns "123"
        }
        val subscriptionHandler: SpringGraphQLSubscriptionHandler = mockk()

        val handler = ApolloSubscriptionProtocolHandler(config, nullContextFactory, subscriptionHandler, objectMapper, subscriptionHooks)
        val flux = handler.handle(simpleInitMessage.toJson(), session)

        val message = flux.blockFirst(Duration.ofSeconds(2))
        assertNotNull(message)
        assertEquals(expected = GQL_CONNECTION_ACK.type, actual = message.type)
    }

    @Test
    fun `Return GQL_CONNECTION_ACK + GQL_CONNECTION_KEEP_ALIVE when sent type is GQL_CONNECTION_INIT and keep alive is on and id is sent`() {
        val config: GraphQLConfigurationProperties = mockk {
            every { subscriptions } returns mockk {
                every { keepAliveInterval } returns 500
            }
        }
        val operationMessage = SubscriptionOperationMessage(GQL_CONNECTION_INIT.type, id = "abc").toJson()
        val session: WebSocketSession = mockk {
            every { id } returns "1"
        }
        val subscriptionHandler: SpringGraphQLSubscriptionHandler = mockk()

        val handler = ApolloSubscriptionProtocolHandler(config, nullContextFactory, subscriptionHandler, objectMapper, subscriptionHooks)

        val initFlux = handler.handle(operationMessage, session)

        val message = initFlux.blockFirst(Duration.ofSeconds(2))
        assertNotNull(message)
        assertEquals(expected = GQL_CONNECTION_ACK.type, actual = message.type)
        initFlux.subscribe().dispose()
    }

    @Test
    fun `Close session when sending GQL_CONNECTION_TERMINATE`() {
        val config: GraphQLConfigurationProperties = mockk()
        val operationMessage = SubscriptionOperationMessage(GQL_CONNECTION_TERMINATE.type)
        val session: WebSocketSession = mockk {
            every { id } returns "123"
            every { close() } returns mockk()
        }
        val subscriptionHandler: SpringGraphQLSubscriptionHandler = mockk()

        val handler = ApolloSubscriptionProtocolHandler(config, nullContextFactory, subscriptionHandler, objectMapper, subscriptionHooks)
        val flux = handler.handle(objectMapper.writeValueAsString(operationMessage), session)

        StepVerifier.create(flux)
            .verifyComplete()

        verify(exactly = 1) { session.close() }
    }

    @Test
    fun `Close session when sending GQL_CONNECTION_TERMINATE with id`() {
        val config: GraphQLConfigurationProperties = mockk()
        val operationMessage = SubscriptionOperationMessage(GQL_CONNECTION_TERMINATE.type, id = "123").toJson()
        val session: WebSocketSession = mockk {
            every { close() } returns mockk()
            every { id } returns "abc"
        }
        val subscriptionHandler: SpringGraphQLSubscriptionHandler = mockk()

        val handler = ApolloSubscriptionProtocolHandler(config, nullContextFactory, subscriptionHandler, objectMapper, subscriptionHooks)
        val flux = handler.handle(operationMessage, session)

        StepVerifier.create(flux)
            .expectNextCount(0)
            .thenAwait()
            .verifyComplete()

        verify(exactly = 1) { session.close() }
    }

    @Test
    fun `Stop sending messages but keep connection open when sending GQL_STOP`() {
        val config: GraphQLConfigurationProperties = mockk()
        val operationMessage = SubscriptionOperationMessage(GQL_STOP.type).toJson()
        val session: WebSocketSession = mockk {
            every { close() } returns mockk()
            every { id } returns "abc"
        }
        val subscriptionHandler: SpringGraphQLSubscriptionHandler = mockk()

        val handler = ApolloSubscriptionProtocolHandler(config, nullContextFactory, subscriptionHandler, objectMapper, subscriptionHooks)
        val flux = handler.handle(operationMessage, session)

        StepVerifier.create(flux)
            .expectNextCount(0)
            .thenCancel()
            .verify()

        verify(exactly = 0) { session.close() }
    }

    @Test
    fun `Stop sending messages but keep connection open and keep sending GQL_CONNECTION_KEEP_ALIVE when client sends GQL_STOP`() {
        val config: GraphQLConfigurationProperties = mockk {
            every { subscriptions } returns mockk {
                every { keepAliveInterval } returns 1
            }
        }
        val session: WebSocketSession = mockk {
            every { close() } returns mockk()
            every { id } returns "abc"
        }
        val subscriptionHandler: SpringGraphQLSubscriptionHandler = mockk()
        val handler = ApolloSubscriptionProtocolHandler(config, nullContextFactory, subscriptionHandler, objectMapper, subscriptionHooks)

        val stopMessage = SubscriptionOperationMessage(GQL_STOP.type).toJson()

        val initFlux = handler.handle(simpleInitMessage.toJson(), session)
        val stopFlux = handler.handle(stopMessage, session)

        StepVerifier.create(initFlux)
            .expectSubscription()
            .expectNextMatches { it.type == "connection_ack" }
            .expectNextMatches { it.type == "ka" }
            .thenCancel()
            .verify()

        StepVerifier.create(stopFlux)
            .expectSubscription()
            .thenCancel()
            .verify()

        verify(exactly = 0) { session.close() }
    }

    @Test
    fun `Return GQL_CONNECTION_ERROR when sending GQL_START but id is null`() {
        val config: GraphQLConfigurationProperties = mockk()
        val operationMessage = SubscriptionOperationMessage(type = GQL_START.type, id = null).toJson()
        val mockSession: WebSocketSession = mockk { every { id } returns "123" }
        val subscriptionHandler: SpringGraphQLSubscriptionHandler = mockk()

        val handler = ApolloSubscriptionProtocolHandler(config, nullContextFactory, subscriptionHandler, objectMapper, subscriptionHooks)
        val flux = handler.handle(operationMessage, mockSession)

        val message = flux.blockFirst(Duration.ofSeconds(2))
        assertNotNull(message)
        assertEquals(expected = GQL_CONNECTION_ERROR.type, actual = message.type)
    }

    @Test
    fun `Return GQL_CONNECTION_ERROR when sending GQL_START but payload is null`() {
        val config: GraphQLConfigurationProperties = mockk()
        val operationMessage = SubscriptionOperationMessage(type = GQL_START.type, id = "abc", payload = null).toJson()
        val session: WebSocketSession = mockk {
            every { id } returns "abc"
        }
        val subscriptionHandler: SpringGraphQLSubscriptionHandler = mockk()

        val handler = ApolloSubscriptionProtocolHandler(config, nullContextFactory, subscriptionHandler, objectMapper, subscriptionHooks)
        handler.handle(simpleInitMessage.toJson(), session)
        val flux = handler.handle(operationMessage, session)

        val message = flux.blockFirst(Duration.ofSeconds(2))
        assertNotNull(message)
        assertEquals(expected = GQL_CONNECTION_ERROR.type, actual = message.type)
        assertEquals(expected = "abc", actual = message.id)
    }

    @Test
    fun `Return GQL_CONNECTION_ERROR when sending GQL_START but payload is invalid GraphQLRequest`() {
        val config: GraphQLConfigurationProperties = mockk()
        val operationMessage = SubscriptionOperationMessage(type = GQL_START.type, id = "abc", payload = "").toJson()
        val session: WebSocketSession = mockk {
            every { id } returns "abc"
        }
        val subscriptionHandler: SpringGraphQLSubscriptionHandler = mockk()

        val handler = ApolloSubscriptionProtocolHandler(config, nullContextFactory, subscriptionHandler, objectMapper, subscriptionHooks)
        handler.handle(simpleInitMessage.toJson(), session)
        val flux = handler.handle(operationMessage, session)

        val message = flux.blockFirst(Duration.ofSeconds(2))
        assertNotNull(message)
        assertEquals(expected = GQL_CONNECTION_ERROR.type, actual = message.type)
        assertEquals(expected = "abc", actual = message.id)
    }

    @Test
    fun `Return GQL_DATA when sending GQL_START with valid GraphQLRequest`() {
        val config: GraphQLConfigurationProperties = mockk()
        val graphQLRequest = GraphQLRequest("{ message }")
        val operationMessage = SubscriptionOperationMessage(type = GQL_START.type, id = "abc", payload = graphQLRequest).toJson()
        val session: WebSocketSession = mockk {
            every { close() } returns mockk()
            every { id } returns "123"
        }
        val subscriptionHandler: SpringGraphQLSubscriptionHandler = mockk {
            every { executeSubscription(eq(graphQLRequest), any()) } returns Flux.just(GraphQLResponse("myData"))
        }

        val handler = ApolloSubscriptionProtocolHandler(config, nullContextFactory, subscriptionHandler, objectMapper, subscriptionHooks)
        handler.handle(simpleInitMessage.toJson(), session)
        val flux = handler.handle(operationMessage, session)
        StepVerifier.create(flux)
            .expectNextMatches {
                val payload = it.payload as? GraphQLResponse<*>
                it.type == GQL_DATA.type &&
                    it.id == "abc" &&
                    payload?.data == "myData"
            }
            .expectNextMatches { it.type == GQL_COMPLETE.type }
            .expectComplete()
            .verify()
        verify(exactly = 0) { session.close() }
    }

    @Test
    fun `Return empty flux when sending GQL_START and already connected operation`() {
        val config: GraphQLConfigurationProperties = mockk()
        val graphQLRequest = GraphQLRequest("{ message }")
        val operationMessage = SubscriptionOperationMessage(type = GQL_START.type, id = "123", payload = graphQLRequest).toJson()
        val session: WebSocketSession = mockk {
            every { close() } returns mockk()
            every { id } returns "123"
        }
        val subscriptionHandler: SpringGraphQLSubscriptionHandler = mockk {
            // Never closes
            every { executeSubscription(eq(graphQLRequest), any()) } returns Flux.interval(Duration.ofSeconds(1)).map { GraphQLResponse("myData") }
        }

        val handler = ApolloSubscriptionProtocolHandler(config, nullContextFactory, subscriptionHandler, objectMapper, subscriptionHooks)

        // Force get a result to cache the operation
        handler.handle(simpleInitMessage.toJson(), session)
        handler.handle(operationMessage, session).blockFirst()

        verify(exactly = 0) { session.close() }

        // Second call with same id should return empty flux
        val flux2 = handler.handle(operationMessage, session)
        StepVerifier.create(flux2)
            .expectComplete()
            .verify()
    }

    @Test
    fun `Return GQL_COMPLETE when sending GQL_STOP with GraphQLRequest having operation id of running operation`() {
        val config: GraphQLConfigurationProperties = mockk()
        val graphQLRequest = GraphQLRequest("{ message }")
        val startRequest = SubscriptionOperationMessage(type = GQL_START.type, id = "abc", payload = graphQLRequest).toJson()
        val stopRequest = SubscriptionOperationMessage(type = GQL_STOP.type, id = "abc").toJson()
        val session: WebSocketSession = mockk {
            every { close() } returns mockk()
            every { id } returns "123"
        }
        val subscriptionHandler: SpringGraphQLSubscriptionHandler = mockk {
            every { executeSubscription(eq(graphQLRequest), any()) } returns Flux.just(GraphQLResponse("myData"))
        }

        val handler = ApolloSubscriptionProtocolHandler(config, nullContextFactory, subscriptionHandler, objectMapper, subscriptionHooks)

        handler.handle(simpleInitMessage.toJson(), session)
        val startFlux = handler.handle(startRequest, session)
        StepVerifier.create(startFlux)
            .expectNextMatches { it.type == GQL_DATA.type }
            .expectNextMatches { it.type == GQL_COMPLETE.type }
            .expectComplete()
            .verify()

        val stopFlux = handler.handle(stopRequest, session)
        StepVerifier.create(stopFlux)
            .expectNextMatches { it.type == GQL_COMPLETE.type }
            .thenCancel()
            .verify()

        verify(exactly = 0) { session.close() }
    }

    @Test
    fun `Dont start second subscription when operation id is already in activeOperations`() {
        val config: GraphQLConfigurationProperties = mockk()
        val graphQLRequest = GraphQLRequest("{ message }")
        val operationMessage = SubscriptionOperationMessage(type = GQL_START.type, id = "abc", payload = graphQLRequest).toJson()
        val session: WebSocketSession = mockk {
            every { close() } returns mockk()
            every { id } returns "123"
        }
        val subscriptionHandler: SpringGraphQLSubscriptionHandler = mockk {
            every { executeSubscription(eq(graphQLRequest), any()) } returns Flux.just(GraphQLResponse("myData"))
        }

        val handler = ApolloSubscriptionProtocolHandler(config, nullContextFactory, subscriptionHandler, objectMapper, subscriptionHooks)
        handler.handle(simpleInitMessage.toJson(), session)
        val flux = handler.handle(operationMessage, session)
        StepVerifier.create(flux)
            .expectNextMatches { it.type == GQL_DATA.type }
            .expectNextMatches { it.type == GQL_COMPLETE.type }
            .expectComplete()
            .verify()
        val fluxTwo = handler.handle(operationMessage, session)
        StepVerifier.create(fluxTwo)
            .expectNextMatches { it.type == GQL_DATA.type }
            .expectNextMatches { it.type == GQL_COMPLETE.type }
            .expectComplete()
            .verify()
        verify(exactly = 0) { session.close() }
    }

    @Test
    fun `Return GQL_ERROR when sending GQL_START with valid GraphQLRequest but response has errors`() {
        val config: GraphQLConfigurationProperties = mockk()
        val graphQLRequest = GraphQLRequest("{ message }")
        val operationMessage = SubscriptionOperationMessage(type = GQL_START.type, id = "abc", payload = graphQLRequest).toJson()
        val session: WebSocketSession = mockk {
            every { close() } returns mockk()
            every { id } returns "123"
        }
        val errors = listOf(GraphQLError("My GraphQL Error"))
        val subscriptionHandler: SpringGraphQLSubscriptionHandler = mockk {
            every { executeSubscription(eq(graphQLRequest), any()) } returns Flux.just(GraphQLResponse<Any>(errors = errors))
        }

        val handler = ApolloSubscriptionProtocolHandler(config, nullContextFactory, subscriptionHandler, objectMapper, subscriptionHooks)
        handler.handle(simpleInitMessage.toJson(), session)
        val flux = handler.handle(operationMessage, session)

        StepVerifier.create(flux)
            .expectNextMatches {
                val payload = it.payload as? GraphQLResponse<*>
                it.type == GQL_ERROR.type &&
                    it.id == "abc" &&
                    payload?.errors?.isNotEmpty() == true
            }
            .expectNextMatches { it.type == GQL_COMPLETE.type }
            .expectComplete()
            .verify()
        verify(exactly = 0) { session.close() }
    }

    @Test
    fun `Verify that onConnect is called during an init message`() {
        val config: GraphQLConfigurationProperties = mockk {
            every { subscriptions } returns mockk {
                every { keepAliveInterval } returns null
            }
        }
        val session: WebSocketSession = mockk {
            every { id } returns "123"
        }
        val subscriptionHandler: SpringGraphQLSubscriptionHandler = mockk()
        val subscriptionHooks: ApolloSubscriptionHooks = mockk {
            every { onConnect(any(), any(), any()) } returns mono { null }
        }
        val handler = ApolloSubscriptionProtocolHandler(config, nullContextFactory, subscriptionHandler, objectMapper, subscriptionHooks)
        val flux = handler.handle(simpleInitMessage.toJson(), session)
        flux.blockFirst(Duration.ofSeconds(2))
        verify(exactly = 1) { subscriptionHooks.onConnect(any(), session, any()) }
    }

    @Test
    fun `Verify that payload is passed to onConnect during an init message`() {
        val config: GraphQLConfigurationProperties = mockk {
            every { subscriptions } returns mockk {
                every { keepAliveInterval } returns null
            }
        }
        val payload = mapOf("message" to "test")
        val operationMessage = SubscriptionOperationMessage(GQL_CONNECTION_INIT.type, payload = payload).toJson()
        val session: WebSocketSession = mockk {
            every { id } returns "123"
        }
        val subscriptionHandler: SpringGraphQLSubscriptionHandler = mockk()
        val subscriptionHooks: ApolloSubscriptionHooks = mockk {
            every { onConnect(any(), any(), any()) } returns mono { null }
        }
        val handler = ApolloSubscriptionProtocolHandler(config, nullContextFactory, subscriptionHandler, objectMapper, subscriptionHooks)
        val flux = handler.handle(operationMessage, session)
        flux.blockFirst(Duration.ofSeconds(2))
        verify(exactly = 1) { subscriptionHooks.onConnect(payload, session, any()) }
    }

    @Test
    fun `Verify that onConnect and onOperation are called during a start message`() {
        val config: GraphQLConfigurationProperties = mockk {
            every { subscriptions } returns mockk {
                every { keepAliveInterval } returns null
            }
        }
        val graphQLRequest = GraphQLRequest("{ message }")
        val startMessage = SubscriptionOperationMessage(GQL_START.type, id = "abc", payload = graphQLRequest).toJson()
        val session: WebSocketSession = mockk {
            every { id } returns "123"
        }
        val expectedResponse = GraphQLResponse("myData")
        val subscriptionHandler: SpringGraphQLSubscriptionHandler = mockk {
            every { executeSubscription(eq(graphQLRequest), any()) } returns Flux.just(expectedResponse)
        }
        val subscriptionHooks: ApolloSubscriptionHooks = mockk {
            every { onConnect(any(), any(), any()) } returns Mono.just(DefaultGraphQLContext())
            every { onOperation(any(), any(), any()) } returns Unit
            every { onOperationComplete(any()) } returns Unit
        }
        val handler = ApolloSubscriptionProtocolHandler(config, nullContextFactory, subscriptionHandler, objectMapper, subscriptionHooks)
        handler.handle(simpleInitMessage.toJson(), session)
        val startFlux = handler.handle(startMessage, session)
        StepVerifier.create(startFlux)
            .expectNextMatches { it.type == GQL_DATA.type && it.payload == expectedResponse }
            .expectNextMatches { it.type == GQL_COMPLETE.type }
            .expectComplete()
            .verify()

        verify(exactly = 1) {
            subscriptionHooks.onConnect(any(), any(), any())
            subscriptionHooks.onOperation(any(), any(), any())
        }
        verifyOrder {
            subscriptionHooks.onConnect(any(), any(), any())
            subscriptionHooks.onOperation(any(), any(), any())
        }
    }

    @Test
    fun `Do not send any messages when onConnect throws error`() {
        val config: GraphQLConfigurationProperties = mockk {
            every { subscriptions } returns mockk {
                every { keepAliveInterval } returns null
            }
        }
        val initMessage = simpleInitMessage.toJson()
        val startMessage = SubscriptionOperationMessage(GQL_START.type).toJson()
        val session: WebSocketSession = mockk {
            every { id } returns "123"
        }
        val subscriptionHandler: SpringGraphQLSubscriptionHandler = mockk()
        val subscriptionHooks: ApolloSubscriptionHooks = mockk {
            every { onConnect(any(), any(), any()) } throws Exception()
            every { onOperation(any(), any(), any()) } returns Unit
        }
        val handler = ApolloSubscriptionProtocolHandler(config, nullContextFactory, subscriptionHandler, objectMapper, subscriptionHooks)
        val initFlux = handler.handle(initMessage, session)
        val startFlux = handler.handle(startMessage, session)
        initFlux.blockFirst(Duration.ofSeconds(2))
        StepVerifier.create(startFlux)
            .expectNextMatches {
                it.type == GQL_CONNECTION_ERROR.type && it.payload == null
            }
            .expectComplete()
            .verify()
    }

    @Test
    fun `Verify that onOperationComplete is called during a stop message`() {
        val config: GraphQLConfigurationProperties = mockk {
            every { subscriptions } returns mockk {
                every { keepAliveInterval } returns null
            }
        }
        val operationMessage = SubscriptionOperationMessage(GQL_STOP.type).toJson()
        val session: WebSocketSession = mockk {
            every { id } returns "123"
        }
        val subscriptionHandler: SpringGraphQLSubscriptionHandler = mockk()
        val subscriptionHooks: ApolloSubscriptionHooks = mockk {
            every { onOperationComplete(session) } returns Unit
        }
        val handler = ApolloSubscriptionProtocolHandler(config, nullContextFactory, subscriptionHandler, objectMapper, subscriptionHooks)
        val flux = handler.handle(operationMessage, session)
        flux.blockFirst(Duration.ofSeconds(2))
        verify(exactly = 1) { subscriptionHooks.onOperationComplete(session) }
    }

    @Test
    fun `Verify that onDisconnect is called during a terminate message`() {
        val config: GraphQLConfigurationProperties = mockk {
            every { subscriptions } returns mockk {
                every { keepAliveInterval } returns null
            }
        }
        val operationMessage = SubscriptionOperationMessage(GQL_CONNECTION_TERMINATE.type).toJson()
        val session: WebSocketSession = mockk {
            every { id } returns "123"
            every { close() } returns mockk()
        }
        val subscriptionHandler: SpringGraphQLSubscriptionHandler = mockk()
        val subscriptionHooks: ApolloSubscriptionHooks = mockk {
            every { onDisconnect(session) } returns Unit
        }
        val handler = ApolloSubscriptionProtocolHandler(config, nullContextFactory, subscriptionHandler, objectMapper, subscriptionHooks)
        val flux = handler.handle(operationMessage, session)
        flux.blockFirst(Duration.ofSeconds(2))
        verify(exactly = 1) { subscriptionHooks.onDisconnect(session) }
    }
}
