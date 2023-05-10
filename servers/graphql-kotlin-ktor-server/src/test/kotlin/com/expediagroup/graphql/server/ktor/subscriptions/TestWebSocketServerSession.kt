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

import io.ktor.server.application.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import io.mockk.mockk
import kotlinx.coroutines.channels.Channel
import kotlin.coroutines.CoroutineContext

class TestWebSocketServerSession(
    override val coroutineContext: CoroutineContext,
    override val incoming: Channel<Frame> = Channel(capacity = Channel.UNLIMITED),
    override val outgoing: Channel<Frame> = Channel(capacity = Channel.UNLIMITED),
) : WebSocketServerSession {

    override val call: ApplicationCall = mockk()
    override val extensions: List<WebSocketExtension<*>> = mockk()
    override var maxFrameSize: Long = 0
    override var masking: Boolean = false
    override suspend fun flush() {}

    @Deprecated("Use cancel() instead.", replaceWith = ReplaceWith("cancel()", "kotlinx.coroutines.cancel"))
    override fun terminate() {
    }

    fun closeChannels() {
        incoming.close()
        outgoing.close()
    }
}
