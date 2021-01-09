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

package com.expediagroup.graphql.server.spring.subscriptions

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.reactivestreams.Subscription
import org.springframework.web.reactive.socket.WebSocketSession
import reactor.core.publisher.Mono
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ApolloSubscriptionSessionStateTest {

    @Test
    fun `saveKeepAliveSubscription saves the subscription by session id`() {
        val state = ApolloSubscriptionSessionState()
        val mockSubscription: Subscription = mockk()
        val mockSession: WebSocketSession = mockk { every { id } returns "123" }

        assertEquals(expected = 0, actual = state.activeKeepAliveSessions.size)

        state.saveKeepAliveSubscription(mockSession, mockSubscription)

        assertEquals(expected = 1, actual = state.activeKeepAliveSessions.size)
        assertEquals(expected = mockSubscription, actual = state.activeKeepAliveSessions["123"])
    }

    @Test
    fun `saveOperation does not save the subscription if operation id is null`() {
        val state = ApolloSubscriptionSessionState()
        val mockSubscription: Subscription = mockk()
        val mockSession: WebSocketSession = mockk { every { id } returns "123" }
        val mockOperationMessage: SubscriptionOperationMessage = mockk { every { id } returns null }

        assertEquals(expected = 0, actual = state.activeOperations.size)

        state.saveOperation(mockSession, mockOperationMessage, mockSubscription)

        assertEquals(expected = 0, actual = state.activeOperations.size)
    }

    @Test
    fun `saveOperation saves the subscription if operation id is valid`() {
        val state = ApolloSubscriptionSessionState()
        val mockSubscription: Subscription = mockk()
        val mockSession: WebSocketSession = mockk { every { id } returns "123" }
        val mockOperationMessage: SubscriptionOperationMessage = mockk { every { id } returns "abc" }

        assertEquals(expected = 0, actual = state.activeOperations.size)

        state.saveOperation(mockSession, mockOperationMessage, mockSubscription)

        assertEquals(expected = 1, actual = state.activeOperations.size)
        assertEquals(expected = mockSubscription, actual = state.activeOperations["123"]?.get("abc"))
    }

    @Test
    fun `saveOperation saves the subscription and does not duplicate session ids if operation id is valid`() {
        val state = ApolloSubscriptionSessionState()
        val mockSubscription: Subscription = mockk()
        val mockSession: WebSocketSession = mockk { every { id } returns "123" }
        val mockOperationMessage1: SubscriptionOperationMessage = mockk { every { id } returns "abc" }
        val mockOperationMessage2: SubscriptionOperationMessage = mockk { every { id } returns "def" }

        assertEquals(expected = 0, actual = state.activeOperations.size)

        state.saveOperation(mockSession, mockOperationMessage1, mockSubscription)

        assertEquals(expected = 1, actual = state.activeOperations.size)
        assertEquals(expected = 1, actual = state.activeOperations["123"]?.size)

        state.saveOperation(mockSession, mockOperationMessage2, mockSubscription)

        assertEquals(expected = 1, actual = state.activeOperations.size)
        assertEquals(expected = 2, actual = state.activeOperations["123"]?.size)
    }

    @Test
    fun `stopOperation does not cancel the subscription if operation id is null`() {
        val state = ApolloSubscriptionSessionState()
        val mockSubscription: Subscription = mockk()
        val mockSession: WebSocketSession = mockk { every { id } returns "123" }
        val inputOperation: SubscriptionOperationMessage = mockk { every { id } returns "abc" }

        state.saveOperation(mockSession, inputOperation, mockSubscription)

        assertEquals(expected = 1, actual = state.activeOperations.size)

        val cancelOperation: SubscriptionOperationMessage = mockk { every { id } returns null }
        state.stopOperation(mockSession, cancelOperation)

        assertEquals(expected = 1, actual = state.activeOperations.size)
        assertEquals(expected = mockSubscription, actual = state.activeOperations["123"]?.get("abc"))
        verify(exactly = 0) { mockSubscription.cancel() }
    }

    @Test
    fun `stopOperation does not cancel the subscription if operation id not match`() {
        val state = ApolloSubscriptionSessionState()
        val mockSubscription: Subscription = mockk()
        val mockSession: WebSocketSession = mockk { every { id } returns "123" }
        val inputOperation: SubscriptionOperationMessage = mockk { every { id } returns "abc" }

        state.saveOperation(mockSession, inputOperation, mockSubscription)

        assertEquals(expected = 1, actual = state.activeOperations.size)

        val cancelOperation: SubscriptionOperationMessage = mockk { every { id } returns "xyz" }
        state.stopOperation(mockSession, cancelOperation)

        assertEquals(expected = 1, actual = state.activeOperations.size)
        assertEquals(expected = mockSubscription, actual = state.activeOperations["123"]?.get("abc"))
        verify(exactly = 0) { mockSubscription.cancel() }
    }

    @Test
    fun `stopOperation clears entire operation cache if it is empty after removal`() {
        val state = ApolloSubscriptionSessionState()
        val mockSubscription: Subscription = mockk { every { cancel() } returns Unit }
        val mockSession: WebSocketSession = mockk { every { id } returns "123" }
        val inputOperation: SubscriptionOperationMessage = mockk { every { id } returns "abc" }

        state.saveOperation(mockSession, inputOperation, mockSubscription)

        assertEquals(expected = 1, actual = state.activeOperations.size)
        assertEquals(expected = 1, actual = state.activeOperations["123"]?.size)

        state.stopOperation(mockSession, inputOperation).subscribe().dispose()

        assertEquals(expected = 0, actual = state.activeOperations.size)
        assertNull(state.activeOperations["123"])
        verify(exactly = 1) { mockSubscription.cancel() }
    }

    @Test
    fun `stopOperation cancels the subscription if operation id is valid`() {
        val state = ApolloSubscriptionSessionState()
        val mockSession: WebSocketSession = mockk { every { id } returns "123" }
        val mockSubscription1: Subscription = mockk { every { cancel() } returns Unit }
        val mockSubscription2: Subscription = mockk { every { cancel() } returns Unit }
        val inputOperation1: SubscriptionOperationMessage = mockk { every { id } returns "abc" }
        val inputOperation2: SubscriptionOperationMessage = mockk { every { id } returns "def" }

        state.saveOperation(mockSession, inputOperation1, mockSubscription1)
        state.saveOperation(mockSession, inputOperation2, mockSubscription2)

        assertEquals(expected = 1, actual = state.activeOperations.size)
        assertEquals(expected = 2, actual = state.activeOperations["123"]?.size)

        state.stopOperation(mockSession, inputOperation1).subscribe().dispose()

        assertEquals(expected = 1, actual = state.activeOperations.size)
        assertEquals(expected = 1, actual = state.activeOperations["123"]?.size)
        verify(exactly = 1) { mockSubscription1.cancel() }
    }

    @Test
    fun `terminateSession cancels the keep alive subscription`() {
        val state = ApolloSubscriptionSessionState()
        val mockSubscription: Subscription = mockk { every { cancel() } returns Unit }
        val mockSession: WebSocketSession = mockk {
            every { id } returns "123"
            every { close() } returns Mono.empty()
        }

        state.saveKeepAliveSubscription(mockSession, mockSubscription)

        assertEquals(expected = 1, actual = state.activeKeepAliveSessions.size)

        state.terminateSession(mockSession)

        assertEquals(expected = 0, actual = state.activeKeepAliveSessions.size)
        verify(exactly = 1) { mockSubscription.cancel() }
        verify(exactly = 1) { mockSession.close() }
    }

    @Test
    fun `terminateSession cancels all subscriptions for the session and operations`() {
        val state = ApolloSubscriptionSessionState()
        val mockSessionSubscription: Subscription = mockk { every { cancel() } returns Unit }
        val mockOperationSubscription: Subscription = mockk { every { cancel() } returns Unit }
        val inputOperation: SubscriptionOperationMessage = mockk { every { id } returns "abc" }
        val mockSession: WebSocketSession = mockk {
            every { id } returns "123"
            every { close() } returns Mono.empty()
        }

        state.saveKeepAliveSubscription(mockSession, mockSessionSubscription)
        state.saveOperation(mockSession, inputOperation, mockOperationSubscription)

        assertEquals(expected = 1, actual = state.activeKeepAliveSessions.size)
        assertEquals(expected = 1, actual = state.activeOperations.size)

        state.terminateSession(mockSession)

        assertEquals(expected = 0, actual = state.activeKeepAliveSessions.size)
        assertEquals(expected = 0, actual = state.activeOperations.size)
        verify(exactly = 1) { mockSessionSubscription.cancel() }
        verify(exactly = 1) { mockOperationSubscription.cancel() }
        verify(exactly = 1) { mockSession.close() }
    }

    @Test
    fun `terminateSession does not cancel any subscriptions if the session id does not match`() {
        val state = ApolloSubscriptionSessionState()
        val mockSessionSubscription: Subscription = mockk { every { cancel() } returns Unit }
        val mockOperationSubscription: Subscription = mockk { every { cancel() } returns Unit }
        val inputOperation: SubscriptionOperationMessage = mockk { every { id } returns "abc" }
        val mockSession: WebSocketSession = mockk {
            every { id } returns "123"
            every { close() } returns Mono.empty()
        }

        state.saveKeepAliveSubscription(mockSession, mockSessionSubscription)
        state.saveOperation(mockSession, inputOperation, mockOperationSubscription)

        assertEquals(expected = 1, actual = state.activeKeepAliveSessions.size)
        assertEquals(expected = 1, actual = state.activeOperations.size)

        val nonMatchingSession: WebSocketSession = mockk {
            every { id } returns "xyz"
            every { close() } returns Mono.empty()
        }
        state.terminateSession(nonMatchingSession)

        assertEquals(expected = 1, actual = state.activeKeepAliveSessions.size)
        assertEquals(expected = 1, actual = state.activeOperations.size)
        verify(exactly = 0) { mockSessionSubscription.cancel() }
        verify(exactly = 0) { mockOperationSubscription.cancel() }
        verify(exactly = 0) { mockSession.close() }
        verify(exactly = 1) { nonMatchingSession.close() }
    }
}
