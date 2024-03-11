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
import com.expediagroup.graphql.server.types.serializers.GraphQLErrorPathKSerializer
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

@JsonDeserialize(using = GraphQLServerResponseDeserializer::class)
@Serializable(with = GraphQLServerResponseKSerializer::class)
sealed class GraphQLServerResponse

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonDeserialize(using = JsonDeserializer.None::class)
@Serializable
data class GraphQLResponse(
    val data: Map<String, @Serializable(with = AnyNullableKSerializer::class) Any?>? = null,
    val errors: List<GraphQLServerError>? = null,
    val extensions: Map<String, @Serializable(with = AnyNullableKSerializer::class) Any?>? = null
) : GraphQLServerResponse()

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonDeserialize(using = JsonDeserializer.None::class)
@Serializable(with = GraphQLBatchResponseKSerializer::class)
data class GraphQLBatchResponse @JsonCreator constructor(@get:JsonValue val responses: List<GraphQLResponse>) : GraphQLServerResponse() {
    constructor(vararg responses: GraphQLResponse) : this(responses.toList())
}

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Serializable
data class GraphQLServerError(
    val message: String,
    val locations: List<GraphQLSourceLocation>? = null,
    val path: List<@Serializable(with = GraphQLErrorPathKSerializer::class) Any>? = null,
    val extensions: Map<String, @Serializable(with = AnyNullableKSerializer::class) Any?>? = null
)

@JsonIgnoreProperties(ignoreUnknown = true)
@Serializable
data class GraphQLSourceLocation(
    val line: Int,
    val column: Int
)

class GraphQLServerResponseDeserializer : JsonDeserializer<GraphQLServerResponse>() {
    override fun deserialize(parser: JsonParser, ctxt: DeserializationContext): GraphQLServerResponse {
        val codec = parser.codec
        val jsonNode = codec.readTree<JsonNode>(parser)
        return if (jsonNode.isArray) {
            codec.treeToValue(jsonNode, GraphQLBatchResponse::class.java)
        } else {
            codec.treeToValue(jsonNode, GraphQLResponse::class.java)
        }
    }
}

object GraphQLServerResponseKSerializer : KSerializer<GraphQLServerResponse> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("GraphQLServerResponse")

    override fun deserialize(decoder: Decoder): GraphQLServerResponse {
        val jsonDecoder = decoder as JsonDecoder
        return when (val jsonElement = jsonDecoder.decodeJsonElement()) {
            is JsonObject -> Json.decodeFromJsonElement<GraphQLResponse>(jsonElement)
            is JsonArray -> GraphQLBatchResponse(Json.decodeFromJsonElement<List<GraphQLResponse>>(jsonElement))
            else -> throw SerializationException("Unknown JSON element found")
        }
    }

    override fun serialize(
        encoder: Encoder,
        value: GraphQLServerResponse,
    ) {
        when (value) {
            is GraphQLResponse -> encoder.encodeSerializableValue(GraphQLResponse.serializer(), value)
            is GraphQLBatchResponse -> encoder.encodeSerializableValue(ListSerializer(GraphQLResponse.serializer()), value.responses)
        }
    }
}

object GraphQLBatchResponseKSerializer : KSerializer<GraphQLBatchResponse> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("GraphQLBatchResponse")

    override fun deserialize(decoder: Decoder): GraphQLBatchResponse =
        GraphQLBatchResponse(decoder.decodeSerializableValue(ListSerializer(GraphQLResponse.serializer())))

    override fun serialize(encoder: Encoder, value: GraphQLBatchResponse) {
        encoder.encodeSerializableValue(ListSerializer(GraphQLResponse.serializer()), value.responses)
    }
}
