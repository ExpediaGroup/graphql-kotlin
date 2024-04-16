package com.expediagroup.graphql.server.types.serializers

import com.expediagroup.graphql.generator.scalars.ID
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

object AnyNullableKSerializer : KSerializer<Any?> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("AnyNullable")

    override fun serialize(
        encoder: Encoder,
        value: Any?,
    ) {
        val jsonEncoder = encoder as JsonEncoder
        jsonEncoder.encodeJsonElement(serializeAny(value))
    }

    private fun serializeAny(value: Any?): JsonElement =
        when (value) {
            null -> JsonNull
            is Map<*, *> -> {
                val mapContents =
                    value.mapNotNull { (key, value) ->
                        key.toString() to serializeAny(value)
                    }.toMap()
                JsonObject(mapContents)
            }
            is List<*> -> {
                val arrayContents = value.mapNotNull { listEntry -> serializeAny(listEntry) }
                JsonArray(arrayContents)
            }
            is Number -> JsonPrimitive(value)
            is Boolean -> JsonPrimitive(value)
            is String -> JsonPrimitive(value)
            is ID -> JsonPrimitive(value.value)
            else -> JsonNull
        }

    override fun deserialize(decoder: Decoder): Any? {
        val jsonDecoder = decoder as JsonDecoder
        val element = jsonDecoder.decodeJsonElement()
        return deserializeJsonElement(element)
    }

    private fun deserializeJsonElement(element: JsonElement): Any? =
        when (element) {
            is JsonNull -> null
            is JsonObject -> {
                element.mapValues { deserializeJsonElement(it.value) }
            }
            is JsonArray -> {
                element.map { deserializeJsonElement(it) }
            }
            is JsonPrimitive ->
                when {
                    element.isString -> element.content
                    element.content == "true" -> true
                    element.content == "false" -> false
                    else -> {
                        element.content.toIntOrNull() ?: element.content.toLongOrNull() ?: element.content.toDoubleOrNull() ?: element.content
                    }
                }
        }
}
