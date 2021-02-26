package com.expediagroup.graphql.generated.differentsubselectionquery

import kotlin.Boolean
import kotlin.Int
import kotlin.String
import kotlinx.serialization.Serializable

/**
 * Inner type object description
 */
@Serializable
data class DetailsObject(
  /**
   * Unique identifier
   */
  val id: Int,
  /**
   * Actual detail value
   */
  val value: String,
  /**
   * Boolean flag
   */
  val flag: Boolean
)
