package com.expediagroup.graphql.generated.inputs

import kotlin.Float
import kotlinx.serialization.Serializable

/**
 * Self referencing input object
 */
@Serializable
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
