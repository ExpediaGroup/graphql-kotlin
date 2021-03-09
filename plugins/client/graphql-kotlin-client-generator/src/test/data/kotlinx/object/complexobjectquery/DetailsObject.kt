package com.expediagroup.graphql.generated.complexobjectquery

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
   * Boolean flag
   */
  val flag: Boolean,
  /**
   * Actual detail value
   */
  val value: String
)
