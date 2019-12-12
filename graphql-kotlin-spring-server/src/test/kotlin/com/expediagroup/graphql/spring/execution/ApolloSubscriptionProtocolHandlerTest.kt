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
import com.expediagroup.graphql.spring.exception.SimpleKotlinGraphQLError
import com.expediagroup.graphql.spring.model.GraphQLRequest
import com.expediagroup.graphql.spring.model.GraphQLResponse
import com.expediagroup.graphql.spring.model.SubscriptionOperationMessage
import com.expediagroup.graphql.spring.model.SubscriptionOperationMessage.ClientMessages.GQL_CONNECTION_INIT
import com.expediagroup.graphql.spring.model.SubscriptionOperationMessage.ClientMessages.GQL_CONNECTION_TERMINATE
import com.expediagroup.graphql.spring.model.SubscriptionOperationMessage.ClientMessages.GQL_START
import com.expediagroup.graphql.spring.model.SubscriptionOperationMessage.ClientMessages.GQL_STOP
import com.expediagroup.graphql.spring.model.SubscriptionOperationMessage.ServerMessages.GQL_CONNECTION_ACK
import com.expediagroup.graphql.spring.model.SubscriptionOperationMessage.ServerMessages.GQL_CONNECTION_ERROR
import com.expediagroup.graphql.spring.model.SubscriptionOperationMessage.ServerMessages.GQL_DATA
import com.expediagroup.graphql.spring.model.SubscriptionOperationMessage.ServerMessages.GQL_ERROR
import com.fasterxml.jackson.module.kotlin.convertValue
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.springframework.web.reactive.socket.WebSocketSession
import reactor.core.publisher.Flux
import reactor.test.StepVerifier
import java.time.Duration
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class ApolloSubscriptionProtocolHandlerTest {

    private val objectMapper = jacksonObjectMapper()

    private fun SubscriptionOperationMessage.toJson() = objectMapper.writeValueAsString(this)

    @Test
    fun `Return GQL_CONNECTION_ERROR when payload is not a SubscriptionOperationMessage`() {
        val config: GraphQLConfigurationProperties = mockk()
        val session: WebSocketSession = mockk()
        val subscriptionHandler: SubscriptionHandler = mockk()

        val handler = ApolloSubscriptionProtocolHandler(config, subscriptionHandler, objectMapper)
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
        val subscriptionHandler: SubscriptionHandler = mockk()

        val handler = ApolloSubscriptionProtocolHandler(config, subscriptionHandler, objectMapper)
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
        val operationMessage = SubscriptionOperationMessage(GQL_CONNECTION_INIT.type).toJson()
        val session: WebSocketSession = mockk {
            every { id } returns "123"
        }
        val subscriptionHandler: SubscriptionHandler = mockk()

        val handler = ApolloSubscriptionProtocolHandler(config, subscriptionHandler, objectMapper)
        val flux = handler.handle(operationMessage, session)

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
        val operationMessage = SubscriptionOperationMessage(GQL_CONNECTION_INIT.type).toJson()
        val session: WebSocketSession = mockk {
            every { id } returns "123"
        }
        val subscriptionHandler: SubscriptionHandler = mockk()

        val handler = ApolloSubscriptionProtocolHandler(config, subscriptionHandler, objectMapper)
        val flux = handler.handle(operationMessage, session)

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
        val subscriptionHandler: SubscriptionHandler = mockk()

        val handler = ApolloSubscriptionProtocolHandler(config, subscriptionHandler, objectMapper)

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
        val subscriptionHandler: SubscriptionHandler = mockk()

        val handler = ApolloSubscriptionProtocolHandler(config, subscriptionHandler, objectMapper)
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
        val subscriptionHandler: SubscriptionHandler = mockk()

        val handler = ApolloSubscriptionProtocolHandler(config, subscriptionHandler, objectMapper)
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
        }
        val subscriptionHandler: SubscriptionHandler = mockk()

        val handler = ApolloSubscriptionProtocolHandler(config, subscriptionHandler, objectMapper)
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
        val subscriptionHandler: SubscriptionHandler = mockk()
        val handler = ApolloSubscriptionProtocolHandler(config, subscriptionHandler, objectMapper)

        val initMessage = SubscriptionOperationMessage(GQL_CONNECTION_INIT.type).toJson()
        val stopMessage = SubscriptionOperationMessage(GQL_STOP.type).toJson()
        val initFlux = handler.handle(initMessage, session)
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
        val session: WebSocketSession = mockk()
        val subscriptionHandler: SubscriptionHandler = mockk()

        val handler = ApolloSubscriptionProtocolHandler(config, subscriptionHandler, objectMapper)
        val flux = handler.handle(operationMessage, session)

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
        val subscriptionHandler: SubscriptionHandler = mockk()

        val handler = ApolloSubscriptionProtocolHandler(config, subscriptionHandler, objectMapper)
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
        val subscriptionHandler: SubscriptionHandler = mockk()

        val handler = ApolloSubscriptionProtocolHandler(config, subscriptionHandler, objectMapper)
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
        val subscriptionHandler: SubscriptionHandler = mockk {
            every { executeSubscription(eq(graphQLRequest)) } returns Flux.just(GraphQLResponse("myData"))
        }

        val handler = ApolloSubscriptionProtocolHandler(config, subscriptionHandler, objectMapper)
        val flux = handler.handle(operationMessage, session)

        val message = flux.blockFirst(Duration.ofSeconds(2))
        assertNotNull(message)
        assertEquals(expected = GQL_DATA.type, actual = message.type)
        assertEquals(expected = "abc", actual = message.id)
        val payload = message.payload
        assertNotNull(payload)
        val graphQLResponse: GraphQLResponse = objectMapper.convertValue(payload)
        assertEquals(expected = "myData", actual = graphQLResponse.data)

        assertEquals(expected = 2, actual = flux.count().block())
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
        val errors = listOf(SimpleKotlinGraphQLError(Throwable("My GraphQL Error")))
        val subscriptionHandler: SubscriptionHandler = mockk {
            every { executeSubscription(eq(graphQLRequest)) } returns Flux.just(GraphQLResponse(errors = errors))
        }

        val handler = ApolloSubscriptionProtocolHandler(config, subscriptionHandler, objectMapper)
        val flux = handler.handle(operationMessage, session)

        assertEquals(expected = 2, actual = flux.count().block())
        val message = flux.blockFirst(Duration.ofSeconds(2))
        assertNotNull(message)
        assertEquals(expected = GQL_ERROR.type, actual = message.type)
        assertEquals(expected = "abc", actual = message.id)
        val response = message.payload as? GraphQLResponse
        assertNotNull(response)
        assertTrue(response.errors?.isNotEmpty() == true)

        verify(exactly = 0) { session.close() }
    }
}
