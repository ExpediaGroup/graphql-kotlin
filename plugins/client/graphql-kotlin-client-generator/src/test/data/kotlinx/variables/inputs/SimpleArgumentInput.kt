package com.expediagroup.graphql.generated.inputs

import com.expediagroup.graphql.client.Generated
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
  public val max: Double? = null,
  /**
   * Minimum value for test criteria
   */
  public val min: Double? = null,
  /**
   * New value to be set
   */
  public val newName: String? = null
)
