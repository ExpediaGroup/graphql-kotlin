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

import com.expediagroup.graphql.spring.exception.SimpleKotlinGraphQLError
import com.expediagroup.graphql.spring.model.GraphQLRequest
import com.expediagroup.graphql.spring.model.GraphQLResponse
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
import com.fasterxml.jackson.module.kotlin.convertValue
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.springframework.web.reactive.socket.WebSocketSession
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ApolloSubscriptionProtocolHandlerTest {

    private val objectMapper = jacksonObjectMapper().registerKotlinModule()

    @Test
    fun `Return GQL_CONNECTION_ERROR when payload is not a SubscriptionOperationMessage`() {
        val session: WebSocketSession = mockk()
        val subscriptionHandler: SubscriptionHandler = mockk()

        val handler = ApolloSubscriptionProtocolHandler(subscriptionHandler, objectMapper)
        val flux = handler.handle("", session)

        val message = flux.blockFirst()
        assertNotNull(message)
        assertEquals(expected = GQL_CONNECTION_ERROR.type, actual = message.type)
    }

    @Test
    fun `Return GQL_CONNECTION_ERROR when SubscriptionOperationMessage type is not valid`() {
        val operationMessage = SubscriptionOperationMessage("", id = "abc")
        val session: WebSocketSession = mockk()
        val subscriptionHandler: SubscriptionHandler = mockk()

        val handler = ApolloSubscriptionProtocolHandler(subscriptionHandler, objectMapper)
        val flux = handler.handle(objectMapper.writeValueAsString(operationMessage), session)

        val message = flux.blockFirst()
        assertNotNull(message)
        assertEquals(expected = GQL_CONNECTION_ERROR.type, actual = message.type)
        assertEquals(expected = "abc", actual = message.id)
    }

    @Test
    fun `Return GQL_CONNECTION_ACK when type is GQL_CONNECTION_INIT`() {
        val operationMessage = SubscriptionOperationMessage(GQL_CONNECTION_INIT.type)
        val session: WebSocketSession = mockk()
        val subscriptionHandler: SubscriptionHandler = mockk()

        val handler = ApolloSubscriptionProtocolHandler(subscriptionHandler, objectMapper)
        val flux = handler.handle(objectMapper.writeValueAsString(operationMessage), session)

        val message = flux.blockFirst()
        assertNotNull(message)
        assertEquals(expected = GQL_CONNECTION_ACK.type, actual = message.type)
    }

    @Test
    fun `Close session when type is GQL_CONNECTION_TERMINATE`() {
        val operationMessage = SubscriptionOperationMessage(GQL_CONNECTION_TERMINATE.type)
        val session: WebSocketSession = mockk {
            every { close() } returns mockk()
        }
        val subscriptionHandler: SubscriptionHandler = mockk()

        val handler = ApolloSubscriptionProtocolHandler(subscriptionHandler, objectMapper)
        val flux = handler.handle(objectMapper.writeValueAsString(operationMessage), session)

        assertNull(flux.blockFirst())
        verify(exactly = 1) { session.close() }
    }

    @Test
    fun `Close session when type is GQL_STOP`() {
        val operationMessage = SubscriptionOperationMessage(GQL_STOP.type)
        val session: WebSocketSession = mockk {
            every { close() } returns mockk()
        }
        val subscriptionHandler: SubscriptionHandler = mockk()

        val handler = ApolloSubscriptionProtocolHandler(subscriptionHandler, objectMapper)
        val flux = handler.handle(objectMapper.writeValueAsString(operationMessage), session)

        assertNull(flux.blockFirst())
        verify(exactly = 1) { session.close() }
    }

    @Test
    fun `Return GQL_CONNECTION_ERROR is returned when type is GQL_START but payload is null`() {
        val operationMessage = SubscriptionOperationMessage(type = GQL_START.type, id = "abc", payload = null)
        val session: WebSocketSession = mockk()
        val subscriptionHandler: SubscriptionHandler = mockk()

        val handler = ApolloSubscriptionProtocolHandler(subscriptionHandler, objectMapper)
        val flux = handler.handle(objectMapper.writeValueAsString(operationMessage), session)

        val message = flux.blockFirst()
        assertNotNull(message)
        assertEquals(expected = GQL_CONNECTION_ERROR.type, actual = message.type)
        assertEquals(expected = "abc", actual = message.id)
    }

    @Test
    fun `Return GQL_CONNECTION_ERROR when type is GQL_START but payload is is valid GraphQLRequest`() {
        val operationMessage = SubscriptionOperationMessage(type = GQL_START.type, id = "abc", payload = "")
        val session: WebSocketSession = mockk()
        val subscriptionHandler: SubscriptionHandler = mockk()

        val handler = ApolloSubscriptionProtocolHandler(subscriptionHandler, objectMapper)
        val flux = handler.handle(objectMapper.writeValueAsString(operationMessage), session)

        val message = flux.blockFirst()
        assertNotNull(message)
        assertEquals(expected = GQL_CONNECTION_ERROR.type, actual = message.type)
        assertEquals(expected = "abc", actual = message.id)
    }

    @Test
    fun `Return GQL_DATA when type is GQL_START with valid GraphQLRequest`() {
        val graphQLRequest = GraphQLRequest("{ message }")
        val operationMessage = SubscriptionOperationMessage(type = GQL_START.type, id = "abc", payload = graphQLRequest)
        val session: WebSocketSession = mockk {
            every { textMessage(any()) } returns mockk()
            every { send(any()) } returns Mono.empty()
            every { close() } returns mockk()
            every { id } returns "123"
        }
        val subscriptionHandler: SubscriptionHandler = mockk {
            every { executeSubscription(eq(graphQLRequest)) } returns Flux.just(GraphQLResponse("myData"))
        }

        val handler = ApolloSubscriptionProtocolHandler(subscriptionHandler, objectMapper)
        val flux = handler.handle(objectMapper.writeValueAsString(operationMessage), session)

        val message = flux.blockFirst()
        assertNotNull(message)
        assertEquals(expected = GQL_DATA.type, actual = message.type)
        assertEquals(expected = "abc", actual = message.id)
        val payload = message.payload
        assertNotNull(payload)
        val graphQLResponse: GraphQLResponse = objectMapper.convertValue(payload)
        assertEquals(expected = "myData", actual = graphQLResponse.data)

        val completeMessage = SubscriptionOperationMessage(type = GQL_COMPLETE.type, id = "abc")
        verify(exactly = 1) { session.textMessage(eq(objectMapper.writeValueAsString(completeMessage))) }
        verify(exactly = 1) { session.send(any()) }
        verify(exactly = 1) { session.close() }
    }

    @Test
    fun `Return GQL_ERROR when type is GQL_START with valid GraphQLRequest but response has errors`() {
        val graphQLRequest = GraphQLRequest("{ message }")
        val operationMessage = SubscriptionOperationMessage(type = GQL_START.type, id = "abc", payload = graphQLRequest)
        val session: WebSocketSession = mockk {
            every { textMessage(any()) } returns mockk()
            every { send(any()) } returns Mono.empty()
            every { close() } returns mockk()
            every { id } returns "123"
        }
        val errors = listOf(SimpleKotlinGraphQLError(Throwable("My GraphQL Error")))
        val subscriptionHandler: SubscriptionHandler = mockk {
            every { executeSubscription(eq(graphQLRequest)) } returns Flux.just(GraphQLResponse(errors = errors))
        }

        val handler = ApolloSubscriptionProtocolHandler(subscriptionHandler, objectMapper)
        val flux = handler.handle(objectMapper.writeValueAsString(operationMessage), session)

        val message = flux.blockFirst()
        assertNotNull(message)
        assertEquals(expected = GQL_ERROR.type, actual = message.type)
        assertEquals(expected = "abc", actual = message.id)
        val payload = message.payload
        assertNotNull(payload)
        val graphQLResponse: GraphQLResponse = objectMapper.convertValue(payload)
        assertTrue(graphQLResponse.errors?.isNotEmpty() == true)

        val completeMessage = SubscriptionOperationMessage(type = GQL_COMPLETE.type, id = "abc")
        verify(exactly = 1) { session.textMessage(eq(objectMapper.writeValueAsString(completeMessage))) }
        verify(exactly = 1) { session.send(any()) }
        verify(exactly = 1) { session.close() }
    }
}
