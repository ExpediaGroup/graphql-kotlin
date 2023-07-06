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

package com.expediagroup.graphql.server.spring.subscriptions

import com.expediagroup.graphql.server.execution.subscription.GRAPHQL_WS_PROTOCOL
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.mockk.mockk
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class SubscriptionWebSocketHandlerTest {

    @Test
    fun `verify Apollo subscriptions handler supports graphql-ws subprotocol`() {
        val handler = ApolloSubscriptionWebSocketHandler(mockk(), mockk())
        assertEquals(expected = listOf(APOLLO_GRAPHQL_WS_PROTOCOL), actual = handler.subProtocols)
    }

    @Test
    fun `verify default subscription handler supports graphql-transport-ws subprotocol`() {
        val handler = SubscriptionWebSocketHandler(mockk(), mockk(), mockk(), mockk(), 1_000, jacksonObjectMapper())
        assertEquals(expected = listOf(GRAPHQL_WS_PROTOCOL), actual = handler.subProtocols)
    }
}
