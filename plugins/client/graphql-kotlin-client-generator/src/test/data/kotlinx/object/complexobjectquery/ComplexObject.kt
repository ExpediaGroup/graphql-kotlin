package com.expediagroup.graphql.generated.complexobjectquery

import com.expediagroup.graphql.client.Generated
import kotlin.Int
import kotlin.String
import kotlinx.serialization.Serializable

/**
 * Multi line description of a complex type.
 * This is a second line of the paragraph.
 * This is final line of the description.
 */
@Generated
@Serializable
public data class ComplexObject(
  /**
   * Some unique identifier
   */
  public val id: Int,
  /**
   * Some object name
   */
  public val name: String,
  /**
   * Optional value
   * Second line of the description
   */
  public val optional: String?,
  /**
   * Some additional details
   */
  public val details: DetailsObject
)
