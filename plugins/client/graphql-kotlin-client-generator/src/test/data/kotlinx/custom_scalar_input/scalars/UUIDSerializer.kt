package com.expediagroup.graphql.generated.scalars

import com.expediagroup.graphql.client.Generated
import com.expediagroup.graphql.plugin.client.generator.UUIDScalarConverter
import java.util.UUID
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.serializerOrNull

@Generated
public object UUIDSerializer : KSerializer<UUID> {
  private val converter: UUIDScalarConverter = UUIDScalarConverter()

  override val descriptor: SerialDescriptor = buildClassSerialDescriptor("UUID")

  override fun serialize(encoder: Encoder, `value`: UUID) {
    val encoded = converter.toJson(value)
    val serializer = serializerOrNull(encoded::class.java)
    if (serializer != null) {
      encoder.encodeSerializableValue(serializer, encoded)
    } else {
      encoder.encodeString(encoded.toString())
    }
  }

  override fun deserialize(decoder: Decoder): UUID {
    val jsonDecoder = decoder as JsonDecoder
    val rawContent: Any = when (val element = jsonDecoder.decodeJsonElement()) {
      is JsonPrimitive -> element.jsonPrimitive.content
      else -> element
    }
    return converter.toScalar(rawContent)
  }
}
