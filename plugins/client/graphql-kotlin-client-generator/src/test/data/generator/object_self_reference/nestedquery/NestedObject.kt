package com.expediagroup.graphql.generated.nestedquery

import com.expediagroup.graphql.client.Generated
import com.fasterxml.jackson.`annotation`.JsonProperty
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
  @get:JsonProperty(value = "id")
  public val id: Int,
  /**
   * Name of the object
   */
  @get:JsonProperty(value = "name")
  public val name: String,
  /**
   * Children elements
   */
  @get:JsonProperty(value = "children")
  public val children: List<NestedObject2>,
)
