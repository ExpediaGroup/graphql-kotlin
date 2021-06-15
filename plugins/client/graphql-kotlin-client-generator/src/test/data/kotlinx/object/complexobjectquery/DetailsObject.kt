package com.expediagroup.graphql.generated.complexobjectquery

import kotlin.Boolean
import kotlin.Int
import kotlin.String
import kotlinx.serialization.Serializable

/**
 * Inner type object description
 */
@Serializable
public data class DetailsObject(
  /**
   * Unique identifier
   */
  public val id: Int,
  /**
   * Boolean flag
   */
  public val flag: Boolean,
  /**
   * Actual detail value
   */
  public val `value`: String
)
