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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.annotation.JsonDeserialize

const val GRAPHQL_WS_CONNECTION_INIT = "connection_init"
const val GRAPHQL_WS_CONNECTION_ACK = "connection_ack"
const val GRAPHQL_WS_PING = "ping"
const val GRAPHQL_WS_PONG = "pong"
const val GRAPHQL_WS_SUBSCRIBE = "subscribe"
const val GRAPHQL_WS_NEXT = "next"
const val GRAPHQL_WS_ERROR = "error"
const val GRAPHQL_WS_COMPLETE = "complete"
const val GRAPHQL_WS_INVALID = "invalid"

/**
 * *graphql-transport-ws* subscription protocol message
 *
 * @see <a href=https://github.com/enisdenjo/graphql-ws/blob/master/PROTOCOL.md>graphql-transport-ws protocol</a>
 */
@JsonDeserialize(using = GraphQLSubscriptionMessageDeserializer::class)
sealed class GraphQLSubscriptionMessage {
    abstract val type: String
}

/**
 * Indicates that the client wants to establish a connection within the existing socket.
 *
 * Connection init process may fail with the following status codes
 * * 4403: Forbidden - if server rejects the connection (e.g. due to auth)
 * * 4408: Connection initialisation timeout - if *connection-init* is not received within specified connection timeout
 * * 4429: Too many initialisation requests - if server receives more than one *connection-init* requests
 *
 * @param payload optional field to provide additional details about the connection
 * @see <a href="https://github.com/enisdenjo/graphql-ws/blob/master/PROTOCOL.md#connectioninit">connection-init</a>
 */
@JsonDeserialize(using = JsonDeserializer.None::class)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
data class SubscriptionMessageConnectionInit(val payload: Any? = null) : GraphQLSubscriptionMessage() {
    override val type: String = GRAPHQL_WS_CONNECTION_INIT
}

/**
 * Successful server response to the <em>connection-init</em> request.
 *
 * @param payload optional field to provide additional details about the connection
 * @see <a href="https://github.com/enisdenjo/graphql-ws/blob/master/PROTOCOL.md#connectionack">connection-ack</a>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonDeserialize(using = JsonDeserializer.None::class)
@JsonIgnoreProperties(ignoreUnknown = true)
data class SubscriptionMessageConnectionAck(val payload: Any? = null) : GraphQLSubscriptionMessage() {
    override val type: String = GRAPHQL_WS_CONNECTION_ACK
}

/**
 * Client diagnostic message that server must respond with *pong* message.
 *
 * @param payload optional field to provide additional details about the connection
 * @see <a href="https://github.com/enisdenjo/graphql-ws/blob/master/PROTOCOL.md#ping">ping</a>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonDeserialize(using = JsonDeserializer.None::class)
@JsonIgnoreProperties(ignoreUnknown = true)
data class SubscriptionMessagePing(val payload: Any? = null) : GraphQLSubscriptionMessage() {
    override val type: String = GRAPHQL_WS_PING
}

/**
 * Server diagnostic message response to the client *ping* message.
 *
 * @param payload optional field to provide additional details about the connection
 * @see <a href="https://github.com/enisdenjo/graphql-ws/blob/master/PROTOCOL.md#pong">pong</a>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonDeserialize(using = JsonDeserializer.None::class)
@JsonIgnoreProperties(ignoreUnknown = true)
data class SubscriptionMessagePong(val payload: Any? = null) : GraphQLSubscriptionMessage() {
    override val type: String = GRAPHQL_WS_PONG
}

/**
 * Requests to execute subscription operation provided in the payload field.
 *
 * If there is already an active subscription for specified ID then server will close the underlying socket
 * and respond with status code 4409: Subscriber for <id> already exists.
 *
 * @param id unique subscription id
 * @param payload GraphQL subscription operation request
 * @see <a href="https://github.com/enisdenjo/graphql-ws/blob/master/PROTOCOL.md#subscribe">subscribe</a>
 */
