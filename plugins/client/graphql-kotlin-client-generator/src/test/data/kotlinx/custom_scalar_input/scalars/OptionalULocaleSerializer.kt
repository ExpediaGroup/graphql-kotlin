package com.expediagroup.graphql.generated.scalars

import com.expediagroup.graphql.client.Generated
import com.expediagroup.graphql.client.serialization.types.OptionalInput
import com.expediagroup.graphql.client.serialization.types.OptionalInput.Defined
import com.expediagroup.graphql.client.serialization.types.OptionalInput.Undefined
import com.ibm.icu.util.ULocale
import kotlin.Unit
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.nullable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Generated
public object OptionalULocaleSerializer : KSerializer<OptionalInput<ULocale>> {
  private val `delegate`: KSerializer<ULocale> = ULocaleSerializer

  public override val descriptor: SerialDescriptor =
      buildClassSerialDescriptor("OptionalULocaleSerializer")

  public override fun serialize(encoder: Encoder, `value`: OptionalInput<ULocale>): Unit {
    when (value) {
      is OptionalInput.Undefined -> return
      is OptionalInput.Defined<ULocale> ->
        encoder.encodeNullableSerializableValue(delegate, value.value)
    }
  }

  /**
   * undefined is only supported during client serialization, this code should never be invoked
   */
  public override fun deserialize(decoder: Decoder): OptionalInput<ULocale> =
      OptionalInput.Defined(decoder.decodeNullableSerializableValue(delegate.nullable))
}
