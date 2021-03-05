package com.expediagroup.graphql.client.serialization.types.data.scalars

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
