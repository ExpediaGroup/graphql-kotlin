package com.expediagroup.graphql.generated.reusedlisttypesquery

import kotlin.Int
import kotlin.String
import kotlin.collections.List
import kotlinx.serialization.Serializable

/**
 * Multi line description of a complex type.
 * This is a second line of the paragraph.
 * This is final line of the description.
 */
@Serializable
data class ComplexObject2(
  /**
   * Some unique identifier
   */
  val id: Int,
  /**
   * Some object name
   */
  val name: String,
  /**
   * List of objects
   */
  val basicList: List<BasicObject2>
)
