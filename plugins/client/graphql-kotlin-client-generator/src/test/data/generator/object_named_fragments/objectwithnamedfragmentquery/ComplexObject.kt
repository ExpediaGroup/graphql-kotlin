package com.expediagroup.graphql.generated.objectwithnamedfragmentquery

import com.expediagroup.graphql.client.Generated
import com.fasterxml.jackson.`annotation`.JsonProperty
import kotlin.Int
import kotlin.String

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
   * Some additional details
   */
  @get:JsonProperty(value = "details")
  public val details: DetailsObject,
)
