package com.expediagroup.graphql.generated.reusedlisttypesquery

import com.expediagroup.graphql.client.Generated
import com.fasterxml.jackson.`annotation`.JsonProperty
import kotlin.Int
import kotlin.String

/**
 * Some basic description
 */
@Generated
public data class BasicObject(
  @get:JsonProperty(value = "id")
  public val id: Int,
  /**
   * Object name
   */
  @get:JsonProperty(value = "name")
  public val name: String,
)
