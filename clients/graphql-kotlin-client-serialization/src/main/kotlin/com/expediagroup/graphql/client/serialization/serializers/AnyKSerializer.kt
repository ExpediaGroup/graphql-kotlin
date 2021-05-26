/*
 * Copyright 2021 Expedia, Inc
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

package com.expediagroup.graphql.client.serialization.serializers

import com.expediagroup.graphql.client.serialization.types.OptionalInput
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

/**
 * Generic KSerializer that can serialize/deserialize Any object.
 *
 * This is a workaround to kotlinx.serialization limitation that cannot process arbitrary maps (required for handling extensions in GraphQL responses) as
 * it relies on compiler to generate appropriate serializers and cannot determine appropriate one to process generic Any object at runtime.
 *
 * During serialization AnyKSerializer relies on Kotlin reflections to find all object member properties.
 *
 * JsonDecoder always represents underlying primitive JSON elements as String with additional flag specifying whether it was a String or not. This means
 * that we have to explicitly attempt to convert the Strings to appropriate types.
 *
 * @see [issue 296](https://github.com/Kotlin/kotlinx.serialization/issues/296) and [issue 746](https://github.com/Kotlin/kotlinx.serialization/issues/746)
 */
object AnyKSerializer : KSerializer<Any?> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("Any")

    override fun serialize(encoder: Encoder, value: Any?) {
        serializeAny(value)?.let {
            val jsonEncoder = encoder as JsonEncoder
            jsonEncoder.encodeJsonElement(it)
        }
    }

    private fun serializeAny(value: Any?): JsonElement? = when (value) {
        null -> JsonNull
        is OptionalInput.Undefined -> null
        is OptionalInput.Defined<*> -> serializeAny(value.value)
        is Map<*, *> -> {
            val mapContents = value.mapNotNull { (key, value) ->
                serializeAny(value)?.let {
                    key.toString() to it
                }
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
        else -> {
            val contents = value::class.memberProperties.mapNotNull { property ->
                serializeAny(property.getter.call(value))?.let {
                    property.name to it
                }
            }.toMap()
            JsonObject(contents)
        }
    }

    override fun deserialize(decoder: Decoder): Any? {
        val jsonDecoder = decoder as JsonDecoder
        val element = jsonDecoder.decodeJsonElement()
        return deserializeJsonElement(element)
    }

    private fun deserializeJsonElement(element: JsonElement): Any? = when (element) {
        is JsonNull -> null
        is JsonObject -> {
            element.mapValues { deserializeJsonElement(it.value) }
        }
        is JsonArray -> {
            element.map { deserializeJsonElement(it) }
        }
        is JsonPrimitive -> when {
            element.isString -> element.content
            element.content == "true" -> true
            element.content == "false" -> false
            else -> {
                element.content.toIntOrNull() ?: element.content.toLongOrNull() ?: element.content.toDoubleOrNull() ?: element.content
            }
        }
    }
}
