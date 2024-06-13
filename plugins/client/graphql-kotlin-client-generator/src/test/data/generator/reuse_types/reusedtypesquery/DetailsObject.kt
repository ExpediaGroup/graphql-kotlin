package com.expediagroup.graphql.generated.reusedtypesquery

import com.expediagroup.graphql.client.Generated
import com.fasterxml.jackson.`annotation`.JsonProperty
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
   * Actual detail value
   */
  @get:JsonProperty(value = "value")
  public val `value`: String,
)
