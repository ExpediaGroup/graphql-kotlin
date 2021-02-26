package com.expediagroup.graphql.generated.nestedquery

import kotlin.Int
import kotlin.String
import kotlinx.serialization.Serializable

/**
 * Example of an object self-referencing itself
 */
@Serializable
data class NestedObject4(
  /**
   * Unique identifier
   */
  val id: Int,
  /**
   * Name of the object
   */
  val name: String
)