@JsonDeserialize(using = JsonDeserializer.None::class)
@JsonIgnoreProperties(ignoreUnknown = true)
data class SubscriptionMessageSubscribe(val id: String, val payload: GraphQLRequest) : GraphQLSubscriptionMessage() {
    override val type: String = GRAPHQL_WS_SUBSCRIBE
}

/**
 * Successful subscription operation message response. Will be followed by *complete* message after successful
 * stream completion.
 *
 * @param id unique subscription id
 * @param payload GraphQL subscription operation response
 * @see <a href="https://github.com/enisdenjo/graphql-ws/blob/master/PROTOCOL.md#next">next</a>
 */
@JsonDeserialize(using = JsonDeserializer.None::class)
@JsonIgnoreProperties(ignoreUnknown = true)
data class SubscriptionMessageNext(val id: String, val payload: GraphQLResponse<*>) : GraphQLSubscriptionMessage() {
    override val type: String = GRAPHQL_WS_NEXT
}

/**
 * Failed subscription operation message response. Terminates the operation and no further message will be sent.
 *
 * @param id unique subscription id
 * @param payload GraphQL errors encountered during subscription execution
 * @see <a href="https://github.com/enisdenjo/graphql-ws/blob/master/PROTOCOL.md#error">error</a>
 */
@JsonDeserialize(using = JsonDeserializer.None::class)
@JsonIgnoreProperties(ignoreUnknown = true)
data class SubscriptionMessageError(val id: String, val payload: List<GraphQLServerError>) : GraphQLSubscriptionMessage() {
    override val type: String = GRAPHQL_WS_ERROR
}

/**
 * Message indicating that client/server completed the subscription operation.
 *
 * @param id unique subscription id
 * @see <a href="https://github.com/enisdenjo/graphql-ws/blob/master/PROTOCOL.md#complete">complete</a>
 */
@JsonDeserialize(using = JsonDeserializer.None::class)
@JsonIgnoreProperties(ignoreUnknown = true)
data class SubscriptionMessageComplete(val id: String) : GraphQLSubscriptionMessage() {
    override val type: String = GRAPHQL_WS_COMPLETE
}

/**
 * Invalid unknown message that results in a close status 4400: Invalid request.
 *
 * @param id unique subscription id
 * @see <a href="https://github.com/enisdenjo/graphql-ws/blob/master/PROTOCOL.md#invalid">invalid</a>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonDeserialize(using = JsonDeserializer.None::class)
data class SubscriptionMessageInvalid(val id: String? = null, val payload: Any? = null) : GraphQLSubscriptionMessage() {
    override val type: String = GRAPHQL_WS_INVALID
}

class GraphQLSubscriptionMessageDeserializer : JsonDeserializer<GraphQLSubscriptionMessage>() {
    override fun deserialize(parser: JsonParser, ctxt: DeserializationContext): GraphQLSubscriptionMessage {
        val codec = parser.codec
        val jsonNode = codec.readTree<JsonNode>(parser)
        return when (jsonNode.get("type")?.textValue()) {
            GRAPHQL_WS_CONNECTION_INIT -> codec.treeToValue(jsonNode, SubscriptionMessageConnectionInit::class.java)
            GRAPHQL_WS_CONNECTION_ACK -> codec.treeToValue(jsonNode, SubscriptionMessageConnectionAck::class.java)
            GRAPHQL_WS_PING -> codec.treeToValue(jsonNode, SubscriptionMessagePing::class.java)
            GRAPHQL_WS_PONG -> codec.treeToValue(jsonNode, SubscriptionMessagePong::class.java)
            GRAPHQL_WS_SUBSCRIBE -> codec.treeToValue(jsonNode, SubscriptionMessageSubscribe::class.java)
            GRAPHQL_WS_NEXT -> codec.treeToValue(jsonNode, SubscriptionMessageNext::class.java)
            GRAPHQL_WS_ERROR -> codec.treeToValue(jsonNode, SubscriptionMessageError::class.java)
            GRAPHQL_WS_COMPLETE -> codec.treeToValue(jsonNode, SubscriptionMessageComplete::class.java)
            else -> codec.treeToValue(jsonNode, SubscriptionMessageInvalid::class.java)
        }
    }
}
