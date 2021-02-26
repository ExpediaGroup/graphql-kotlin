package com.expediagroup.graphql.generated.reusedlisttypesquery

import kotlin.Int
import kotlin.collections.List
import kotlinx.serialization.Serializable

/**
 * Multi line description of a complex type.
 * This is a second line of the paragraph.
 * This is final line of the description.
 */
@Serializable
data class ComplexObject3(
  /**
   * Some unique identifier
   */
  val id: Int,
  /**
   * List of objects
   */
  val basicList: List<BasicObject3>
)
