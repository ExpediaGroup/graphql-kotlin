package com.expediagroup.graphql.generated.nestedquery

import kotlin.String
import kotlin.collections.List
import kotlinx.serialization.Serializable

/**
 * Example of an object self-referencing itself
 */
@Serializable
data class NestedObject2(
  /**
   * Name of the object
   */
  val name: String,
  /**
   * Children elements
   */
  val children: List<NestedObject3>
)
