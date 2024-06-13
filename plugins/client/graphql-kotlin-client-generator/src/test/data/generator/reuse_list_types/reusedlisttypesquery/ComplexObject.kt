package com.expediagroup.graphql.generated.reusedlisttypesquery

import com.expediagroup.graphql.client.Generated
import com.fasterxml.jackson.`annotation`.JsonProperty
import kotlin.Int
import kotlin.String
import kotlin.collections.List

/**
 * Multi line description of a complex type.
 * This is a second line of the paragraph.
 * This is final line of the description.
 */
@Generated
public data class ComplexObject(
  /**
   * Some unique identifier
   */
  @get:JsonProperty(value = "id")
  public val id: Int,
  /**
   * Some object name
   */
  @get:JsonProperty(value = "name")
  public val name: String,
  /**
   * List of objects
   */
  @get:JsonProperty(value = "basicList")
  public val basicList: List<BasicObject>,
)
