package com.expediagroup.graphql.generated.nestedquery

import kotlin.Int
import kotlin.String
import kotlin.collections.List

/**
 * Example of an object self-referencing itself
 */
data class NestedObject(
  /**
   * Unique identifier
   */
  val id: Int,
  /**
   * Name of the object
   */
  val name: String,
  /**
   * Children elements
   */
  val children: List<NestedObject2>
)
