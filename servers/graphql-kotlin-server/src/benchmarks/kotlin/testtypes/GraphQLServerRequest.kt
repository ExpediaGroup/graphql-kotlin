/*
 * Copyright 2024 Expedia, Inc
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

package com.expediagroup.graphql.server.testtypes

import com.expediagroup.graphql.server.types.serializers.AnyNullableKSerializer
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonValue
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromJsonElement

@JsonDeserialize(using = GraphQLServerRequestDeserializer::class)
@Serializable(with = GraphQLServerRequestKSerializer::class)
sealed class GraphQLServerRequest

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonDeserialize(using = JsonDeserializer.None::class)
@Serializable
data class GraphQLRequest(
    val query: String = "",
    val operationName: String? = null,
    val variables: Map<String, @Serializable(with = AnyNullableKSerializer::class) Any?>? = null,
    val extensions: Map<String, @Serializable(with = AnyNullableKSerializer::class) Any?>? = null
) : GraphQLServerRequest()

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonDeserialize(using = JsonDeserializer.None::class)
@Serializable(with = GraphQLBatchRequestKSerializer::class)
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

object GraphQLServerRequestKSerializer : KSerializer<GraphQLServerRequest> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("GraphQLServerRequest")

    override fun deserialize(decoder: Decoder): GraphQLServerRequest {
        val jsonDecoder = decoder as JsonDecoder
        return when (val jsonElement = jsonDecoder.decodeJsonElement()) {
            is JsonObject -> {
                Json.decodeFromJsonElement<GraphQLRequest>(jsonElement)
            }
            is JsonArray -> {
                GraphQLBatchRequest(Json.decodeFromJsonElement<List<GraphQLRequest>>(jsonElement))
            }
            else -> throw SerializationException("Unknown JSON element found")
        }
    }

    override fun serialize(
        encoder: Encoder,
        value: GraphQLServerRequest,
    ) {
        when (value) {
            is GraphQLRequest -> {
                encoder.encodeSerializableValue(GraphQLRequest.serializer(), value)
            }
            is GraphQLBatchRequest -> {
                encoder.encodeSerializableValue(ListSerializer(GraphQLRequest.serializer()), value.requests)
            }
        }
    }
}

object GraphQLBatchRequestKSerializer : KSerializer<GraphQLBatchRequest> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("GraphQLBatchRequest")

    override fun deserialize(decoder: Decoder): GraphQLBatchRequest =
        GraphQLBatchRequest(decoder.decodeSerializableValue(ListSerializer(GraphQLRequest.serializer())))

    override fun serialize(encoder: Encoder, value: GraphQLBatchRequest) {
        encoder.encodeSerializableValue(ListSerializer(GraphQLRequest.serializer()), value.requests)
    }
}
