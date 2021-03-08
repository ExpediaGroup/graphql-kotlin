package com.expediagroup.graphql.generated.inputs

import kotlin.Float
import kotlin.String
import kotlinx.serialization.Serializable

/**
 * Test input object
 */
@Serializable
data class SimpleArgumentInput(
  /**
   * Maximum value for test criteria
   */
  val max: Float? = null,
  /**
   * Minimum value for test criteria
   */
  val min: Float? = null,
  /**
   * New value to be set
   */
  val newName: String? = null
)
