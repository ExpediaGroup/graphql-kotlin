package com.expediagroup.graphql.generated.scalars

import com.expediagroup.graphql.plugin.client.generator.UUIDScalarConverter
import java.util.UUID
import kotlin.Unit
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind.STRING
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.jsonPrimitive

public object UUIDSerializer : KSerializer<UUID> {
  private val converter: UUIDScalarConverter = UUIDScalarConverter()

  public override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("UUID", STRING)

  public override fun serialize(encoder: Encoder, `value`: UUID): Unit {
    val encoded = converter.toJson(value)
    encoder.encodeString(encoded.toString())
  }

  public override fun deserialize(decoder: Decoder): UUID {
    val jsonDecoder = decoder as JsonDecoder
    val element = jsonDecoder.decodeJsonElement()
    val rawContent = element.jsonPrimitive.content
    return converter.toScalar(rawContent)
  }
}
