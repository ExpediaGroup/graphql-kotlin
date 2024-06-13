package com.expediagroup.graphql.generated.complexobjectquery

import com.expediagroup.graphql.client.Generated
import com.fasterxml.jackson.`annotation`.JsonProperty
import kotlin.Boolean
import kotlin.Int
import kotlin.String

/**
 * Inner type object description
 */
@Generated
public data class DetailsObject(
  /**
   * Unique identifier
   */
  @get:JsonProperty(value = "id")
  public val id: Int,
  /**
   * Boolean flag
   */
  @get:JsonProperty(value = "flag")
  public val flag: Boolean,
  /**
   * Actual detail value
   */
  @get:JsonProperty(value = "value")
  public val `value`: String,
)
