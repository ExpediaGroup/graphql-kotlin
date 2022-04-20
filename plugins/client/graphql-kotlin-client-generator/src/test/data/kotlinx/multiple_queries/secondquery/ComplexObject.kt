package com.expediagroup.graphql.generated.secondquery

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
)
