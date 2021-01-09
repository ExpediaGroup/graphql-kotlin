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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

/**
 * The `graphql-ws` protocol from Apollo Client has some special text messages to signal events.
 * Along with the HTTP WebSocket event handling we need to have some extra logic
 *
 * https://github.com/apollographql/subscriptions-transport-ws/blob/master/PROTOCOL.md
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class SubscriptionOperationMessage(
    val type: String,
    val id: String? = null,
    val payload: Any? = null
) {
    enum class ClientMessages(val type: String) {
        GQL_CONNECTION_INIT("connection_init"),
        GQL_START("start"),
        GQL_STOP("stop"),
        GQL_CONNECTION_TERMINATE("connection_terminate")
    }

    enum class ServerMessages(val type: String) {
        GQL_CONNECTION_ACK("connection_ack"),
        GQL_CONNECTION_ERROR("connection_error"),
        GQL_DATA("data"),
        GQL_ERROR("error"),
        GQL_COMPLETE("complete"),
        GQL_CONNECTION_KEEP_ALIVE("ka")
    }
}
