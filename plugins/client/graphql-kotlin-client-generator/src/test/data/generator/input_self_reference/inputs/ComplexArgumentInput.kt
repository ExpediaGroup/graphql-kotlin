package com.expediagroup.graphql.generated.inputs

import com.expediagroup.graphql.client.Generated
import com.fasterxml.jackson.`annotation`.JsonProperty
import kotlin.Double

/**
 * Self referencing input object
 */
@Generated
public data class ComplexArgumentInput(
  /**
   * Maximum value for test criteria
   */
  @get:JsonProperty(value = "max")
  public val max: Double? = null,
  /**
   * Minimum value for test criteria
   */
  @get:JsonProperty(value = "min")
  public val min: Double? = null,
  /**
   * Next criteria
   */
  @get:JsonProperty(value = "next")
  public val next: ComplexArgumentInput? = null,
)
