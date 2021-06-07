package com.expediagroup.graphql.generated.scalars

import com.expediagroup.graphql.plugin.client.generator.UUIDScalarConverter
import kotlin.Unit
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind.STRING
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.jsonPrimitive

/**
 * Custom scalar representing UUID
 */
@Serializable(with = UUIDSerializer::class)
public data class UUID(
  public val `value`: java.util.UUID
)

public class UUIDSerializer : KSerializer<UUID> {
  private val converter: UUIDScalarConverter = UUIDScalarConverter()

  public override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("UUID", STRING)

  public override fun serialize(encoder: Encoder, `value`: UUID): Unit {
    val encoded = converter.toJson(value.value)
    encoder.encodeString(encoded.toString())
  }

  public override fun deserialize(decoder: Decoder): UUID {
    val jsonDecoder = decoder as JsonDecoder
    val element = jsonDecoder.decodeJsonElement()
    val rawContent = element.jsonPrimitive.content
    return UUID(value = converter.toScalar(rawContent))
  }
}
