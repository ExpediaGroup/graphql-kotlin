package com.expediagroup.graphql.server.types.serializers

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.JsonPrimitive

class GraphQLErrorPathKSerializer : KSerializer<Any> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("GraphQLErrorPath")

    override fun serialize(encoder: Encoder, value: Any) {
        val jsonEncoder = encoder as JsonEncoder
        val jsonElement = when (value) {
            is Int -> JsonPrimitive(value)
            is String -> JsonPrimitive(value)
            else -> {
                // should never be the case
                JsonPrimitive(value.toString())
            }
        }
        jsonEncoder.encodeJsonElement(jsonElement)
    }

    override fun deserialize(decoder: Decoder): Any {
        val jsonDecoder = decoder as JsonDecoder
        val element = jsonDecoder.decodeJsonElement() as JsonPrimitive
        return if (!element.isString) {
            element.content.toIntOrNull() ?: element.content
        } else {
            element.content
        }
    }
}
