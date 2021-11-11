package com.expediagroup.graphql.generated.inputs

import com.expediagroup.graphql.client.Generated
import com.expediagroup.graphql.client.serialization.types.OptionalInput
import com.expediagroup.graphql.client.serialization.types.OptionalInput.Undefined
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
  public val max: OptionalInput<Double> = OptionalInput.Undefined,
  /**
   * Minimum value for test criteria
   */
  public val min: OptionalInput<Double> = OptionalInput.Undefined,
  /**
   * Next criteria
   */
  public val next: OptionalInput<ComplexArgumentInput> = OptionalInput.Undefined
)
