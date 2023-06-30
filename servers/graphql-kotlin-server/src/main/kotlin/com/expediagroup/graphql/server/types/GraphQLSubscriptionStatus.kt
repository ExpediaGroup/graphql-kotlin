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

package com.expediagroup.graphql.server.types

/**
 * WebSocket status codes required by GraphQL WS subscription protocol.
 *
 * @see <a href="https://www.rfc-editor.org/rfc/rfc6455.html#section-7.4.2">WebSocket RFC</a>
 */
data class GraphQLSubscriptionStatus(val code: Int, val reason: String) {
    companion object {
        val INVALID_MESSAGE = GraphQLSubscriptionStatus(4400, "Invalid operation")
        val UNAUTHORIZED = GraphQLSubscriptionStatus(4401, "Unauthorized")
        val FORBIDDEN = GraphQLSubscriptionStatus(4403, "Forbidden")
        val CONNECTION_INIT_TIMEOUT = GraphQLSubscriptionStatus(4408, "Connection initialisation timeout")
        val TOO_MANY_REQUESTS = GraphQLSubscriptionStatus(4429, "Too many initialisation requests")

        fun conflict(id: String) = GraphQLSubscriptionStatus(4409, "Subscriber for $id already exists")
    }
}
