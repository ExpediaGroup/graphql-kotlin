package com.expediagroup.graphql.generated.inputs

import com.expediagroup.graphql.client.Generated
import com.expediagroup.graphql.client.serialization.types.OptionalInput
import com.expediagroup.graphql.client.serialization.types.OptionalInput.Undefined
import kotlin.Double
import kotlin.String
import kotlinx.serialization.Serializable

/**
 * Test input object
 */
@Generated
@Serializable
public data class SimpleArgumentInput(
  /**
   * Maximum value for test criteria
   */
  public val max: OptionalInput<Double> = OptionalInput.Undefined,
  /**
   * Minimum value for test criteria
   */
  public val min: OptionalInput<Double> = OptionalInput.Undefined,
  /**
   * New value to be set
   */
  public val newName: OptionalInput<String> = OptionalInput.Undefined
)
