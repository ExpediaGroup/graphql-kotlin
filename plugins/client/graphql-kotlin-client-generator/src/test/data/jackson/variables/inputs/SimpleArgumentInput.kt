package com.expediagroup.graphql.generated.inputs

import com.expediagroup.graphql.client.Generated
import com.expediagroup.graphql.client.jackson.types.OptionalInput
import com.expediagroup.graphql.client.jackson.types.OptionalInput.Undefined
import com.fasterxml.jackson.`annotation`.JsonProperty
import kotlin.Double
import kotlin.String

/**
 * Test input object
 */
@Generated
public data class SimpleArgumentInput(
  /**
   * Maximum value for test criteria
   */
  @get:JsonProperty(value = "max")
  public val max: OptionalInput<Double> = OptionalInput.Undefined,
  /**
   * Minimum value for test criteria
   */
  @get:JsonProperty(value = "min")
  public val min: OptionalInput<Double> = OptionalInput.Undefined,
  /**
   * New value to be set
   */
  @get:JsonProperty(value = "newName")
  public val newName: OptionalInput<String> = OptionalInput.Undefined,
)
