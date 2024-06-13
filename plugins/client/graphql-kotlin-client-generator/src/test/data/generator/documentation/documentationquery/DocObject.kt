package com.expediagroup.graphql.generated.documentationquery

import com.expediagroup.graphql.client.Generated
import com.fasterxml.jackson.`annotation`.JsonProperty
import kotlin.Int

/**
 * Doc object with % and $ floating around
 */
@Generated
public data class DocObject(
  /**
   * An id with a comment containing % and $ as well
   */
  @get:JsonProperty(value = "id")
  public val id: Int,
)
