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

package com.expediagroup.graphql.client.serialization.data.scalars

import com.expediagroup.graphql.client.converter.ScalarConverter
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.serializerOrNull

object JsonObjectSerializer : KSerializer<JsonObject> {
    private val converter: AnyScalarConverter = AnyScalarConverter()

    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("JsonObject")

    override fun serialize(encoder: Encoder, `value`: JsonObject) {
        val encoded = converter.toJson(value)
        val serializer = serializerOrNull(encoded::class.java)
        if (serializer != null) {
            encoder.encodeSerializableValue(serializer, encoded)
        } else {
            encoder.encodeString(encoded.toString())
        }
    }

    override fun deserialize(decoder: Decoder): JsonObject {
        val jsonDecoder = decoder as JsonDecoder
        val rawContent: Any = when (val element = jsonDecoder.decodeJsonElement()) {
            is JsonPrimitive -> element.jsonPrimitive.content
            else -> element
        }
        return converter.toScalar(rawContent)
    }
}

// scalar converter would not be part of the generated sources
class AnyScalarConverter : ScalarConverter<JsonObject> {
    override fun toScalar(rawValue: Any): JsonObject = Json.parseToJsonElement(rawValue.toString()).jsonObject
    override fun toJson(value: JsonObject): Any = value
}
