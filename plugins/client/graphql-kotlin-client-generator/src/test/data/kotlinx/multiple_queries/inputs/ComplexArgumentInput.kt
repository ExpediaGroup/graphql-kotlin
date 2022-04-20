package com.expediagroup.graphql.generated.inputs

import com.expediagroup.graphql.client.Generated
import com.expediagroup.graphql.client.serialization.serializers.OptionalScalarSerializer
import com.expediagroup.graphql.client.serialization.types.OptionalInput
import com.expediagroup.graphql.client.serialization.types.OptionalInput.Undefined
import com.expediagroup.graphql.generated.scalars.OptionalComplexArgumentInputSerializer
import kotlin.Double
import kotlinx.serialization.Serializable

/**
 * Self referencing input object
 */
@Generated
@Serializable
public data class ComplexArgumentInput(
  /**
   * Maximum value for test criteria
   */
  @Serializable(with = OptionalScalarSerializer::class)
  public val max: OptionalInput<Double> = OptionalInput.Undefined,
  /**
   * Minimum value for test criteria
   */
  @Serializable(with = OptionalScalarSerializer::class)
  public val min: OptionalInput<Double> = OptionalInput.Undefined,
  /**
   * Next criteria
   */
  @Serializable(with = OptionalComplexArgumentInputSerializer::class)
  public val next: OptionalInput<ComplexArgumentInput> = OptionalInput.Undefined,
)
