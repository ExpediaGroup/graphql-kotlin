package com.expediagroup.graphql.generated.inputs

import kotlin.Float
import kotlinx.serialization.Serializable

/**
 * Self referencing input object
 */
@Serializable
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
