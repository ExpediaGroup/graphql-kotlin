/*
 * Copyright 2025 Expedia, Inc
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

import com.alibaba.fastjson2.annotation.JSONType
import com.expediagroup.graphql.server.types.serializers.FastJsonIncludeNonNullProperty
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonValue
import tools.jackson.core.JsonParser
import tools.jackson.databind.DeserializationContext
import tools.jackson.databind.JsonNode
import tools.jackson.databind.ValueDeserializer
import tools.jackson.databind.annotation.JsonDeserialize

/**
 * GraphQL server response abstraction that provides a convenient way to handle both single and batch responses.
 */
@JsonDeserialize(using = GraphQLServerResponseDeserializer::class)
sealed class GraphQLServerResponse

/**
 * Wrapper that holds single GraphQLResponse to an HTTP request.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonDeserialize(using = ValueDeserializer.None::class)
@JSONType(serializeFilters = [FastJsonIncludeNonNullProperty::class])
data class GraphQLResponse<T>(
    val data: T? = null,
    val errors: List<GraphQLServerError>? = null,
    val extensions: Map<Any, Any?>? = null
) : GraphQLServerResponse()

/**
 * Wrapper that holds list of GraphQLResponses that were processed together within a single HTTP request.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonDeserialize(using = ValueDeserializer.None::class)
data class GraphQLBatchResponse @JsonCreator constructor(@get:JsonValue val responses: List<GraphQLResponse<*>>) : GraphQLServerResponse()

class GraphQLServerResponseDeserializer : ValueDeserializer<GraphQLServerResponse>() {
    override fun deserialize(parser: JsonParser, ctxt: DeserializationContext): GraphQLServerResponse {
        val objectReadContext = parser.objectReadContext()
        val jsonNode = objectReadContext.readTree<JsonNode>(parser)
        return if (jsonNode.isArray) {
            objectReadContext.treeAsTokens(jsonNode).use { treeParser ->
                treeParser.nextToken()
                objectReadContext.readValue(treeParser, GraphQLBatchResponse::class.java)
            }
        } else {
            objectReadContext.treeAsTokens(jsonNode).use { treeParser ->
                treeParser.nextToken()
                objectReadContext.readValue(treeParser, GraphQLResponse::class.java)
            }
        }
    }
}
