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

package com.expediagroup.graphql.server.ktor.subscriptions.legacy

object LegacyMessageTypes {
    const val GQL_CONNECTION_INIT = "connection_init"
    const val GQL_START = "start"
    const val GQL_STOP = "stop"
    const val GQL_CONNECTION_TERMINATE = "connection_terminate"

    const val GQL_CONNECTION_ACK = "connection_ack"
    const val GQL_CONNECTION_ERROR = "connection_error"
    const val GQL_DATA = "data"
    const val GQL_ERROR = "error"
    const val GQL_COMPLETE = "complete"
    const val GQL_CONNECTION_KEEP_ALIVE = "ka"
}
