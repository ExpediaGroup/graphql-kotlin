package com.expediagroup.graphql.generated.scalars

import com.expediagroup.graphql.client.Generated
import com.expediagroup.graphql.client.serialization.types.OptionalInput
import com.expediagroup.graphql.client.serialization.types.OptionalInput.Defined
import com.expediagroup.graphql.client.serialization.types.OptionalInput.Undefined
import java.util.UUID
import kotlin.Unit
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.nullable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Generated
public object OptionalUUIDSerializer : KSerializer<OptionalInput<UUID>> {
  private val `delegate`: KSerializer<UUID> = UUIDSerializer

  public override val descriptor: SerialDescriptor =
      buildClassSerialDescriptor("OptionalUUIDSerializer")

  public override fun serialize(encoder: Encoder, `value`: OptionalInput<UUID>): Unit {
    when (value) {
      is OptionalInput.Undefined -> return
      is OptionalInput.Defined<UUID> ->
        encoder.encodeNullableSerializableValue(delegate, value.value)
    }
  }

  /**
   * undefined is only supported during client serialization, this code should never be invoked
   */
  public override fun deserialize(decoder: Decoder): OptionalInput<UUID> =
      OptionalInput.Defined(decoder.decodeNullableSerializableValue(delegate.nullable))
}
