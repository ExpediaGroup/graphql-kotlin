package com.expediagroup.graphql.generated.inputs

import kotlin.Float

/**
 * Self referencing input object
 */
data class ComplexArgumentInput(
  /**
   * Maximum value for test criteria
   */
  val max: Float? = null,
  /**
   * Minimum value for test criteria
   */
  val min: Float? = null,
  /**
   * Next criteria
   */
  val next: ComplexArgumentInput? = null
)
