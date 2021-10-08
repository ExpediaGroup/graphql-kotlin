package com.expediagroup.graphql.generated.scalars

import com.expediagroup.graphql.client.Generated
import com.expediagroup.graphql.plugin.client.generator.ULocaleScalarConverter
import com.ibm.icu.util.ULocale
import kotlin.Unit
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind.STRING
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.jsonPrimitive

@Generated
public object ULocaleSerializer : KSerializer<ULocale> {
  private val converter: ULocaleScalarConverter = ULocaleScalarConverter()

  public override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("ULocale", STRING)

  public override fun serialize(encoder: Encoder, `value`: ULocale): Unit {
    val encoded = converter.toJson(value)
    encoder.encodeString(encoded.toString())
  }

  public override fun deserialize(decoder: Decoder): ULocale {
    val jsonDecoder = decoder as JsonDecoder
    val element = jsonDecoder.decodeJsonElement()
    val rawContent = element.jsonPrimitive.content
    return converter.toScalar(rawContent)
  }
}
