package com.expediagroup.graphql.generated.inputs

import com.expediagroup.graphql.client.Generated
import kotlin.Double

/**
 * Self referencing input object
 */
@Generated
public data class ComplexArgumentInput(
  /**
   * Maximum value for test criteria
   */
  public val max: Double? = null,
  /**
   * Minimum value for test criteria
   */
  public val min: Double? = null,
  /**
   * Next criteria
   */
  public val next: ComplexArgumentInput? = null
)
