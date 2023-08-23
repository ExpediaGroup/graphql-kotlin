package com.expediagroup.graphql.generated.scalars

import com.expediagroup.graphql.client.Generated
import com.expediagroup.graphql.client.serialization.types.OptionalInput
import com.expediagroup.graphql.client.serialization.types.OptionalInput.Defined
import com.expediagroup.graphql.client.serialization.types.OptionalInput.Undefined
import com.expediagroup.graphql.generated.inputs.ComplexArgumentInput
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.nullable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Generated
public object OptionalComplexArgumentInputSerializer :
    KSerializer<OptionalInput<ComplexArgumentInput>> {
  private val `delegate`: KSerializer<ComplexArgumentInput> = ComplexArgumentInput.serializer()

  override val descriptor: SerialDescriptor =
      buildClassSerialDescriptor("OptionalComplexArgumentInputSerializer")

  override fun serialize(encoder: Encoder, `value`: OptionalInput<ComplexArgumentInput>) {
    when (value) {
      is OptionalInput.Undefined -> return
      is OptionalInput.Defined<ComplexArgumentInput> ->
        encoder.encodeNullableSerializableValue(delegate, value.value)
    }
  }

  /**
   * undefined is only supported during client serialization, this code should never be invoked
   */
  override fun deserialize(decoder: Decoder): OptionalInput<ComplexArgumentInput> =
      OptionalInput.Defined(decoder.decodeNullableSerializableValue(delegate.nullable))
}
