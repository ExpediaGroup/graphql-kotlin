package com.expediagroup.graphql.generated.inputs

import com.expediagroup.graphql.client.Generated
import com.expediagroup.graphql.client.jackson.types.OptionalInput
import com.expediagroup.graphql.client.jackson.types.OptionalInput.Undefined
import kotlin.Float
import kotlin.String

/**
 * Test input object
 */
@Generated
public data class SimpleArgumentInput(
  /**
   * Maximum value for test criteria
   */
  public val max: OptionalInput<Float> = OptionalInput.Undefined,
  /**
   * Minimum value for test criteria
   */
  public val min: OptionalInput<Float> = OptionalInput.Undefined,
  /**
   * New value to be set
   */
  public val newName: OptionalInput<String> = OptionalInput.Undefined
)
