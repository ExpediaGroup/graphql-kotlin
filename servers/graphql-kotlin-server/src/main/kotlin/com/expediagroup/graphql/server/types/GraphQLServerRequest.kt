/*
 * Copyright 2022 Expedia, Inc
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

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonValue
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.annotation.JsonDeserialize

/**
 * GraphQL server request abstraction that provides a convenient way to handle both single and batch requests.
 */
@JsonDeserialize(using = GraphQLServerRequestDeserializer::class)
sealed class GraphQLServerRequest

/**
 * Wrapper that holds single GraphQLRequest to be processed within an HTTP request.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonDeserialize(using = JsonDeserializer.None::class)
data class GraphQLRequest(
    val query: String = "",
    val operationName: String? = null,
    val variables: Map<String, Any?>? = null,
    val extensions: Map<String, Any?>? = null
) : GraphQLServerRequest()

/**
 * Wrapper that holds list of GraphQLRequests to be processed together within a single HTTP request.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonDeserialize(using = JsonDeserializer.None::class)
data class GraphQLBatchRequest @JsonCreator constructor(@get:JsonValue val requests: List<GraphQLRequest>) : GraphQLServerRequest() {
    constructor(vararg requests: GraphQLRequest) : this(requests.toList())
}

class GraphQLServerRequestDeserializer : JsonDeserializer<GraphQLServerRequest>() {
    override fun deserialize(parser: JsonParser, ctxt: DeserializationContext): GraphQLServerRequest {
        val codec = parser.codec
        val jsonNode = codec.readTree<JsonNode>(parser)
        return if (jsonNode.isArray) {
            codec.treeToValue(jsonNode, GraphQLBatchRequest::class.java)
        } else {
            codec.treeToValue(jsonNode, GraphQLRequest::class.java)
        }
    }
}
