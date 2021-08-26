package com.expediagroup.graphql.generated.nestedquery

import com.expediagroup.graphql.client.Generated
import kotlin.Int
import kotlin.String
import kotlin.collections.List

/**
 * Example of an object self-referencing itself
 */
@Generated
public data class NestedObject(
  /**
   * Unique identifier
   */
  public val id: Int,
  /**
   * Name of the object
   */
  public val name: String,
  /**
   * Children elements
   */
  public val children: List<NestedObject2>
)
