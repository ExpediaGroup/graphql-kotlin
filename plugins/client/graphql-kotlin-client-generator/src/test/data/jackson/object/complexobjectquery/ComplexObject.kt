package com.expediagroup.graphql.generated.complexobjectquery

import kotlin.Int
import kotlin.String

/**
 * Multi line description of a complex type.
 * This is a second line of the paragraph.
 * This is final line of the description.
 */
data class ComplexObject(
  /**
   * Some unique identifier
   */
  val id: Int,
  /**
   * Some object name
   */
  val name: String,
  /**
   * Optional value
   * Second line of the description
   */
  val optional: String?,
  /**
   * Some additional details
   */
  val details: DetailsObject
)
