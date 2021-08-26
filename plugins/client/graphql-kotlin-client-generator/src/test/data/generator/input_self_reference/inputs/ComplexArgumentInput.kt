package com.expediagroup.graphql.generated.inputs

import com.expediagroup.graphql.client.Generated
import kotlin.Float

/**
 * Self referencing input object
 */
@Generated
public data class ComplexArgumentInput(
  /**
   * Maximum value for test criteria
   */
  public val max: Float? = null,
  /**
   * Minimum value for test criteria
   */
  public val min: Float? = null,
  /**
   * Next criteria
   */
  public val next: ComplexArgumentInput? = null
)
