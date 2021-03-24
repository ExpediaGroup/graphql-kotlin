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

package com.expediagroup.graphql.client.serialization.data.scalars

import com.expediagroup.graphql.client.converter.ScalarConverter
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.jsonPrimitive

@Serializable(with = UUIDSerializer::class)
data class UUID(
    val value: java.util.UUID
)

class UUIDSerializer : KSerializer<UUID> {
    private val converter: UUIDScalarConverter = UUIDScalarConverter()

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("UUID", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: UUID) {
        val encoded = converter.toJson(value.value)
        encoder.encodeString(encoded.toString())
    }

    override fun deserialize(decoder: Decoder): UUID {
        val jsonDecoder = decoder as JsonDecoder
        val element = jsonDecoder.decodeJsonElement()
        val rawContent = element.jsonPrimitive.content
        return UUID(value = converter.toScalar(rawContent))
    }
}

// scalar converter would not be part of the generated sources
class UUIDScalarConverter : ScalarConverter<java.util.UUID> {
    override fun toScalar(rawValue: Any): java.util.UUID = java.util.UUID.fromString(rawValue.toString())
    override fun toJson(value: java.util.UUID): String = value.toString()
}
