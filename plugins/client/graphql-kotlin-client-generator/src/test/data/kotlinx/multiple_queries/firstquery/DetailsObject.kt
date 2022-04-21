package com.expediagroup.graphql.generated.firstquery

import com.expediagroup.graphql.client.Generated
import kotlin.Boolean
import kotlin.Int
import kotlin.String
import kotlinx.serialization.Serializable

/**
 * Inner type object description
 */
@Generated
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
  public val `value`: String,
)
