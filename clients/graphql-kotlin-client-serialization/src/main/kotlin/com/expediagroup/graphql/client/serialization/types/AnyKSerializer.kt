package com.expediagroup.graphql.client.serialization.types

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
import kotlin.reflect.full.memberProperties

object AnyKSerializer : KSerializer<Any> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("Any")

    override fun serialize(encoder: Encoder, value: Any) {
        val jsonEncoder = encoder as JsonEncoder
        val jsonElement = serializeAny(value)
        jsonEncoder.encodeJsonElement(jsonElement)
    }

    private fun serializeAny(value: Any?): JsonElement = when (value) {
        null -> JsonNull
        is Map<*, *> -> {
            val mapContents = value.entries.associate { mapEntry ->
                mapEntry.key.toString() to serializeAny(mapEntry.value)
            }
            JsonObject(mapContents)
        }
        is List<*> -> {
            val arrayContents = value.map { listEntry -> serializeAny(listEntry) }
            JsonArray(arrayContents)
        }
        is Number -> JsonPrimitive(value)
        is Boolean -> JsonPrimitive(value)
        is String -> JsonPrimitive(value)
        else -> {
            val contents = value::class.memberProperties.associate { property ->
                property.name to serializeAny(property.getter.call(value))
            }
            JsonObject(contents)
        }
    }

    override fun deserialize(decoder: Decoder): Any {
        val jsonDecoder = decoder as JsonDecoder
        val element = jsonDecoder.decodeJsonElement()

        return deserializeJsonElement(element)
    }

    private fun deserializeJsonElement(element: JsonElement): Any = when (element) {
        is JsonObject -> {
            element.mapValues { deserializeJsonElement(it.value) }
        }
        is JsonArray -> {
            element.map { deserializeJsonElement(it) }
        }
        is JsonPrimitive -> element.content
    }
}
