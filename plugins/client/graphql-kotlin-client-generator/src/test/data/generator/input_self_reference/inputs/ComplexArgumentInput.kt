package com.expediagroup.graphql.generated.inputs

import com.expediagroup.graphql.client.jackson.types.OptionalInput
import com.expediagroup.graphql.client.jackson.types.OptionalInput.Undefined
import kotlin.Float

/**
 * Self referencing input object
 */
public data class ComplexArgumentInput(
  /**
   * Maximum value for test criteria
   */
  public val max: OptionalInput<Float> = OptionalInput.Undefined,
  /**
   * Minimum value for test criteria
   */
  public val min: OptionalInput<Float> = OptionalInput.Undefined,
  /**
   * Next criteria
   */
  public val next: OptionalInput<ComplexArgumentInput> = OptionalInput.Undefined
)
